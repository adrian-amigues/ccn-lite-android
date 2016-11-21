package ch.unibas.ccn_lite_android.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.unibas.ccn_lite_android.fragments.NetworkSettingsFragment;
import ch.unibas.ccn_lite_android.models.Area;
import ch.unibas.ccn_lite_android.adapters.AreasAdapter;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.models.SensorReading;
import ch.unibas.ccn_lite_android.models.AreaManager;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CcnLiteAndroid extends AppCompatActivity
        implements NetworkSettingsFragment.NoticeDialogListener
{
    private String TAG = "unoise";
    private boolean useParallelTaskExecution = false; // native function androidPeek can't handle parallel executions

//    private List<Area> areas;
    private AreaManager areaManager;
    private AreasAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private AndroidPeekTaskCounter peekTaskCounter = null;
    private SharedPreferences sharedPref;
    private ScheduledFuture scheduledFuture;

    private Boolean useServiceRelay;
    private Boolean useAutoRefresh;
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

//        areas = new ArrayList<>();
        areaManager = new AreaManager();
        adapter = new AreasAdapter(areaManager, this);

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
        refreshSds();
        if (useAutoRefresh) {
            startAutoRefresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.menu_item_sync_sds:
                refreshSds();
                break;
            case R.id.menu_item_network_settings:
                DialogFragment dialog = new NetworkSettingsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.bundle_name_externalIp), externalIp);
                bundle.putString(getString(R.string.bundle_name_ccnSuite), ccnSuite);
                bundle.putBoolean(getString(R.string.bundle_name_useServiceRelay), useServiceRelay);
                bundle.putBoolean(getString(R.string.bundle_name_useAutoRefresh), useAutoRefresh);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "dialog_network_settings");
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
        useAutoRefresh = sharedPref.getBoolean(res.getString(R.string.pref_key_use_auto_refresh),
                res.getBoolean(R.bool.default_use_auto_refresh));
    }

    /**
     * Initializes the areas array with data
     */
    private void initializeData() {
        areaManager.addArea(new Area("FooBar", "Mote 1", R.drawable.foobar, "/demo/mote1/"));
//        areaManager.addArea(new Area("FooBar", "Mote 1", R.drawable.foobar, "/test"));
        areaManager.addArea(new Area("Uthgård", "Mote 2", R.drawable.uthgard, "/demo/mote2/"));
        areaManager.addArea(new Area("Rullan", "Mote 3", R.drawable.rullan, "/demo/mote3/"));
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
                refresh();
            }
        });
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
    public void onNetworkSettingsDialogPositiveClick(NetworkSettingsFragment dialog) {
        Boolean newUseAutoRefresh = dialog.getUseAutoRefresh();
        if (newUseAutoRefresh != useAutoRefresh) {
            if (newUseAutoRefresh) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        }
        useAutoRefresh = newUseAutoRefresh;
        useServiceRelay = dialog.getUseServiceRelay();
        externalIp = dialog.getExternalIp();
        ccnSuite = dialog.getCcnSuite();
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(getString(R.string.pref_key_use_service_relay), useServiceRelay);
        prefEditor.putBoolean(getString(R.string.pref_key_use_auto_refresh), useAutoRefresh);
        prefEditor.putString(getString(R.string.pref_key_external_ip), externalIp);
        prefEditor.putString(getString(R.string.pref_key_ccn_suite), ccnSuite);
        prefEditor.apply();
    }

    /**
     * Starts the automatic refreshing at a fixed rate
     */
    private void startAutoRefresh() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        int delay = getResources().getInteger(R.integer.auto_refresh_delay_seconds);
        scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                refresh();
            }
        }, delay, delay, SECONDS);
    }

    /**
     * Stops the automatic refreshing
     */
    private void stopAutoRefresh() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    /**
     * Retreives SDS data, creates a peekTask with a specific uri aiming at the SDS
     */
    private void refreshSds() {
        stopAutoRefresh();
        String port = getString(R.string.sds_port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;
        String uri = getString(R.string.sds_uri);

        if (peekTaskCounter != null && peekTaskCounter.getRunningTasks() > 0) {
            peekTaskCounter.setRunningTasks(1 + peekTaskCounter.getRunningTasks());
        } else {
            peekTaskCounter = new AndroidPeekTaskCounter(1, true);
        }
        Log.d(TAG, "refreshSds called");
        swipeContainer.setRefreshing(true);
        if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetIp, port, uri, null);
        } else {
            new AndroidPeekTask().execute(targetIp, port, uri, null);
        }
    }

    /**
     * Retreives data from the known sensors by creating multiple peekTasks
     */
    private void refresh() {
        String port = getString(R.string.port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;
        int cardCount = adapter.getItemCount();
//        cardCount = 3;

        if (peekTaskCounter != null && peekTaskCounter.getRunningTasks() > 0) {
            peekTaskCounter.setRunningTasks(cardCount + peekTaskCounter.getRunningTasks());
        } else {
            peekTaskCounter = new AndroidPeekTaskCounter(cardCount, false);
        }
        Log.d(TAG, "refresh called with "+cardCount+" URIs");
        swipeContainer.setRefreshing(true);
        for (int i = 0; i < cardCount; i++) {
            String requestedURI = adapter.getURI(i);
            if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetIp, port, requestedURI, Integer.toString(i));
            } else {
                new AndroidPeekTask().execute(targetIp, port, requestedURI, Integer.toString(i));
            }
        }
    }

    /**
     * Task in charge of using the jni androidPeek function.
     * calls androidPeek with passed parameters and treats the returned string
     */
    private class AndroidPeekTask extends AsyncTask<String, Void, String> {
        // position of the area in areas which will recieve the results
        private int areaPos = -1;

        /**
         * calls androidPeek jni function with the received parameters
         * @param params parameters necesssary for the task
         * @return the answer from androidPeek, passing it to onPostExecute
         */
        protected String doInBackground(String... params) {
            String ipString = params[0];
            int portInt = Integer.parseInt(params[1]);
            String contentString = params[2];
            if (params[3] != null) {
                areaPos = Integer.parseInt(params[3]);
            }
            Log.i(TAG, contentString+" ("+ccnSuite+") sent to "+ipString+" on port "+portInt);
            return androidPeek(ccnSuite, ipString, portInt, contentString);
        }

        /**
         * Treats the returned string from androidPeek
         * @param result the returned string from androidPeek
         */
        protected void onPostExecute(String result) {
            if (isJSONValid(result)) {
                Log.i(TAG, "onPostExecute SDS result = " + result);
                areaManager.updateFromSds(result);
            } else if (SensorReading.isSensorReading(result)) {
                Log.i(TAG, "onPostExecute "+peekTaskCounter.runningTasks+" sensor reading result = " + result);
                if (areaPos == -1) {
                    Log.e(TAG, "Sensor reading received but has no link to an area");
                } else {
                    try {
                        SensorReading sr = new SensorReading(result);
                        //                    areaManager.addSensorReading(sr);
                        adapter.updateValue(areaPos, sr);
                    } catch (Exception e) {
                        result = "Corrupted SensorReading";
                        adapter.updateValue(areaPos, result);
                        peekTaskCounter.taskFinished(true);
                    }
                }
            } else {
                Log.i(TAG, "onPostExecute "+peekTaskCounter.runningTasks+" unknown result = " + result);
                if (result.equals("\n")) {
                    result = "No data available";
                } else {
                    result = cleanResultString(result);
                }
                if (areaPos >= 0) {
                    adapter.updateValue(areaPos, result);
                }
                peekTaskCounter.taskFinished(false);
            }
        }
    }

    /**
     * Keeps track of the number of tasks still being processed.
     * When all tasks are done it updates the data and the UI
     */
    public class AndroidPeekTaskCounter {
        private int runningTasks;
        private boolean isSdsTask;

        AndroidPeekTaskCounter(int numberOfTasks, boolean isSdsTask) {
            this.runningTasks = numberOfTasks;
            this.isSdsTask = isSdsTask;
        }

        void taskFinished(Boolean validResult) {
            if (--runningTasks == 0) {
                if (isSdsTask) {
                    if (!validResult) {
                        Toast.makeText(CcnLiteAndroid.this, "SDS not found. Using old sensor info", Toast.LENGTH_LONG).show();
                    }
                    refresh();
                    if (useAutoRefresh) {
                        startAutoRefresh();
                    }
                } else {
                    adapter.sortAreas();
                    adapter.resetExpandedPosition();
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }
            }
        }
        public int getRunningTasks() {
            return runningTasks;
        }

        public void setRunningTasks(int runningTasks) {
            this.runningTasks = runningTasks;
        }
    }

    /* this is used to load the 'ccn-lite-android' library on application startup */
    static {
        System.loadLibrary("ccn-lite-android");
    }
    public native String androidPeek(String suiteString, String ipString,
                                      int portString, String contentString);

    /**
     * Returns true if the passed string is a valid JSON
     * @param test the string to test
     * @return true if the json string test is valid
     */
    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}


