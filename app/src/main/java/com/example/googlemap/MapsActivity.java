package com.example.googlemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    Location currentLocation;
    ImageView find_location;
    Marker CurrentMark=null;
    private static final int LOCATION_REQUEST_CODE = 101;
EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        search=findViewById(R.id.search);
        find_location=findViewById(R.id.location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
find_location.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        fetchLastLocation();
    }
});
    }

    /** Par

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
           mMap.setMyLocationEnabled(true);
        }
        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin1));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        if (CurrentMark!=null)
            CurrentMark.remove();
        CurrentMark=googleMap.addMarker(markerOptions);
        intial();

    }

    /** *********************** Street *****************/
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
/*streetViewPanorama.setPosition(new LatLng(40.705991, -74.008780));
streetViewPanorama.setStreetNamesEnabled(true);
streetViewPanorama.setUserNavigationEnabled(true);
streetViewPanorama.setZoomGesturesEnabled(true);
streetViewPanorama.setPanningGesturesEnabled(true);*/
    }

    /** *********************** Street **************** */

    /**Find Location ############# */
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
    task.addOnSuccessListener(new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                currentLocation = location;

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
                Toast.makeText(MapsActivity.this,currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MapsActivity.this,"No Location recorded",Toast.LENGTH_SHORT).show();
            }
        }
    });
}


    /**search Location ############# */
    private void intial() {
search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId== EditorInfo.IME_ACTION_DONE ||actionId== EditorInfo.IME_ACTION_SEARCH
                ||actionId== KeyEvent.ACTION_DOWN ||actionId== KeyEvent.KEYCODE_ENTER )
        {
            geoLocate();
        }
        return false;
    }
});
    }
    private void geoLocate() {
String s=search.getText().toString();
        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> list=new ArrayList<>();
        try {
            list= geocoder.getFromLocationName(s,1);
        } catch (IOException e) {
            Toast.makeText(this, "Not found!", Toast.LENGTH_SHORT).show();
        }
        if(list.size()>0){
            Address address=list.get(0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(),address.getLongitude()),6));
            MarkerOptions markerOptions= (MarkerOptions) new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude()))
                    .title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            mMap.clear();
            mMap.addMarker(markerOptions);
        }

    }
    



}
