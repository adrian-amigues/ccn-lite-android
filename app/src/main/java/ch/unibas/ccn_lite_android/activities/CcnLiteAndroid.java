package ch.unibas.ccn_lite_android.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.unibas.ccn_lite_android.models.Area;
import ch.unibas.ccn_lite_android.adapters.AreasAdapter;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.fragments.RelayOptionsFragment;
import ch.unibas.ccn_lite_android.models.SensorReading;
import ch.unibas.ccn_lite_android.models.SensorReadingManager;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CcnLiteAndroid extends AppCompatActivity
        implements RelayOptionsFragment.NoticeDialogListener
{
    private String TAG = "unoise";
    private boolean useParallelTaskExecution = false; // native function androidPeek can't handle parallel executions

    private List<Area> areas;
    private SensorReadingManager sensorReadingManager;
    private AreasAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private AndroidPeekTaskCounter taskCounter = null;
    private SharedPreferences sharedPref;

    private Boolean useServiceRelay;
    private String externalIp;
    private String ccnSuite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize preferences
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        loadPreferences(sharedPref);

        // Initialize the swipe refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        initializeSwipeRefresh(swipeContainer);

        // Initialize the top bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        if(myToolbar != null) {
//            myToolbar.setOnMenuItemClickListener(new ToolbarMenuItemClickListener());
//        }

        areas = new ArrayList<>();
        sensorReadingManager = new SensorReadingManager();
        adapter = new AreasAdapter(areas, this);

        // Initialize the RecyclerView
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        if (rv != null) {
            rv.setLayoutManager(llm);
            rv.setAdapter(adapter);
        }

        initializeData();
    }

    @Override
    public void onStart() {
        super.onStart();
//        refresh();

//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                refresh(false);
//            }
//        }, 0, 10, SECONDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toast.makeText(CcnLiteAndroid.this, "Creating menu", Toast.LENGTH_SHORT).show();
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        if (ccnSuite.equals("ccnx2015")) {
            menu.findItem(R.id.menu_radio_item_ccnx).setChecked(true);
        } else if (ccnSuite.equals("ndn2013")) {
            menu.findItem(R.id.menu_radio_item_ndn).setChecked(true);
        }
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        Toast.makeText(CcnLiteAndroid.this, "Closing menu", Toast.LENGTH_SHORT).show();
//        openOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.menu_item_sync_sds:
                refresh(true);
                break;
            case R.id.menu_item_network_settings:
                DialogFragment dialog = new RelayOptionsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("externalIp", externalIp);
                bundle.putBoolean("useServiceRelay", useServiceRelay);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "dialog_network_settings");
                break;
            case R.id.menu_item_auto_refresh_checkbox:
                break;
            case R.id.menu_radio_item_ccnx:
                ccnSuite = "ccnx2015";
                prefEditor.putString(getString(R.string.pref_key_ccn_suite), ccnSuite);
                prefEditor.apply();
                break;
            case R.id.menu_radio_item_ndn:
                ccnSuite = "ndn2013";
                prefEditor.putString(getString(R.string.pref_key_ccn_suite), ccnSuite);
                prefEditor.apply();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * Loads the saved preferences into activity variables
     * @param sharedPref the preference file
     */
    private void loadPreferences(SharedPreferences sharedPref) {
        Resources res = getResources();
        ccnSuite = sharedPref.getString(res.getString(R.string.pref_key_ccn_suite),
                res.getString(R.string.default_ccn_suite));
        externalIp = sharedPref.getString(res.getString(R.string.pref_key_external_ip),
                res.getString(R.string.default_external_ip));
        useServiceRelay = sharedPref.getBoolean(res.getString(R.string.pref_key_use_service_relay),
                res.getBoolean(R.bool.default_use_service_relay));
    }

    /**
     * Initializes the areas array with data
     */
    private void initializeData() {
        areas.add(new Area("FooBar", "Mote 1", R.drawable.foobar, "/demo/mote1/"));
        areas.add(new Area("Uthgård", "Mote 2", R.drawable.uthgard, "/demo/mote2/"));
        areas.add(new Area("Rullan", "Mote 3", R.drawable.rullan, "/demo/mote3/"));
        adapter.notifyDataSetChanged();
    }

    /**
     * Initializes the swipe layout with a listener and colors
     * @param swipe the swipe layout
     */
    private void initializeSwipeRefresh(SwipeRefreshLayout swipe) {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(false);
            }
        });
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * The main way of getting data from the network.
     * refresh creates AndroidPeekTask for each area.
     */
    private void refresh(Boolean refreshSds) {
        String port = getString(R.string.port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;

        if (refreshSds) {
            String ret;
            int sdsPort = Integer.parseInt(getString(R.string.sds_port));
            ret = androidPeek(ccnSuite, targetIp, sdsPort, getString(R.string.sds_uri));
            Log.d(TAG, "SDS returned: "+ret);
            try {
                JSONArray jsonArray = new JSONArray(ret);
                String prefix = jsonArray.getJSONObject(0).getString("prefix");
                Log.d(TAG, "SDS prefix: "+prefix);
            } catch(org.json.JSONException e) {
                Log.e(TAG, "Unvalid Json: "+e);
            }
        }

        int areaCount = adapter.getItemCount();
        areaCount = 3;
        taskCounter = new AndroidPeekTaskCounter(areaCount);

        for (int i = 0; i < areaCount; i++) {
            String requestedURI = adapter.getURI(i);
            if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetIp, port, requestedURI, Integer.toString(i));
            } else {
                new AndroidPeekTask().execute(targetIp, port, requestedURI, Integer.toString(i));
            }
        }
    }

    /**
     * Cleans and returns the passed string by stripping it of all '\n' at its end
     * @param str the string to clean
     * @return the cleaned string
     */
    public String cleanResultString(String str) {
        if (str != null) {
            while (str.length() > 0 && str.charAt(str.length()-1)=='\n') {
                str = str.substring(0, str.length()-1);
            }
        }
        return str;
    }

    /**
     * Callback for the positive button click on the RelayOptions dialog
     * @param dialog the dialog whence the positive button was clicked
     */
    @Override
    public void onNetworkSettingsDialogPositiveClick(RelayOptionsFragment dialog) {
        useServiceRelay = dialog.getUseServiceRelay();
        externalIp = dialog.getExternalIp();
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(getString(R.string.pref_key_use_service_relay), useServiceRelay);
        prefEditor.putString(getString(R.string.pref_key_external_ip), externalIp);
        prefEditor.apply();
        if (useServiceRelay) {
            Toast.makeText(CcnLiteAndroid.this, "Now using service", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CcnLiteAndroid.this, "Now using "+externalIp, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Task in charge of using the jni androidPeek function.
     * calls androidPeek with passed parameters and treats the returned string
     */
    private class AndroidPeekTask extends AsyncTask<String, Void, String> {
        // position of the area in areas which will recieve the results
        private int areaPos;

        /**
         * calls androidPeek jni function with the received parameters
         * @param params parameters necesssary for the task
         * @return the answer from androidPeek, passing it to onPostExecute
         */
        protected String doInBackground(String... params) {
            String ipString = params[0];
            int portInt = Integer.parseInt(params[1]);
            String contentString = params[2];
            areaPos = Integer.parseInt(params[3]);

            return androidPeek(ccnSuite, ipString, portInt, contentString);
        }

        /**
         * Treats the returned string from androidPeek
         * @param result the returned string from androidPeek
         */
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute "+taskCounter.runningTasks+" result = " + result);
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
            taskCounter.taskFinished();
        }
    }

    /**
     * Keeps track of the number of tasks still being processed.
     * When all tasks are done it updates the data and the UI
     */
    public class AndroidPeekTaskCounter {
        private int runningTasks;

        AndroidPeekTaskCounter(int numberOfTasks) {
            this.runningTasks = numberOfTasks;
        }

        void taskFinished() {
            if (--runningTasks == 0) {
                swipeContainer.setRefreshing(false);
                adapter.sortAreas();
                adapter.resetExpandedPosition();
                adapter.notifyDataSetChanged();
            }
        }
    }

    /* this is used to load the 'ccn-lite-android' library on application startup */
    static {
        System.loadLibrary("ccn-lite-android");
    }
    public native String androidPeek(String suiteString, String ipString,
                                      int portString, String contentString);
}


