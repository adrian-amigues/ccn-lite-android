package ch.unibas.ccn_lite_android;

import java.util.UUID;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;


import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class CcnLiteAndroid extends Activity implements OnMenuItemClickListener
{
    ArrayAdapter adapter;
    String hello;
    Context ccnLiteContext;
    int newData;
    SQLiteDatabase sensorDatabase;
    String resultValue;

    String ipString;
    String portString;
    String contentString;
    private Handler mHandler;

//    For service
    RelayService mService;
    boolean mBound = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
        adapter.notifyDataSetChanged();


        String arraySpinner[] = new String[] {
                "CCNx2015", "NDN2013", "CCNB", "IOT2014", "LOCALRPC", "LOCALRPC"
        };

        Spinner s = (Spinner) findViewById(R.id.formatSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);

        s.setAdapter(adapter);


//        hello = relayInit();
        if(mBound) {
            mService.startRely();
        }
        ccnLiteContext = this;
    }

    @Override
    public void onStart() {
        super.onStart();

        ListView lv;
        sensorDatabase = openOrCreateDatabase("SENSORDATABASE",MODE_PRIVATE,null);
        sensorDatabase.execSQL("CREATE TABLE IF NOT EXISTS sensorTable(sensorValue VARCHAR);");

        // Bind to RelayService
        Intent intent = new Intent(this, RelayService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "mBound = " + mBound, Toast.LENGTH_SHORT).show();

        Button b = (Button) findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
               // myLayout.setBackgroundColor(Color.rgb(1,0,0));
                EditText ip = (EditText) findViewById(R.id.IPEditText);
                ipString = ip.getText().toString();
                EditText port = (EditText) findViewById(R.id.portEditText);
                portString = port.getText().toString();
                int portInt = Integer.parseInt(portString);
                EditText content = (EditText) findViewById(R.id.contentEditText);
                contentString = content.getText().toString();
                mHandler = new Handler();
                resultValue = mService.startAndroidPeek(ipString, portInt, contentString);
                TextView result = (TextView) findViewById(R.id.resultTextView);
                result.setMovementMethod(new ScrollingMovementMethod());
                result.setText(resultValue, TextView.BufferType.EDITABLE);

            }
        });

        ImageView imageViewMenu = (ImageView) findViewById(R.id.imageViewMenu);
        imageViewMenu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                showPopUp(v);
            }
        });
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

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                sensorDatabase.execSQL("INSERT INTO sensorTable VALUES('" + resultValue + "');");
                return true;

            case R.id.menu_history:
                Cursor resultSet = sensorDatabase.rawQuery("Select * from sensorTable",null);
                String sensorValue="";
                int count = 0;
                if(resultSet != null) {
                    resultSet.moveToFirst();
                    while (count < resultSet.getCount()) {
                        count++;
                        sensorValue += count + ": ";
                        sensorValue += resultSet.getString(0) + "\n";
                        resultSet.moveToNext();

                    }
                }

                Intent intent = new Intent(this, DisplayDatabaseHistory.class);
                intent.putExtra("sensorHistory", sensorValue);
                intent.putExtra("countOfItems", count);
                startActivity(intent);

                return true;

            case R.id.menu_reset:
                sensorDatabase.execSQL("DELETE FROM sensorTable;");

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
    public void onServiceButtonClick(View v) {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            int num = mService.getRandomNumber();
            Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();
        }
    }

    public void showPopUp(View v){
        PopupMenu popup = new PopupMenu(CcnLiteAndroid.this, v);
        popup.setOnMenuItemClickListener(CcnLiteAndroid.this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_items, popup.getMenu());
        popup.show();
    }

    public void appendToLog(String line) {
        while (adapter.getCount() > 500)
            adapter.remove(adapter.getItem(0));
        adapter.add(line);
        adapter.notifyDataSetChanged();
    }

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
}
