package com.example.tyson.trackatrail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
*   Name: ProfileMenuActivity.java class
*   Description: Profile page that lets the user view and change their personal information.
*   Authors: Becky Harris, Werner Uetz and Tyson Seaborn
*/

public class ProfileMenuActivity extends TrackATrail {
    boolean updateValid;
    DBAdapter db;
    User user;
    EditText etUsername, etFirstName, etLastName, etEmail;
    Button btnUporSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);
        updateValid = false;

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

        // Set all the edit text fields to be the user's information
        // Disable on start so user cannot edit unless they choose to
        etUsername = (EditText)findViewById(R.id.editTextProfUsername);
        etFirstName = (EditText)findViewById(R.id.editTextProfFirstName);
        etLastName = (EditText)findViewById(R.id.editTextProfLastName);
        etEmail = (EditText)findViewById(R.id.editTextProfEmail);
        btnUporSub = (Button)findViewById(R.id.btnEdit);

        // Set all text fields given the user data
        etUsername.setText(user.username);
        etFirstName.setText(user.firstname);
        etLastName.setText(user.lastname);
        etEmail.setText(user.email);

        etUsername.setEnabled(false);
        etFirstName.setEnabled(false);
        etLastName.setEnabled(false);
        etEmail.setEnabled(false);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
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
        // User wants to edit their information
        if(btnUporSub.getText().toString().equals("Update")) {
            // Set their information to be editable
            etFirstName.setEnabled(true);
            etLastName.setEnabled(true);
            etEmail.setEnabled(true);

            // Distinguishes between the action the user wants to do
            // User is in the process of editing - submit button
            btnUporSub.setText("Submit");
        }
        else {
            boolean userInfoValid = true;

            // Information validation
            // Empty check
            if(etFirstName.getText().toString().equals("") ||
                    etLastName.getText().toString().equals("") ||
                    etEmail.getText().toString().equals("")) {
                // A field is empty
                Toast.makeText(this,"Please enter data in all fields",Toast.LENGTH_SHORT).show();
                userInfoValid = false;
            }
            else {
                // Space check
                if (etFirstName.getText().toString().contains(" ") ||
                        etLastName.getText().toString().contains(" ") ||
                        etEmail.getText().toString().contains(" ")) {
                    // Space exists in any field
                    Toast.makeText(this, "No spaces allowed in any fields", Toast.LENGTH_SHORT).show();
                    userInfoValid = false;
                }
                else {
                    // Email check
                    if (!isValidEmail(etEmail.getText())) {
                        // Email is in an invalid format
                        Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
                        userInfoValid = false;
                    }
                }
            }

            // Information entered is valid, open the password check pop up
            if(userInfoValid) {
                popup();
            }
        }
    }

    // Update the user if all checks are valid
    private void updateUser() {
        if(updateValid) { // Validated password
            user.firstname = etFirstName.getText().toString();
            user.lastname = etLastName.getText().toString();
            user.email = etEmail.getText().toString();

            // Update the user in the database
            db.open();
            db.updateUser(user);
            db.close();

            // Display message notifying user the update has completed
            Toast.makeText(this, "User " + user.username + " updated",Toast.LENGTH_LONG).show();

            // Reset fields, button, etc., to default values
            etFirstName.setEnabled(false);
            etLastName.setEnabled(false);
            etEmail.setEnabled(false);

            // Distinguishes between the action the user wants to do
            // User has finished editing - update button
            btnUporSub.setText("Update");
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
                        updateUser();
                        alertDialog.dismiss();
                    }
                    else {
                        // Password is not the user's password
                        updateValid = false;
                        tvPassMatch.setText("Invalid password");
                    }
                }
                else {
                    if(etPass1.getText().toString().equals("") ||
                            etPass2.getText().toString().equals("")) { // A field is empty
                        tvPassMatch.setText("No fields can be empty");
                    } else { // Passwords entered are not the same
                        tvPassMatch.setText(R.string.passnomatch);
                    }
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

    // Checks that the user email is a valid email
    public boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
