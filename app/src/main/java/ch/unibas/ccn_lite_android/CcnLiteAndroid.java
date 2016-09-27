package ch.unibas.ccn_lite_android;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class CcnLiteAndroid extends Activity implements OnMenuItemClickListener
{
    ArrayAdapter adapter;

    Context ccnLiteContext;

    SQLiteDatabase sensorDatabase;
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

    String netConnString;
    String filename;


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

        ccnLiteContext = this;

        ipEditText = (EditText) findViewById(R.id.IPEditText);
        portEditText = (EditText) findViewById(R.id.portEditText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        contentEditText = (EditText) findViewById(R.id.contentEditText);

    }

    @Override
    public void onStart() {
        ListView lv;
        sensorDatabase = openOrCreateDatabase("SENSORDATABASE",MODE_PRIVATE,null);
        sensorDatabase.execSQL("CREATE TABLE IF NOT EXISTS sensorTable(sensorValue VARCHAR);");

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
                //if not do network op
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
        });

        ImageView imageViewMenu = (ImageView) findViewById(R.id.imageViewMenu);
        imageViewMenu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                showPopUp(v);
            }
        });
        mHandler = new Handler();
    }
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                sensorDatabase.execSQL("INSERT INTO sensorTable VALUES('" + androidPeekResult + "');");
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
