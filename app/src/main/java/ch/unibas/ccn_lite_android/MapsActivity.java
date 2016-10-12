package ch.unibas.ccn_lite_android;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,  OnMarkerClickListener{

    private GoogleMap mMap;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMaxZoomPreference(14.0f);

        LatLng uppsalaCenter = new LatLng(59.8586926, 17.6459759);
        markers.add(0, mMap.addMarker(new MarkerOptions().position(uppsalaCenter).title("Sensor in Center")));
        markers.get(0).setTag("UppsalaCenter");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uppsalaCenter, 14.0f));

        mMap.addCircle(new CircleOptions().center(uppsalaCenter)
                .radius(300)
                .fillColor(0x90e74c3c)
                .strokeColor(0x90e74c3c)
                .strokeWidth(1));

        LatLng uppsalaAngstrom = new LatLng(59.8400773, 17.6478329);
        markers.add(1, mMap.addMarker(new MarkerOptions().position(uppsalaAngstrom).title("Sensor in Ångström")));
        markers.get(1).setTag("uppsalaAngstrom");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uppsalaAngstrom, 14.0f));

        LatLng uppsalaIkea = new LatLng(59.8471077, 17.69475);
        markers.add(2, mMap.addMarker(new MarkerOptions().position(uppsalaIkea).title("Sensor in IKEA")));
        markers.get(2).setTag("uppsalaIkea");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uppsalaIkea, 14.0f));
        /*// Add a marker in Stockholm and move the camera
        LatLng stockholm = new LatLng(59.3293235, 18.0685808);
        markers.add(0, mMap.addMarker(new MarkerOptions().position(stockholm).title("Sensor in Stockholm")));
        markers.get(0).setTag("Stockholm");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stockholm));

        LatLng uppsala = new LatLng(59.8586926, 17.6459759);
        markers.add(1, mMap.addMarker(new MarkerOptions().position(uppsala).title("Sensor in Uppsala")));
        markers.get(1).setTag("Uppsala");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uppsala));

        LatLng lund = new LatLng(55.70466016, 13.1910073);
        markers.add(2, mMap.addMarker(new MarkerOptions().position(lund).title("Sensor in Lund")));
        markers.get(2).setTag("Lund");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lund));


        LatLng gavle = new LatLng(60.6748796, 17.1412726);
        markers.add(3, mMap.addMarker(new MarkerOptions().position(gavle).title("Sensor in Gävle")));
        markers.get(3).setTag("Gävle");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gavle));

        LatLng linkoping = new LatLng(58.410807, 15.6213727);
        markers.add(4, mMap.addMarker(new MarkerOptions().position(linkoping).title("Sensor in Linköping")));
        markers.get(4).setTag("Linköping");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(linkoping));

        LatLng orebro = new LatLng(59.2752626, 15.2134105);
        markers.add(5, mMap.addMarker(new MarkerOptions().position(orebro).title("Sensor in Örebro")));
        markers.get(5).setTag("Örebro");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(orebro));

        LatLng vasteras = new LatLng(59.6099005, 16.5448091);
        markers.add(6, mMap.addMarker(new MarkerOptions().position(vasteras).title("Sensor in Västerås")));
        markers.get(6).setTag("Västerås");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vasteras));*/

        mMap.setOnMarkerClickListener(this);
    }

    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, DisplaySensorInfo.class);
        String sensorInfo;
        if(marker.getTag().equals("Stockholm")){
            sensorInfo = "Sensor is located in Stockholm.\nNo. 1\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else if(marker.getTag().equals("Uppsala")){
            sensorInfo = "Sensor is located in Uppsala.\nNo. 2\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else if(marker.getTag().equals("Lund")){
            sensorInfo = "Sensor is located in Lund.\nNo. 3\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else if(marker.getTag().equals("Gävle")){
            sensorInfo = "Sensor is located in Gävle.\nNo. 4\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else if(marker.getTag().equals("Linköping")){
            sensorInfo = "Sensor is located in Linköping.\nNo. 5\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else if(marker.getTag().equals("Örebro")){
            sensorInfo = "Sensor is located in Örebro.\nNo. 6\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else if(marker.getTag().equals("Västerås")){
            sensorInfo = "Sensor is located in Västerås.\nNo. 7\nBluetooth\nWifi";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }else{
            sensorInfo = "No Info.";
            intent.putExtra("sensorInfo", sensorInfo);
            startActivity(intent);
        }
        return false;
    }

   /* @Override
    public void onInfoWindowClick(Marker marker) {

        if(marker.getTag().equals("Stockholm"))
            Toast.makeText(this, "Stockholm\nNo.1",
                Toast.LENGTH_LONG).show();
        else if(marker.getTag().equals("Uppsala"))
        Toast.makeText(this, "Uppsala\nNo.2",
                Toast.LENGTH_LONG).show();
        else if(marker.getTag().equals("Lund"))
            Toast.makeText(this, "Lund\nNo.3",
                    Toast.LENGTH_LONG).show();
        else if(marker.getTag().equals("Gävle"))
            Toast.makeText(this, "Gävle\nNo.4",
                    Toast.LENGTH_LONG).show();
        else if(marker.getTag().equals("Linköping"))
            Toast.makeText(this, "Linköping\nNo.5",
                    Toast.LENGTH_LONG).show();
        else if(marker.getTag().equals("Örebro"))
            Toast.makeText(this, "Örebro\nNo.6",
                    Toast.LENGTH_LONG).show();
        else if(marker.getTag().equals("Västerås"))
            Toast.makeText(this, "Västerås\nNo.7",
                    Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "No Info.",
                    Toast.LENGTH_LONG).show();
    }*/

}
