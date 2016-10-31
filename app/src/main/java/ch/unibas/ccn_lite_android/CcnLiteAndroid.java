package ch.unibas.ccn_lite_android;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class CcnLiteAndroid extends AppCompatActivity
{
    private List<Area> areas;
    private AreasAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private String ipString = "127.0.0.1";
    private String port = "9999";
    private WorkCounter wk = null;
    private boolean useParallelTaskExecution = false; // native function androidPeek can't handle parallel executions

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

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
//        rv.setHasFixedSize(true);
        areas = new ArrayList<>();
        adapter = new AreasAdapter(areas, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        initializeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void initializeData() {
        areas.add(new Area("FooBar", "Eat in a basement - 35 Db", R.drawable.foobar, "/unoise/foobar/"));
        areas.add(new Area("Uthgård", "They have sofas - 28 Db", R.drawable.uthgard, "/unoise/utn/"));
        areas.add(new Area("Rullan", "Expensive but nice - 32 Db", R.drawable.rullan, "/unoise/rullan/"));
        areas.add(new Area("Uthgård", "Not them again - 25 Db. " +
                "This is a longer text than the previous ones.", R.drawable.uthgard, "/unoise/utn/"));
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
        int areaCount = adapter.getItemCount();
        wk = new WorkCounter(areaCount);

        for (int i = 0; i < areaCount; i++) {
            String requestedURI = adapter.getURI(i);
            if (useParallelTaskExecution && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new AndroidPeekTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ipString, port, requestedURI, Integer.toString(i));
            } else {
                new AndroidPeekTask().execute(ipString, port, requestedURI, Integer.toString(i));
            }
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

            return androidPeek(ipString, portInt, contentString);
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String result) {
            if (result.equals("\n")) {
                result = "No data available";
            } else {
                result = cleanResultString(result);
            }
            adapter.updateValue(areaPos, result);
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
                adapter.notifyDataSetChanged();
            }
        }
    }

    //    Native functions declarations
    public native String relayInit();
    public native String androidPeek(String ipString, int portString, String contentString);
    /* this is used to load the 'ccn-lite-android' library on application
     * startup. The library has already been unpacked into
     * /data/data/ch.unibas.ccnliteandroid/lib/libccn-lite-android.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("ccn-lite-android");
    }
}


