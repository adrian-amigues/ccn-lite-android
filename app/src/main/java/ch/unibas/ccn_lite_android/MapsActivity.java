package ch.unibas.ccn_lite_android;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnInfoWindowClickListener {

    private GoogleMap mMap;

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

        // Add a marker in Stockholm and move the camera
        LatLng stockholm = new LatLng(59.3293235, 18.0685808);
        mMap.addMarker(new MarkerOptions().position(stockholm).title("Sensor No. 1 in Stockholm"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stockholm));

        LatLng uppsala = new LatLng(59.8586926, 17.6459759);
        mMap.addMarker(new MarkerOptions().position(uppsala).title("Sensor No. 2 in Uppsala"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uppsala));

        LatLng lund = new LatLng(55.70466016, 13.1910073);
        mMap.addMarker(new MarkerOptions().position(lund).title("Sensor No. 3 in lund"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lund));
        mMap.setOnInfoWindowClickListener(this);

        LatLng gavle = new LatLng(60.6748796, 17.1412726);
        mMap.addMarker(new MarkerOptions().position(gavle).title("Sensor No. 1 in Gävle"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gavle));

        LatLng linkoping = new LatLng(58.410807, 15.6213727);
        mMap.addMarker(new MarkerOptions().position(linkoping).title("Sensor No. 1 in Linköping"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(linkoping));

        LatLng orebro = new LatLng(59.2752626, 15.2134105);
        mMap.addMarker(new MarkerOptions().position(orebro).title("Sensor No. 1 in Örebro"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(orebro));

        LatLng vasteras = new LatLng(59.6099005, 16.5448091);
        mMap.addMarker(new MarkerOptions().position(vasteras).title("Sensor No. 1 in Västerås"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vasteras));

    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_LONG).show();
    }

}
