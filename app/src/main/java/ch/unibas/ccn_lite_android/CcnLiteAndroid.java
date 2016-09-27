package ch.unibas.ccn_lite_android;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.os.Bundle;
import android.os.Handler;


public class CcnLiteAndroid extends Activity {
    ArrayAdapter adapter;
    String hello;
    Context ccnLiteContext;

    String ipString;
    String portString;
    String contentString;
    private Handler mHandler;

    EditText ipEditText;
    EditText portEditText;
    int portInt;
    EditText content = (EditText) findViewById(R.id.contentEditText);
    String androidPeekResult;
    TextView resultTextView;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
        adapter.notifyDataSetChanged();
        hello = relayInit();
        ccnLiteContext = this;

        ipEditText = (EditText) findViewById(R.id.IPEditText);
        ipString = ipEditText.getText().toString();
        portEditText = (EditText) findViewById(R.id.portEditText);
        portString = portEditText.getText().toString();
        portInt = Integer.parseInt(portString);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    @Override
    public void onStart() {
        ListView lv;

        super.onStart();
        Button b = (Button) findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mHandler = new Handler();
                androidPeekResult = androidPeek(ipString, portInt, contentString);
                resultTextView.setMovementMethod(new ScrollingMovementMethod());
                resultTextView.setText(androidPeekResult, TextView.BufferType.EDITABLE);

            }
        });
        mHandler = new Handler();
        //  System.out.print("hiiiiiiiiiiiiiiii");

    }

    public void appendToLog(String line) {
        while (adapter.getCount() > 500)
            adapter.remove(adapter.getItem(0));
        adapter.add(line);
        adapter.notifyDataSetChanged();
    }

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
