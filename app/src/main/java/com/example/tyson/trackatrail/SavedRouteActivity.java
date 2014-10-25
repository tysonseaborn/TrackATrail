package com.example.tyson.trackatrail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class SavedRouteActivity extends TrackATrail {
    Button btnEdit;
    boolean updateValid;
    User user;
    Route route;
    Spinner sItems;
    String inUsername, inRouteName;
    EditText etRouteName, etRouteDesc;
    TextView tvDistance;

    //Map Properties
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_route);

        etRouteName = (EditText)findViewById(R.id.etRouteTitle);
        etRouteDesc = (EditText)findViewById(R.id.editTextSavedRouteDesc);
        tvDistance = (TextView)findViewById(R.id.textViewDistance);
        btnEdit = (Button)findViewById(R.id.btnEditRoute);
        sItems = (Spinner) findViewById(R.id.spinnerEditRouteType);

        updateValid = false;

        // Get the username of the current logged in user and route name
        inRouteName = getIntent().getExtras().getString("routeName");
        inUsername = getIntent().getExtras().getString("username");

        // spinner data
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Walking");
        spinnerArray.add("Jogging");
        spinnerArray.add("Cycling");
        spinnerArray.add("Roller Blading");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);

        db = new DBAdapter(this);
        db.open();

        // Find the user in the database
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

        Cursor routeCursor = db.getAllRoutesForUser(user.user_ID.toString());
        if (routeCursor.moveToFirst()) {
            do {
                Route refRoute = db.RetrieveRoute(routeCursor);

                if (refRoute.name.equals(inRouteName)) {
                    route = refRoute;
                    break;
                }
            } while(routeCursor.moveToNext());
        }

        // Set all the edit text fields to be the route's information
        // Disable on start so user cannot edit unless they choose to
        etRouteName.setText(route.name);
        tvDistance.setText(route.distance + "km");
        etRouteDesc.setText(route.description);
        sItems.setSelection(adapter.getPosition(route.type));

        etRouteName.setEnabled(false);
        etRouteDesc.setEnabled(false);
        sItems.setClickable(false);

        initMap(db.getAllLocationsById(route.route_ID));
        db.close();

    }

    public void initMap(RouteLocation[] rlArray) {
        // Initializing
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if (map != null) {
            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(false);
            map.getUiSettings().setAllGesturesEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);

            for (int i = 0; i < rlArray.length; i++) {
                if (i < (rlArray.length-1)) {
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(rlArray[i].latitude, rlArray[i].longitude),
                                    new LatLng(rlArray[i + 1].latitude, rlArray[i + 1].longitude))
                            .width(5)
                            .color(Color.MAGENTA).geodesic(true));
                }

            }
            if (rlArray.length % 2 == 0) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(rlArray[rlArray.length/2].latitude, rlArray[rlArray.length/2].longitude), 14));
            }
            else {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(rlArray[(int)Math.ceil(rlArray.length/2)].latitude,
                                rlArray[(int)Math.ceil(rlArray.length/2)].longitude), 14));
            }
        }
    }

    public void onButtonClick(View view) {
        // User wants to edit their route information
        if(btnEdit.getText().toString().equals("Edit Route Info")) {
            // Set the route information to be editable
            etRouteName.setEnabled(true);
            etRouteDesc.setEnabled(true);
            sItems.setClickable(true);

            // Distinguishes between the action the user wants to do
            // User is in the process of editing - submit button
            btnEdit.setText("Submit");
        }
        else {
            boolean routeInfoValid = true;

            if(etRouteName.getText().toString().equals("") ||
                    etRouteDesc.getText().toString().equals("")) {
                // A field is empty
                Toast.makeText(this,"Please enter data in all fields",Toast.LENGTH_SHORT).show();
                routeInfoValid = false;
            }

            if(routeInfoValid) {
                popup();
            }
        }
    }

    // Update the route if all checks are valid
    private void updateRoute() {
        if(updateValid) { // Validated password

            route.name = etRouteName.getText().toString();
            route.description = etRouteDesc.getText().toString();
            route.type = sItems.getSelectedItem().toString();

            // Update the user in the database
            db.open();
            db.updateRoute(route);
            db.close();

            // Display message notifying user the update has completed
            Toast.makeText(this, "Route " + route.name + " updated",Toast.LENGTH_LONG).show();

            // Reset fields, button, etc., to default values
            etRouteName.setEnabled(false);
            etRouteDesc.setEnabled(false);
            sItems.setClickable(false);

            // Distinguishes between the action the user wants to do
            // User has finished editing - update button
            btnEdit.setText("Edit Route Info");
            updateValid = false;

        }
    }

    // Handles the password validation popup in order for the user to update
    private void popup() {
        // Get the controls in the password_popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.password_popup, (ViewGroup) findViewById(R.id.root));
        final EditText etPass1 = (EditText) layout.findViewById(R.id.editTextPopupPass);
        final EditText etPass2 = (EditText) layout.findViewById(R.id.editTextPopupConfirm);
        final TextView tvPassMatch = (TextView) layout.findViewById(R.id.textViewPassMatch);

        // Text change listener for password field 2
        // Checks that the two passwords entered are identical and updates error message accordingly
        etPass2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!etPass1.getText().toString().equals("") &&
                        !etPass2.getText().toString().equals("")) {
                    if (etPass1.getText().toString().equals(etPass2.getText().toString())) {
                        tvPassMatch.setText(R.string.passmatch);
                    } else {
                        tvPassMatch.setText(R.string.passnomatch);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Text change listener for password field 1
        // Checks that the two passwords entered are identical and updates error message accordingly
        etPass1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!etPass1.getText().toString().equals("") &&
                        !etPass2.getText().toString().equals("")) {
                    if (etPass1.getText().toString().equals(etPass2.getText().toString())) {
                        tvPassMatch.setText(R.string.passmatch);
                    } else {
                        tvPassMatch.setText(R.string.passnomatch);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Set the alert dialog
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton("Submit",null)
                .setNegativeButton("Cancel", null)
                .setView(layout)
                .setTitle("Confirm Password")
                .setIcon(R.drawable.ic_launcher)
                .create();

        alertDialog.show();

        // User presses Submit button
        // Checks that the information is valid
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Passwords are the same and both are not empty
                if (etPass1.getText().toString().equals(etPass2.getText().toString()) &&
                        !etPass1.getText().toString().equals("") &&
                        !etPass2.getText().toString().equals("")) {
                    // Password is valid to the user's password
                    if(etPass1.getText().toString().equals(user.password)) {
                        // Update the user and close the pop up
                        updateValid = true;
                        updateRoute();
                        alertDialog.dismiss();
                        return;
                    }
                    else {
                        // Password is not the user's password
                        updateValid = false;
                        tvPassMatch.setText("Invalid password");
                        return;
                    }
                }
                else {
                    if(etPass1.getText().toString().equals("") ||
                            etPass2.getText().toString().equals("")) { // A field is empty
                        tvPassMatch.setText("No fields can be empty");
                    } else { // Passwords entered are not the same
                        tvPassMatch.setText(R.string.passnomatch);
                    }
                    return;
                }

            }
        });

        // User presses Cancel button
        // Cancels out of popup
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValid = false;
                alertDialog.dismiss();
            }
        });
    }
}
