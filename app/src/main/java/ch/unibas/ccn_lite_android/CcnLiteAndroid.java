package ch.unibas.ccn_lite_android;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class CcnLiteAndroid extends Activity implements OnMenuItemClickListener
{
    ArrayAdapter adapter;
    String hello;
    Context ccnLiteContext;
    SQLiteDatabase sensorDatabase;
    String resultValue;

    String ipString; // var for server ip
    String portString; //port
    String contentString;//Interest Object Name
    private Handler mHandler;


    /**
     * @desc create new activity and init relay with CCN
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
        adapter.notifyDataSetChanged();

        /*this part make dropdown list for testing options*/
        String arraySpinner[] = new String[] {
                "CCNx2015", "NDN2013", "CCNB", "IOT2014", "LOCALRPC", "LOCALRPC"
        };

        Spinner s = (Spinner) findViewById(R.id.formatSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);

        s.setAdapter(adapter);

        hello = relayInit();//Here we init our relay
        ccnLiteContext = this;
    }

    @Override
    public void onStart() {
        ListView lv;
        //Create SQLdb connection for history
        sensorDatabase = openOrCreateDatabase("SENSORDATABASE",MODE_PRIVATE,null);
        sensorDatabase.execSQL("CREATE TABLE IF NOT EXISTS sensorTable(sensorValue VARCHAR);");

        super.onStart();
        Button b = (Button) findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
                EditText ip = (EditText) findViewById(R.id.IPEditText);
                ipString = ip.getText().toString();
                EditText port = (EditText) findViewById(R.id.portEditText);
                portString = port.getText().toString();
                int portInt = Integer.parseInt(portString);
                EditText content = (EditText) findViewById(R.id.contentEditText);
                contentString = content.getText().toString();
                mHandler = new Handler();
                resultValue = androidPeek(ipString, portInt, contentString);//Send interest Request to jni file
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
    /**
     * @desc insert values into DB by lick button
     */
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
//This is declaration of native c functions
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
