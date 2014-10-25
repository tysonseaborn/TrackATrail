package com.example.tyson.trackatrail;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class SaveRouteActivity extends TrackATrail {

    EditText etName, etDescription;
    TextView tvDistance;
    Spinner sItems;
    User user;
    DBAdapter db;
    double[] latitudes;
    double[] longitudes;
    String inUsername;
    double routeDistance;

    DecimalFormat df = new DecimalFormat("#.####");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_route);
        db = new DBAdapter(this);

        db.open();
        inUsername = getIntent().getExtras().getString("username");
        latitudes = getIntent().getExtras().getDoubleArray("latitudes");
        longitudes = getIntent().getExtras().getDoubleArray("longitudes");

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

        for (int i = 0; i < longitudes.length-1; i++) {
            Location locationA = new Location("point A");

            locationA.setLatitude(latitudes[i]);
            locationA.setLongitude(longitudes[i]);

            Location locationB = new Location("point B");

            locationB.setLatitude(latitudes[i+1]);
            locationB.setLongitude(longitudes[i+1]);

            routeDistance += locationA.distanceTo(locationB);
        }

        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Walking");
        spinnerArray.add("Jogging");
        spinnerArray.add("Cycling");
        spinnerArray.add("Roller Blading");

        routeDistance *= 0.001;

        tvDistance = (TextView)findViewById(R.id.textViewRouteDistance);
        tvDistance.setText(df.format(routeDistance) + "km");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.spinnerRouteType);
        sItems.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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

    public void onButtonClick(View view) {
        etName = (EditText)findViewById(R.id.editTextRouteName);
        etDescription = (EditText)findViewById(R.id.editTextRouteDescription);
        sItems = (Spinner)findViewById(R.id.spinnerRouteType);
        tvDistance = (TextView)findViewById(R.id.textViewRouteDistance);

        // check that all fields are filled out
        if(!etName.getText().toString().equals("") &&
                !etDescription.getText().toString().equals("")) {

                db.open();
                Route route = new Route();
                route.user_ID = user.user_ID;
                route.name = etName.getText().toString().trim();
                route.description = etDescription.getText().toString().trim();
                route.type = sItems.getSelectedItem().toString();
                route.distance = df.format(routeDistance);
                int id = db.insertRoute(route);
                route.route_ID = String.valueOf(id);
                db.close();

                if (id > 0) {
                    for (int i = 0; i < latitudes.length; i++) {
                        db.open();
                        RouteLocation loc = new RouteLocation();
                        loc.route_ID = route.route_ID;
                        loc.latitude = (float) latitudes[i];
                        loc.longitude = (float) longitudes[i];

                        int lid = db.insertLocation(loc);
                        loc.location_ID = String.valueOf(lid);
                        db.close();
                    }
                }

                if (id < 0 ) {
                    Toast.makeText(this, "Route name already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Route " + route.name + " added", Toast.LENGTH_SHORT).show();

                    try {
                        String destPath = "/data/data/" + getPackageName() +
                                "/databases";
                        File f = new File(destPath);
                        if (!f.exists()) {
                            f.mkdirs();
                            f.createNewFile();

                            //---copy the db from the assets folder into
                            // the databases folder---
                            CopyDB(getBaseContext().getAssets().open("trackatraildata"),
                                    new FileOutputStream(destPath + "/TrackATrailData"));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Return to route manager
                    Intent i = new Intent(this, RouteManagerActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("username", inUsername);
                    startActivity(i);
                    finish();
                }
        }
        else {
            // msg stating a field is empty
            Toast.makeText(this,"Please enter data in all fields",Toast.LENGTH_SHORT).show();
        }
    }

    public void CopyDB(InputStream inputStream,
                       OutputStream outputStream) throws IOException {
        //---copy 1K bytes at a time---
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
}
