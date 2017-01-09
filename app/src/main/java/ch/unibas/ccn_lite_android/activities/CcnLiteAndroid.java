package ch.unibas.ccn_lite_android.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.unibas.ccn_lite_android.models.DatabaseTable;
import ch.unibas.ccn_lite_android.fragments.NetworkSettingsFragment;
import ch.unibas.ccn_lite_android.helpers.Helper;
import ch.unibas.ccn_lite_android.models.Area;
import ch.unibas.ccn_lite_android.adapters.AreasAdapter;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.models.Prediction;
import ch.unibas.ccn_lite_android.models.Sensor;
import ch.unibas.ccn_lite_android.models.SensorReading;
import ch.unibas.ccn_lite_android.models.AreaManager;

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
//    private Boolean useAutoRefresh;
    private String externalIp;
    private String ccnSuite;
    Prediction prediction;
    SQLiteDatabase myDB= null;
    DatabaseTable dbTable;
    int ppp;
    static ImageView selectedImage;
    String predictionData = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = this.openOrCreateDatabase("uNoiseDatabase", MODE_PRIVATE, null);
        dbTable = new DatabaseTable(myDB);
        dbTable.createTable();

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
        areaManager = new AreaManager(this);
        adapter = new AreasAdapter(areaManager, this, dbTable);

        // Initialize the RecyclerView
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        if (rv != null) {
            rv.setLayoutManager(llm);
            rv.setAdapter(adapter);
        }
        adapter.setOnItemClickListener(new AreasAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                ppp=position;
            }
        });

        initializeData();
//        refreshSds();
//        if (useAutoRefresh) {
//            startAutoRefresh();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
//                bundle.putBoolean(getString(R.string.bundle_name_useAutoRefresh), useAutoRefresh);
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
//        useAutoRefresh = sharedPref.getBoolean(res.getString(R.string.pref_key_use_auto_refresh),
//                res.getBoolean(R.bool.default_use_auto_refresh));
    }

    /**
     * Initializes the areas array with data
     */
    private void initializeData() {
//        Area a = new Area("FooBar Origins");
//        Sensor s = new Sensor("/p/4b4b6683/foobar/opt", Calendar.getInstance().getTimeInMillis() / 1000, 1, 5);
//        s.setLight("260");
//        s.setTemperature("19.6");
//        s.setHumidity("20");
//        a.addSensor(s);
//        a.setPhotoId(R.drawable.foobar);
//        areaManager.addArea(a);
//
//        areaManager.setAreaImages(dbTable);
//        adapter.notifyDataSetChanged();
    }

    /**
     * Initializes the swipe layout with a listener and colors
     * @param swipe the swipe layout
     */
    private void initializeSwipeRefresh(SwipeRefreshLayout swipe) {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (useAutoRefresh) {
//                    stopAutoRefresh();
//                    startAutoRefresh();
//                }
                refresh();
            }
        });
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

//    /**
//     * Cleans and returns the passed string by stripping it of all '\n' at its end
//     * @param str the string to clean
//     * @return the cleaned string
//     */
//    public String cleanResultString(String str) {
//        if (str != null) {
//            while (str.length() > 0 && str.charAt(str.length()-1)=='\n') {
//                str = str.substring(0, str.length()-1);
//            }
//        }
//        return str;
//    }

    /**
     * Callback for the positive button click on the RelayOptions dialog
     * @param dialog the dialog whence the positive button was clicked
     */
    @Override
    public void onNetworkSettingsDialogPositiveClick(NetworkSettingsFragment dialog) {
//        Boolean newUseAutoRefresh = dialog.getUseAutoRefresh();
//        if (newUseAutoRefresh != useAutoRefresh) {
//            if (newUseAutoRefresh) {
//                startAutoRefresh();
//            } else {
//                stopAutoRefresh();
//            }
//        }
//        useAutoRefresh = newUseAutoRefresh;
        useServiceRelay = dialog.getUseServiceRelay();
        externalIp = dialog.getExternalIp();
        ccnSuite = dialog.getCcnSuite();
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(getString(R.string.pref_key_use_service_relay), useServiceRelay);
//        prefEditor.putBoolean(getString(R.string.pref_key_use_auto_refresh), useAutoRefresh);
        prefEditor.putString(getString(R.string.pref_key_external_ip), externalIp);
        prefEditor.putString(getString(R.string.pref_key_ccn_suite), ccnSuite);
        prefEditor.apply();
    }

//    /**
//     * Starts the automatic refreshing at a fixed rate
//     */
//    private void startAutoRefresh() {
//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        int delay = getResources().getInteger(R.integer.auto_refresh_delay_seconds);
//        scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                refresh();
//            }
//        }, delay, delay, SECONDS);
//    }
//
//    /**
//     * Stops the automatic refreshing
//     */
//    private void stopAutoRefresh() {
//        if (scheduledFuture != null) {
//            scheduledFuture.cancel(true);
//        }
//    }

    private void startSwipeAnimation() {
//        if(!swipeContainer.isRefreshing()) {
//            TypedValue typed_value = new TypedValue();
//            this.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
//            swipeContainer.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
//            swipeContainer.setRefreshing(true);
//        }
        swipeContainer.setRefreshing(true);
    }

    /**
     * Retreives SDS data, creates a peekTask with a specific uri aiming at the SDS
     */
    private void refreshSds() {
//        stopAutoRefresh();
        String port = getString(R.string.sds_port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;
        String uri = getString(R.string.sds_uri);

        Log.d(TAG, "refreshSds called");
        startSwipeAnimation();
        if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR
                    , AndroidPeekTask.SDS_TASK, targetIp, port, uri);
        } else {
            new AndroidPeekTask().execute(AndroidPeekTask.SDS_TASK, targetIp, port, uri);
        }
    }

    /**
     * Retreives the prediction data
     */
    public void refreshPrediction(Area area) {
        String port = getString(R.string.port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;
//        String targetIp = getString(R.string.databasse_ip);
//        String uri = getString(R.string.named_function_base) + area.getNamedFunctions().get("prediction") + "/" + area.getName();
        String uri = getString(R.string.prediction_base) + "/" + area.getName();

        Log.d(TAG, "refresh predictions called");
        startSwipeAnimation();
        if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR
                    , AndroidPeekTask.PREDICTION_TASK, targetIp, port, uri);
        } else {
            new AndroidPeekTask().execute(AndroidPeekTask.PREDICTION_TASK, targetIp, port, uri);
        }
    }

    /**
     * Retreives the historical data
     */
    public void refreshHistory(Area area) {
        String port = getString(R.string.port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;
//        String targetIp = getString(R.string.databasse_ip);
//        String uri = getString(R.string.named_function_base) + area.getNamedFunctions().get("historical") + "/" + area.getName();
        String uri = getString(R.string.history_base) + "/" + area.getName();

        Log.d(TAG, "refresh history called");
        startSwipeAnimation();
        if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR
                    , AndroidPeekTask.HISTORY_TASK, targetIp, port, uri);
        } else {
            new AndroidPeekTask().execute(AndroidPeekTask.HISTORY_TASK, targetIp, port, uri);
        }
    }

    /**
     * Retreives data from the known sensors by creating multiple peekTasks
     */
    private void refresh() {
        String port = getString(R.string.port);
        String targetIp = useServiceRelay ? getString(R.string.localIp) : externalIp;
        String requestedURI;
        int totalUris = areaManager.getTotalUris();
        int areaCount = areaManager.getAreas().size();

//        if (peekTaskCounter != null && peekTaskCounter.getRunningTasks() > 0) {
//            peekTaskCounter.setRunningTasks(totalUris + peekTaskCounter.getRunningTasks());
//        } else {
            peekTaskCounter = new AndroidPeekTaskCounter(totalUris);
//        }
        Log.d(TAG, "refresh called with "+totalUris+" URIs");
        startSwipeAnimation();

        for (int i = 0; i < areaCount; i++) {
            Area a = areaManager.getAreas().get(i);
            for (int j = 0; j < a.getSensors().size(); j++) {

                requestedURI = a.getSensor(j).getUriWithSeqno();
                if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, AndroidPeekTask.REFRESH_TASK
                            , targetIp, port, requestedURI, Integer.toString(i), Integer.toString(j));
                } else {
                    new AndroidPeekTask().execute(AndroidPeekTask.REFRESH_TASK, targetIp, port, requestedURI
                            , Integer.toString(i), Integer.toString(j));
                }
            }
        }
    }

    /**
     * Task in charge of using the jni androidPeek function.
     * calls androidPeek with passed parameters and treats the returned string
     */
    private class AndroidPeekTask extends AsyncTask<String, Void, String> {
        // position of the area in areas which will recieve the results
        static final String REFRESH_TASK = "refreshTask";
        static final String SDS_TASK = "sdsTask";
        static final String PREDICTION_TASK = "predictionTask";
        static final String HISTORY_TASK = "historyTask";
        private String taskType;
        private int areaPos = -1;
        private int sensorPos = -1;

        /**
         * calls androidPeek jni function with the received parameters
         * @param params parameters necesssary for the task
         * @return the answer from androidPeek, passing it to onPostExecute
         */
        protected String doInBackground(String... params) {
            taskType = params[0];
            String ipString = params[1];
            int portInt = Integer.parseInt(params[2]);
            String contentString = params[3];
            if (params.length == 6 && params[4] != null && params[5] != null) {
                areaPos = Integer.parseInt(params[4]);
                sensorPos = Integer.parseInt(params[5]);
            }
            Log.i(TAG, contentString+" ("+ccnSuite+") sent to "+ipString+" on port "+portInt);
            return androidPeek(ccnSuite, ipString, portInt, contentString);
        }

        /**
         * Treats the returned string from androidPeek
         * @param result the returned string from androidPeek
         */
        protected void onPostExecute(String result) {
            switch (taskType) {
                case SDS_TASK:
                    Log.i(TAG, "onPostExecute SDS result = " + result);
                    if (isJSONValid(result)) {
                        Log.i(TAG, "is valid");
                        areaManager.updateFromSds(result);
                    } else {
                        Toast.makeText(CcnLiteAndroid.this, "SDS not found. Using old sensor info", Toast.LENGTH_LONG).show();
                    }
                    areaManager.setAreaImages(dbTable);
                    refresh();
                    break;
                case PREDICTION_TASK:
                    Log.i(TAG, "onPostExecute prediction result = " + result);
                    predictionData = parsePredictionData(result);
                    //predictionData = "Date\n2016-12-14 12:04:44     8.83\n2016-12-14 12:04:57     12.03\ndtype";
//                    swipeContainer.setRefreshing(false);
                    break;
                case HISTORY_TASK:
                    Log.i(TAG, "onPostExecute history result = " + result);
                    String historicalData = parseHistoricalData(result);
                    if (!predictionData.equals("")) {
                        Prediction p = new Prediction(predictionData, historicalData);
                        adapter.updatePredictionGraph(p);
                    }
                    swipeContainer.setRefreshing(false);
                    break;
                case REFRESH_TASK:
                    Log.e(TAG, "onPostExecute refresh result = " + result);
                    if (areaPos == -1 || sensorPos == -1) {
                        Log.e(TAG, "Sensor reading received but has no link to an area or a sensor");
                        peekTaskCounter.taskFinished();
                    } else if (SensorReading.isSensorReading(result)) {
                        Log.i(TAG, "onPostExecute " + peekTaskCounter.runningTasks + " sensor reading result = " + result);
                        try {
                            Sensor s = areaManager.getAreas().get(areaPos).getSensor(sensorPos);
                            SensorReading sr = new SensorReading(result, s);
                            adapter.updateValue(areaPos, sensorPos, sr);
                            peekTaskCounter.taskFinished();
                        } catch (Exception e) {
                            Log.e(TAG, "Unvalid SensorReading: " + e);
                            result = "Unvalid SensorReading";
                            adapter.updateValue(areaPos, sensorPos, result);
                            peekTaskCounter.taskFinished();
                        }
                    } else {
                        Log.i(TAG, "onPostExecute " + peekTaskCounter.runningTasks + " unknown result = " + result);
                        if (result.equals("\n")) {
                            result = "No data available";
                        } else {
                            result = Helper.cleanResultString(result);
                        }
                        if (areaPos >= 0 && sensorPos >= 0) {
                            adapter.updateValue(areaPos, sensorPos, result);
                        }
                        peekTaskCounter.taskFinished();
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown task finished");
                    break;
            }
        }
    }

    String parsePredictionData(String result) {
        String[] eachLineArray = result.split("\n");
       // ArrayList<String> predictionData = new ArrayList<String>();
        for(int i=1; i < eachLineArray.length-1; i++){
            String[] columns = eachLineArray[i].split("\\s+");
            String time = columns[1];
            String[] HHMMArray = time.split(":");
          //  predictionData.add(HHMMArray[0]+HHMMArray[1]);
            //values.add(columns[2]);
        }
        predictionData = result;
        return predictionData;
    }

    String parseHistoricalData(String result){
        return result;
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
            Log.v(TAG, "Refresh finished");
            if (--runningTasks == 0) {
                areaManager.updateSmileyValues();
//                areaManager.sortAreas();
//                adapter.resetExpandedPosition();
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        }

        int getRunningTasks() {
            return runningTasks;
        }

        void setRunningTasks(int runningTasks) {
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

    //This function is called automatically when another activity sends back any data to this activity. In this case, another activity is the dialog for the camera or gallery.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {//It means that the Camera Intent had been selected by the user and now the data is back
            final Bundle extras = data.getExtras();
            if (extras != null) {

                Bitmap newProfilePic = extras.getParcelable("data");
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root);
                myDir.mkdirs();

                //Creates a name for the taken photo by camera
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String areaName = areaManager.getAreas().get(ppp).getName();
                String fname = areaName + n;

                File file = new File (myDir, fname);

                if (file.exists ()) file.delete ();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    boolean b = newProfilePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String address = root + "/" + areaName + n;
                File imageFile = new File(address);
                Uri uri = Uri.fromFile(imageFile);

                Cursor c = dbTable.selectData();
                if(c != null){
                    int count = c.getCount();
                    int index = 0;
                    c.moveToFirst();
                    int Column1 = c.getColumnIndex(dbTable.firstColumnName);
                    int Column2 = c.getColumnIndex(dbTable.secondColumnName);
                    while(index < count){
                        String nameOfPicture = c.getString(Column1);
                        String j = c.getString(Column2);
                        if(nameOfPicture.equals(areaName)){//If the area name is available in the database, then the table is just updated.
                            dbTable.updateTable(uri, areaName);
                            break;
                        }
                        c.moveToNext();
                        index++;
                    }
                    if(index == count)//If the area name is not available in the database, then the new row is inserted into the table.
                        dbTable.insertToTable(uri, areaName);
                }

                adapter.updateImage(ppp, newProfilePic);
                adapter.notifyItemChanged(ppp);
            }
        }else if (requestCode == 2){//It means that the Gallery Intent had been selected by the user and now the data is back
            Uri selectedImageUri = data.getData();
            String selectedImagePath = selectedImageUri.getPath();
            File file = new File(selectedImagePath);
            Uri test = Uri.fromFile(file);
            String testString = test.getPath();
            if(test.equals(selectedImageUri))
                System.out.print("hi");
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String areaName = areaManager.getAreas().get(ppp).getName();
            Cursor c = dbTable.selectData();
            if(c != null){
                int count = c.getCount();
                int index = 0;
                c.moveToFirst();
                int Column1 = c.getColumnIndex(dbTable.firstColumnName);
                int Column2 = c.getColumnIndex(dbTable.secondColumnName);
                while(index < count){
                    String nameOfPicture = c.getString(Column1);
                    if(nameOfPicture.equals(areaName)){//If the area name is available in the database, then the table is just updated.
                        dbTable.updateTable(selectedImageUri, areaName);
                        break;
                    }
                    c.moveToNext();
                    index++;
                }
                if(index == count)//If the area name is not available in the database, then the new row is inserted into the table.
                    dbTable.insertToTable(selectedImageUri, areaName);
            }
            adapter.updateImage(ppp, b);
            adapter.notifyItemChanged(ppp);
        }

    }

    public void launchHistoryActivity(View v) {
        Intent intent = new Intent(this, ChartTabsActivity_main.class);
        startActivity(intent);
    }
}


