package ch.unibas.ccn_lite_android;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class CcnLiteAndroid extends Activity {
    ArrayAdapter adapter;

    Context ccnLiteContext;
    String ipString;
    String portString;
    String contentString;
    private Handler mHandler;

    EditText ipEditText;
    EditText portEditText;
    EditText contentEditText;
    TextView resultTextView;
    int portInt;
    String androidPeekResult;

    boolean networkConnectionBool;
    String netConnString;
    String filename;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
        adapter.notifyDataSetChanged();

        ccnLiteContext = this;

        ipEditText = (EditText) findViewById(R.id.IPEditText);
        portEditText = (EditText) findViewById(R.id.portEditText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        contentEditText = (EditText) findViewById(R.id.contentEditText);

    }

    @Override
    public void onStart() {
        ListView lv;

        super.onStart();
        Button b = (Button) findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ipString = ipEditText.getText().toString();
                portString = portEditText.getText().toString();
                portInt = Integer.parseInt(portString);
                contentString = contentEditText.getText().toString();
                filename = contentString.replace("/", ""); //maybe file can't start with dash
                androidPeekResult = "No Data Found";
                mHandler = new Handler();

                //check if file exists
                File file = new File(ccnLiteContext.getFilesDir(), filename);
                if(file.exists()) {
                    FileInputStream inputStream;
                    try {
                        inputStream = openFileInput(filename);
                        //inputStream.read(contentString.getBytes());
                        int c;
                        String temp="";
                        while( (c = inputStream.read()) != -1){
                            temp = temp + Character.toString((char)c);
                        }
                        androidPeekResult = temp;
                        inputStream.close();
                        toast("result taken from file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    resultTextView.setText(androidPeekResult, TextView.BufferType.EDITABLE);
                    resultTextView.setMovementMethod(new ScrollingMovementMethod());
                } else {
                    //if not do network op
                    //TODO: make sure this happens in seperate thread
                    //check network connection
                    netConnString = "No Network Connection";
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new AndroidPeekTask().execute(androidPeekResult); //TODO: it's not actually using this content string

                    } else {
                        // display error
                        toast(netConnString);
                        androidPeekResult = netConnString;
                        resultTextView.setText(androidPeekResult, TextView.BufferType.EDITABLE);
                    }
                }
            }
        });
        mHandler = new Handler();

    }

    //executes the android peek function
    private class AndroidPeekTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            androidPeekResult = androidPeek(ipString, portInt, contentString);
            return androidPeekResult;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            resultTextView.setText(androidPeekResult, TextView.BufferType.EDITABLE);
            resultTextView.setMovementMethod(new ScrollingMovementMethod());

            //create file and store result
            //temp files saving to be replaced by data base
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(androidPeekResult.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //end temp file saving
        }
    }


    public void toast(String text) {
        Toast toast = Toast.makeText(ccnLiteContext, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void appendToLog(String line) {
        while (adapter.getCount() > 500)
            adapter.remove(adapter.getItem(0));
        adapter.add(line);
        adapter.notifyDataSetChanged();
    }

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
