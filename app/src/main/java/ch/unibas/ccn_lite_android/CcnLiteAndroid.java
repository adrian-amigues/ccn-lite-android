package ch.unibas.ccn_lite_android;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.widget.Toast;

public class CcnLiteAndroid extends AppCompatActivity
        implements RelayOptionsFragment.NoticeDialogListener
{
    private String TAG = "unoise";
    private boolean useParallelTaskExecution = false; // native function androidPeek can't handle parallel executions

    private List<Area> areas;
    private SensorReadingManager sensorReadingManager;
    private AreasAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private WorkCounter wk = null;

    private final String localIp = "127.0.0.1";
    private final String port = "9695";
    private Boolean useServiceRelay = true;
    private String externalIp = "192.168.1.101";
    private String ccnSuite = "ndn2013";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(CcnLiteAndroid.this, "Menu item click", Toast.LENGTH_SHORT).show();
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.radio_item_ccnx:
                        ccnSuite = "ccnx2015";
                        return true;
                    case R.id.radio_item_ndn:
                        ccnSuite = "ndn2013";
                        return true;
                    case R.id.item_relay_options:
                        DialogFragment dialog = new RelayOptionsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("externalIp", externalIp);
                        bundle.putBoolean("useServiceRelay", useServiceRelay);
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), "dialog_relay_options");
                        return true;
                }
                return false;
            }
        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
//        rv.setHasFixedSize(true);
        areas = new ArrayList<>();
        sensorReadingManager = new SensorReadingManager();
        adapter = new AreasAdapter(areas, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        initializeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        if (ccnSuite.equals("ccnx2015")) {
            menu.findItem(R.id.radio_item_ccnx).setChecked(true);
        } else if (ccnSuite.equals("ndn2013")) {
            menu.findItem(R.id.radio_item_ndn).setChecked(true);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }


    private void initializeData() {
        areas.add(new Area("FooBar", "Eat in a basement - 35 Db", R.drawable.foobar, "/demo/mote1/"));
        areas.add(new Area("Uthgård", "They have sofas - 28 Db", R.drawable.uthgard, "/demo/mote2/"));
        areas.add(new Area("Rullan", "Expensive but nice - 32 Db", R.drawable.rullan, "/demo/mote3/"));
        adapter.notifyDataSetChanged();
    }

    public String cleanResultString(String str) {
        if (str != null) {
            while (str.length() > 0 && str.charAt(str.length()-1)=='\n') {
                str = str.substring(0, str.length()-1);
            }
        }
        return str;
    }

    private void refresh() {
        String targetIp = useServiceRelay ? localIp : externalIp;
        int areaCount = adapter.getItemCount();
        areaCount = 3;
        wk = new WorkCounter(areaCount);

        for (int i = 0; i < areaCount; i++) {
            String requestedURI = adapter.getURI(i);
            if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, externalIp, port, requestedURI, Integer.toString(i));
            } else {
                new AndroidPeekTask().execute(externalIp, port, requestedURI, Integer.toString(i));
            }
        }
    }

    @Override
    public void onDialogPositiveClick(RelayOptionsFragment dialog) {
        useServiceRelay = dialog.getUseServiceRelay();
        externalIp = dialog.getExternalIp();
        if (useServiceRelay) {
            Toast.makeText(CcnLiteAndroid.this, "Now using service", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CcnLiteAndroid.this, "Now using "+externalIp, Toast.LENGTH_SHORT).show();
        }
    }

    private class AndroidPeekTask extends AsyncTask<String, Void, String> {
        private int areaPos;
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected String doInBackground(String... params) {
            String ipString = params[0];
            int portInt = Integer.parseInt(params[1]);
            String contentString = params[2];
            areaPos = Integer.parseInt(params[3]);

            return androidPeek(ccnSuite, ipString, portInt, contentString);
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute "+wk.runningTasks+" result = " + result);
            if (SensorReading.isSensorReading(result)) {
                try {
                    SensorReading sr = new SensorReading(result);
                    sensorReadingManager.addSensorReading(sr);
                    adapter.updateValue(areaPos, sr);
                } catch(Exception e) {
                    result = "Corrupted SensorReading";
                    adapter.updateValue(areaPos, result);
                }
            } else {
                if (result.equals("\n")) {
                    result = "No data available";
                } else {
                    result = cleanResultString(result);
                }
                adapter.updateValue(areaPos, result);
            }
            wk.taskFinished();
        }
    }

//    Used to count the finished tasks when all the cards are refreshed
    public class WorkCounter {
        private int runningTasks;

        public WorkCounter(int numberOfTasks) {
            this.runningTasks = numberOfTasks;
        }
        // Only call this in onPostExecute!
        public void taskFinished() {
            if (--runningTasks == 0) {
                swipeContainer.setRefreshing(false);
                adapter.sortAreas();
                adapter.resetExpandedPosition();
                adapter.notifyDataSetChanged();
            }
        }
    }

    //    Native functions declarations
    public native String relayInit();
    public native String androidPeek(String suiteString, String ipString,
                                     int portString, String contentString);
    /* this is used to load the 'ccn-lite-android' library on application
     * startup. The library has already been unpacked into
     * /data/data/ch.unibas.ccnliteandroid/lib/libccn-lite-android.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("ccn-lite-android");
    }
}


