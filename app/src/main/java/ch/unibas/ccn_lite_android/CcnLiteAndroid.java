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
    int newData;
    String ipString;
    String portString;
    String contentString;
    private Handler mHandler;

    public final static UUID SERV_UUID = new UUID(0x0000222000001000L,
            0x800000805f9b34fbL);
    public final static UUID CONF_UUID = new UUID(0x0000290200001000L,
            0x800000805f9b34fbL);
    public final static UUID SEND_UUID = new UUID(0x0000222200001000L,
            0x800000805f9b34fbL);
    public final static UUID RECV_UUID = new UUID(0x0000222100001000L,
            0x800000805f9b34fbL);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
        adapter.notifyDataSetChanged();
        hello = relayInit();
        ccnLiteContext = this;
    }

    @Override
    public void onStart() {
        ListView lv;

        super.onStart();
        Button b = (Button) findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText ip = (EditText) findViewById(R.id.IPEditText);
                ipString = ip.getText().toString();
                EditText port = (EditText) findViewById(R.id.portEditText);
                portString = port.getText().toString();
                int portInt = Integer.parseInt(portString);
                EditText content = (EditText) findViewById(R.id.contentEditText);
                contentString = content.getText().toString();
                mHandler = new Handler();
                String test = androidPeek(ipString, portInt, contentString);
                TextView result = (TextView) findViewById(R.id.resultTextView);
                result.setMovementMethod(new ScrollingMovementMethod());
                result.setText(test, TextView.BufferType.EDITABLE);

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
