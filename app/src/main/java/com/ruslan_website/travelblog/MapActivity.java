package com.ruslan_website.travelblog;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ruslan_website.travelblog.utils.view.RoundMap;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


    @BindView(R.id.bGetLocation) Button bGetLocation;
    @BindView(R.id.bCurrentLocation) Button bCurrentLocation;

    // In case of fragment
    // private GoogleMap googleMap;

    private MapView mapView;
    //private RoundMap mapView;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private Marker mCurrentMarker;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ********** FULL SCREEN **********
//        // hide title & notification bar. Can also be done in Manifest
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // hide action bar etc. If hide nav bar -> hide status bar -> hide action bar.
//        if (Build.VERSION.SDK_INT < 16) {
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else {
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //SYSTEM_UI_FLAG_HIDE_NAVIGATION hide nav bar
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN; //SYSTEM_UI_FLAG_FULLSCREEN Android 4.1+ hide status bar
//            decorView.setSystemUiVisibility(uiOptions);
//            ActionBar actionBar = getActionBar(); //if inherit android.support.v7.app.ActionBarActivity, use getSupportActionBar()
//            if (actionBar != null) {
//                actionBar.hide();
//            }
//        }
        // ********** FULL SCREEN **********

        setContentView(R.layout.activity_map);

        //mapView = (RoundMap) findViewById(R.id.mapview);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        MapsInitializer.initialize(getApplicationContext());

        // Async
        mapView.getMapAsync(this);

        // Sync
//		map = mapView.getMap();
//		map.getUiSettings().setMyLocationButtonEnabled(false);
//		map.setMyLocationEnabled(true);
//		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//		map.getUiSettings().setZoomGesturesEnabled(true);
//		this.buildGoogleApiClient();
//		mGoogleApiClient.connect();

        // In case of fragment
//		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // {inpoint initial dummy location
//		final LatLng Test = new LatLng(21 , 57);
//		Marker TP = map.addMarker(new MarkerOptions().position(Test).title("Test"));
    }

    // Async
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomGesturesEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Location selectedLocation = new Location(LocationManager.GPS_PROVIDER);
                selectedLocation.setLatitude(latLng.latitude);
                selectedLocation.setLongitude(latLng.longitude);

                if (mCurrentMarker != null) {
                    mCurrentMarker.remove();
                }
                placeMarker(selectedLocation);
            }
        });

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // Sync
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//		Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//		if (mLastLocation != null) {
//			// Place marker at current position
//			map.clear();
//			placeMarker(mLastLocation);
//			animateCamera(mLastLocation);
//		}

        LocationRequest mLocationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//		mLocationRequest.setSmallestDisplacement(0.1F); // 0.1 meter
        return mLocationRequest;
    }

    @Override
    public void onConnectionSuspended(int i) {
//		Log.i("onConnectionSuspended", "Connection to Google API suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//		Log.i("onConnectionSuspended", "Connection to Google API failed");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onPause() {
        super.onPause();
        // Unregister for location callbacks:
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
//		Log.i("Location Update", "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());

        // Remove previous location marker and add new one at current position
//		if (mCurrentMarker != null) {
//            mCurrentMarker.remove();
//		}
//		placeMarker(location);

        // If you only need one location, unregister the listener
//		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

//        animateCamera(location);
        mCurrentLocation = location;
    }

    private void placeMarker(Location location){
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Selected Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrentMarker = map.addMarker(markerOptions);
    }
    private void animateCamera(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
//			.bearing(90)                // Sets the orientation of the camera to east
//			.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @OnClick(R.id.bCurrentLocation)
    public void pinCurrentLocation(View view){
        animateCamera(mCurrentLocation);

        if (mCurrentMarker != null) {
            mCurrentMarker.remove();
        }
        placeMarker(mCurrentLocation);
    }

    @OnClick(R.id.bGetLocation)
    public void getLocation(View view){
        if(latLng == null) {
            return;
        }

        String currentLocality = getAddress(latLng.latitude, latLng.longitude);

        if(currentLocality == null){
            Toast.makeText(MapActivity.this, "Pick an actual town/city!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("Current Locality", currentLocality);

        Intent intent = new Intent(MapActivity.this, NewEntryActivity.class);
        intent.putExtra("currentLocality", currentLocality);
        startActivity(intent);
    }

    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

//            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

            return obj.getLocality();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Current Locality", e.getMessage());
            return null;
        }
    }
}
