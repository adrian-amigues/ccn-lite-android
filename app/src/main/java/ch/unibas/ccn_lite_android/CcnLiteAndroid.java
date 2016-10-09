package ch.unibas.ccn_lite_android;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

import com.google.android.gms.maps.MapFragment;

import ch.unibas.ccn_lite_android.fragments.DeleteDatabaseFragment;
import ch.unibas.ccn_lite_android.fragments.HistoryFragment;
import ch.unibas.ccn_lite_android.fragments.HomeFragment;
import ch.unibas.ccn_lite_android.fragments.PreferencesFragment;
import ch.unibas.ccn_lite_android.fragments.SensorsInfo;
import ch.unibas.ccn_lite_android.fragments.addToDatabase;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class CcnLiteAndroid extends Activity //implements OnMenuItemClickListener
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

    private static String TAG = CcnLiteAndroid.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    //private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        adapter = new ArrayAdapter(this, R.layout.logtextview, 0);
        adapter.notifyDataSetChanged();

        mNavItems.add(new NavItem("Home", "Send request", R.drawable.ic_home_black_24dp));
        mNavItems.add(new NavItem("Add", "Add to database", R.drawable.ic_add_black_24dp));
        mNavItems.add(new NavItem("Delete", "Delete from datasbase", R.drawable.ic_delete_black_24dp));
        mNavItems.add(new NavItem("History", "Previous sensor values", R.drawable.ic_history_black_24dp));
        mNavItems.add(new NavItem("Sensors Location", "See on GoogleMap", R.drawable.ic_place_black_24dp));
        mNavItems.add(new NavItem("Sensors Info", " ", R.drawable.ic_settings_input_antenna_black_24dp));


        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter2 = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter2);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

       /* String arraySpinner[] = new String[] {
                "CCNx2015", "NDN2013", "CCNB", "IOT2014", "LOCALRPC", "LOCALRPC"
        };

        Spinner s = (Spinner) findViewById(R.id.formatSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);

        s.setAdapter(adapter);
*/

        hello = relayInit();
        ccnLiteContext = this;
    }


    private void selectItemFromDrawer(int position) {
        Fragment fragment;
        if(position == 0){//Home
            fragment = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mNavItems.get(position).mTitle);
        }else if(position == 1){//Add
            sensorDatabase.execSQL("INSERT INTO sensorTable VALUES('" + resultValue + "');");
            fragment = new addToDatabase();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mNavItems.get(position).mTitle);
        }else if(position == 2){//Delete
            sensorDatabase.execSQL("DELETE FROM sensorTable;");
            fragment = new DeleteDatabaseFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mNavItems.get(position).mTitle);
        }else if(position == 3){//History
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
            fragment = new HistoryFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            TextView historyLocation = (TextView) findViewById(R.id.historyShowTextView2);
            // historyLocation.setMovementMethod(new ScrollingMovementMethod());
            // historyLocation.setText("Number of Items: " + count + "\n" + sensorValue , TextView.BufferType.EDITABLE);


            mDrawerList.setItemChecked(position, true);
            setTitle(mNavItems.get(position).mTitle);
        }else if(position == 4){//Sensors
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }else  if(position == 5){//Home
            fragment = new SensorsInfo();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mNavItems.get(position).mTitle);
        }




        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void onStart() {
        ListView lv;
        sensorDatabase = openOrCreateDatabase("SENSORDATABASE",MODE_PRIVATE,null);
        sensorDatabase.execSQL("CREATE TABLE IF NOT EXISTS sensorTable(sensorValue VARCHAR);");

        super.onStart();
       /* Button b = (Button) findViewById(R.id.sendButton);
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
                resultValue = androidPeek(ipString, portInt, contentString);
                TextView result = (TextView) findViewById(R.id.resultTextView);
                result.setMovementMethod(new ScrollingMovementMethod());
                result.setText(resultValue, TextView.BufferType.EDITABLE);

            }
        });*/

       /* ImageView imageViewMenu = (ImageView) findViewById(R.id.imageViewMenu);
        imageViewMenu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                showPopUp(v);
            }
        });*/
        mHandler = new Handler();
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

class NavItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }
}

class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.navTitle);
        TextView subtitleView = (TextView) view.findViewById(R.id.navSubTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon2);

        titleView.setText( mNavItems.get(position).mTitle );
        subtitleView.setText( mNavItems.get(position).mSubtitle );
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }
}
