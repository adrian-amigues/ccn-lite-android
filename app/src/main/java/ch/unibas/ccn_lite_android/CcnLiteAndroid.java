package ch.unibas.ccn_lite_android;

import java.util.UUID;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class CcnLiteAndroid extends Activity
{
    ArrayAdapter adapter;
    Context ccnLiteContext;
    private Handler mHandler;

    //    For service
    RelayService mService;
    boolean mBound = false;
    TextView resultTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
       // adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
       // adapter.notifyDataSetChanged();
        if(mBound) {
            mService.startRely();
        }
        ccnLiteContext = this;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bind to RelayService
        Intent intent = new Intent(this, RelayService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "mBound = " + mBound, Toast.LENGTH_SHORT).show();

        mHandler = new Handler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void onLinearLayoutClick(View v) {
       // Toast.makeText(this, "click!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, chart.class);
        startActivity(intent);

    }

//    public void appendToLog(String line) {
//        while (adapter.getCount() > 500)
//            adapter.remove(adapter.getItem(0));
//        adapter.add(line);
//        adapter.notifyDataSetChanged();
//    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to RelayService, cast the IBinder and get RelayService instance
            RelayService.LocalBinder binder = (RelayService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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

    private class AndroidPeek extends AsyncTask<String, Void, String> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected String doInBackground(String... params) {
            String ipString = params[0];
            int portInt = Integer.parseInt(params[1]);
            String contentString = params[2];
            return mService.startAndroidPeek(ipString, portInt, contentString);
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String result) {
            resultTextView.setMovementMethod(new ScrollingMovementMethod());
            resultTextView.append(result);
        }
    }
}


