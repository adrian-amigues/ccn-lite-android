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
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
//                fetchTimelineAsync(0);
                Toast.makeText(CcnLiteAndroid.this, "Refresh values", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
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
    }

    private void initializeData(){
        areas.add(new Area("FooBar", "Eat in a basement - 35 Db", R.drawable.foobar));
        areas.add(new Area("Uthgård", "They have sofas - 28 Db", R.drawable.uthgard));
        areas.add(new Area("Uthgård2", "Not them again - 25 Db. " +
                "This is a longer text than the previous ones.", R.drawable.uthgard));
        areas.add(new Area("Uthgård3", "Not them again - 25 Db. " +
                "This is a longer text than the previous ones.", R.drawable.uthgard));
        adapter.notifyDataSetChanged();
    }

//    public void onLinearLayoutClick(View v) {
//        Toast.makeText(this, "click!", Toast.LENGTH_SHORT).show();
//    }

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

//    private class AndroidPeek extends AsyncTask<String, Void, String> {
//        /** The system calls this to perform work in a worker thread and
//         * delivers it the parameters given to AsyncTask.execute() */
//        protected String doInBackground(String... params) {
//            String ipString = params[0];
//            int portInt = Integer.parseInt(params[1]);
//            String contentString = params[2];
//            return mService.startAndroidPeek(ipString, portInt, contentString);
//        }
//
//        /** The system calls this to perform work in the UI thread and delivers
//         * the result from doInBackground() */
//        protected void onPostExecute(String result) {
//            resultTextView.setMovementMethod(new ScrollingMovementMethod());
//            resultTextView.append(result);
//        }
//    }
}


