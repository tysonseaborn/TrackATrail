package com.example.tyson.trackatrail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;


public class SavedRouteActivity extends TrackATrail {
    Button btnEdit;
    boolean updateValid;
    User user;
    Spinner sItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String routeName = "";
        String routeType = "";
        String routeDescription = "";

        setContentView(R.layout.activity_saved_route);

        String inRouteID = getIntent().getExtras().getString("routeID");

//        Cursor routeCursor = db.getAllRoutes();

//        tvName = (TextView)findViewById(R.id.textViewSavedRouteName);
//        //tvType = (TextView)findViewById(R.id.textViewSavedRouteName);
//        tvDescription = (TextView)findViewById(R.id.editTextRouteDescription);

//        if (routeCursor.moveToFirst()) {
//            do {
//                Route refRoute = db.RetrieveRoute(routeCursor);
//
//
//                if (refRoute.route_ID.equals(inRouteID)) {
//
//                    routeName = refRoute.name;
//                    //routeType = refRoute.type;
//                    routeDescription = refRoute.description;
//
//                    break;
//                }
//
//
//
//            } while(routeCursor.moveToNext());
//        }

//        tvName.setText(routeName);
//        tvDescription.setText(routeDescription);

        btnEdit = (Button)findViewById(R.id.btnEditRoute);

        updateValid = false;

        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Walking");
        spinnerArray.add("Jogging");
        spinnerArray.add("Cycling");
        spinnerArray.add("Roller Blading");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.spinnerEditRouteType);
        sItems.setAdapter(adapter);

        db = new DBAdapter(this);
        db.open();

        // Get the username of the current logged in user
        String inUsername = getIntent().getExtras().getString("username");

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

        db.close();
    }

    public void onButtonClick(View view) {
        // User wants to edit their route information
        if(btnEdit.getText().toString().equals("Edit Route")) {
            // Set the route information to be editable


            // Distinguishes between the action the user wants to do
            // User is in the process of editing - submit button
            btnEdit.setText("Submit");
        }
        else {
            boolean routeInfoValid = true;

            if(routeInfoValid) {
                popup();
            }
        }
    }

    // Update the user if all checks are valid
    private void updateRoute() {
        if(updateValid) { // Validated password
            /*
            user.username = etUsername.getText().toString();
            user.firstname = etFirstName.getText().toString();
            user.lastname = etLastName.getText().toString();
            user.email = etEmail.getText().toString();

            // Update the user in the database
            db.open();
            db.updateRoute(route);
            db.close();

            // Display message notifying user the update has completed
            Toast.makeText(this, "User " + user.username + " updated",Toast.LENGTH_LONG).show();

            // Reset fields, button, etc., to default values
            etUsername.setEnabled(false);
            etFirstName.setEnabled(false);
            etLastName.setEnabled(false);
            etEmail.setEnabled(false);
*/

            // Distinguishes between the action the user wants to do
            // User has finished editing - update button
            btnEdit.setText("Update");
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
