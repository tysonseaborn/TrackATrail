package com.example.tyson.trackatrail;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class StartRouteActivity  extends TrackATrail implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //String userID;
    boolean startedTracking, currentlyTracking = false;
    Button btnStart, btnSave;
    //String username;
    User user;
    String inUsername;

    //Map Properties
    GoogleMap map;
    ArrayList<LatLng> markerPoints;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private static final float DISPLACEMENT_DISTANCE =
            5.0f;

    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    Location mCurrentLocation;
    ArrayList<Location> locationArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_route);
        db.open();
        inUsername = getIntent().getExtras().getString("username");

        Cursor c = db.getAllUsers();
        if (c.moveToFirst()) {
            do {
                User dbUser = db.RetrieveUser(c);

                if (dbUser.username.equals(inUsername)) {
                    user = dbUser;
                    break;
                }
            } while(c.moveToNext());
        }

        db.close();


        initMap();

        btnStart = (Button)findViewById(R.id.buttonStart);
        btnSave = (Button)findViewById(R.id.buttonSaveRoute);
    }

    private void initMap() {
        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if (map != null) {

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);
            map.getUiSettings().setAllGesturesEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            locationArray = new ArrayList<Location>();
            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create();
            // Use high accuracy
            mLocationRequest.setPriority(
                    LocationRequest.PRIORITY_HIGH_ACCURACY);
            // Set the update interval to 5 seconds
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            // Set the fastest update interval to 1 second
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT_DISTANCE);

            mLocationClient = new LocationClient(this, this, this);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            openMenu("about");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Open the selected menu item
    public void openMenu(String menuItem) {
        if(menuItem.equals("about")) {
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 1. connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 1. disconnecting the client invalidates it.
        mLocationClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLocationClient != null) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            Toast.makeText(this, "SWAG", Toast.LENGTH_LONG).show();
        }

        if (mLocationClient != null) {
            mCurrentLocation = mLocationClient.getLastLocation();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));
            locationArray.add(mCurrentLocation);
            Toast.makeText(this, "SWAG2",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = mLocationClient.getLastLocation();
        //Keep camera on user
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));

        if (currentlyTracking = true) {
            locationArray.add(mCurrentLocation);
            Toast.makeText(this, mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude(),Toast.LENGTH_LONG).show();
            if (locationArray.size() > 2) {
                Toast.makeText(this, "SWAG3", Toast.LENGTH_LONG).show();
                map.addPolyline(new PolylineOptions()
                        .add(new LatLng(locationArray.get(locationArray.size()-2).getLatitude(), locationArray.get(locationArray.size()-2).getLongitude() ),
                                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .width(2)
                        .color(Color.MAGENTA).geodesic(true));
            }
        }
    }

    public void onButtonClick(View view) {
        switch(view.getId()) {
            case R.id.buttonStart:
                startTracking();
                break;
            case R.id.buttonSaveRoute:
                Intent iSave = new Intent(this, SaveRouteActivity.class);
                iSave.putExtra("username", inUsername);
                startActivity(iSave);
                break;
        }
    }

    public void startTracking() {
        startedTracking = true;

        if(btnStart.getText().equals("Start Tracking")) {
            btnStart.setText("Stop Tracking");
            btnSave.setEnabled(false);
            currentlyTracking = true;
        }
        else {
            btnStart.setText("Start Tracking");
            btnSave.setEnabled(true);
            currentlyTracking = false;
        }
    }
}
