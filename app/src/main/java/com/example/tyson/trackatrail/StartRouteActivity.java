package com.example.tyson.trackatrail;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 2;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private static final float DISPLACEMENT_DISTANCE =
            2.0f;

    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    Location mCurrentLocation;
    ArrayList<Location> locationArray;
    ArrayList<Location> completedRouteArray;

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
        }

        if (mLocationClient != null) {
            mCurrentLocation = mLocationClient.getLastLocation();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));
            locationArray.add(mCurrentLocation);
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

        if (currentlyTracking == true) {
            locationArray.add(mCurrentLocation);
            if (locationArray.size() > 2) {
                map.addPolyline(new PolylineOptions()
                        .add(new LatLng(locationArray.get(locationArray.size()-2).getLatitude(), locationArray.get(locationArray.size()-2).getLongitude()),
                                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .width(5)
                        .color(Color.MAGENTA).geodesic(true));
            }
        }
    }

    public void onButtonClick(View view) {
        switch(view.getId()) {
            case R.id.buttonStart:
                manageTracking();
                break;
            case R.id.buttonSaveRoute:
                if (completedRouteArray.size() > -1) {

                    Intent iSave = new Intent(this, SaveRouteActivity.class);
                    iSave.putExtra("username", inUsername);
                    double[] latitudes = new double[completedRouteArray.size()];
                    double[] longitudes = new double[completedRouteArray.size()];

                    for (int i = 0; i < completedRouteArray.size(); i++) {
                        latitudes[i] = completedRouteArray.get(i).getLatitude();
                        longitudes[i] = completedRouteArray.get(i).getLongitude();
                    }
                    /*
                    latitudes[0] = 43.01186;
                    latitudes[1] = 43.01192;
                    latitudes[2] = 43.01223;
                    latitudes[3] = 43.01255;
                    latitudes[4] = 43.01267;

                    longitudes[0] = -81.21094;
                    longitudes[1] = -81.21042;
                    longitudes[2] = -81.20948;
                    longitudes[3] = -81.20776;
                    longitudes[4] = -81.20682; */

                    iSave.putExtra("latitudes", latitudes);
                    iSave.putExtra("longitudes", longitudes);
                    startActivity(iSave);
                }
                else {
                    Toast.makeText(this, "You need to travel further peasant!", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    public void manageTracking() {
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
            completedRouteArray = locationArray;
        }
    }
}
