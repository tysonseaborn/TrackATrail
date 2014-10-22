package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.ArrayList;
import java.util.List;


public class SaveRouteActivity extends TrackATrail {

    EditText etName, etDescription;
    TextView tvDistance, tvTime;
    Spinner sItems;
    User user;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_route);
        db = new DBAdapter(this);

        db.open();
        String inUsername = getIntent().getExtras().getString("username");

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


        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Walking");
        spinnerArray.add("Jogging");
        spinnerArray.add("Cycling");
        spinnerArray.add("Roller Blading");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.spinnerRouteType);
        sItems.setAdapter(adapter);

    }

    public void onButtonClick(View view) {
        etName = (EditText)findViewById(R.id.editTextRouteName);
        etDescription = (EditText)findViewById(R.id.editTextRouteDescription);
        sItems = (Spinner)findViewById(R.id.spinnerRouteType);
        tvDistance = (TextView)findViewById(R.id.textViewRouteDistance);
        tvTime = (TextView)findViewById(R.id.textViewRouteTime);

        // check passwords are same and that all fields are filled out
        if(!etName.getText().toString().equals("") &&
                !etDescription.getText().toString().equals("")) {

                db.open();
                Route route = new Route();
                //route.user_ID = "1";
                route.user_ID = user.user_ID;
                route.name = etName.getText().toString();
                route.description = etDescription.getText().toString();
                route.type = sItems.getSelectedItem().toString();
                route.distance = tvDistance.getText().toString();
                route.time = tvTime.getText().toString();
                int id = db.insertRoute(route);
                route.route_ID = String.valueOf(id);
                db.close();

                if (id < 0 ) {
                    Toast.makeText(this, "Route not added", Toast.LENGTH_SHORT).show();
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.save_route, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
