package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
*   Name: RegisterActivity.java class
*   Description: Register functionality that the user fills out to create a new profile.
*   Authors: Becky Harris, Werner Uetz and Tyson Seaborn
*/

public class RegisterActivity extends TrackATrail {
    EditText etFirstName, etLastName, etUsername, etPassword, etConfirm, etEmail;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DBAdapter(this);
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

    // Register button selected
    public void onButtonClick(View view) {
        // Get the values in all fields
        etFirstName = (EditText)findViewById(R.id.editTextFirstName);
        etLastName = (EditText)findViewById(R.id.editTextLastName);
        etUsername = (EditText)findViewById(R.id.editTextUsername);
        etPassword = (EditText)findViewById(R.id.editTextPassword);
        etConfirm = (EditText)findViewById(R.id.editTextConfirm);
        etEmail = (EditText)findViewById(R.id.editTextEmail);

        // check that all fields are filled out
        if(!etFirstName.getText().toString().equals("") &&
                !etLastName.getText().toString().equals("") &&
                !etUsername.getText().toString().equals("") &&
                !etPassword.getText().toString().equals("") &&
                !etConfirm.getText().toString().equals("") &&
                !etEmail.getText().toString().equals("")) {
            // check that there are no spaces in any fields
            if (!etUsername.getText().toString().contains(" ") &&
                    !etFirstName.getText().toString().contains(" ") &&
                    !etLastName.getText().toString().contains(" ") &&
                    !etEmail.getText().toString().contains(" ") &&
                    !etPassword.getText().toString().contains(" ")) {
                // check that the confirm password is valid
                if (etPassword.getText().toString().equals(etConfirm.getText().toString())) {
                    // check that the email is valid
                    if (isValidEmail(etEmail.getText())) {
                        // Open database connection
                        db.open();

                        // Set the user information to all the data entered
                        User user = new User();
                        user.firstname = etFirstName.getText().toString();
                        user.lastname = etLastName.getText().toString();
                        user.username = etUsername.getText().toString();
                        user.password = etPassword.getText().toString();
                        user.email = etEmail.getText().toString();

                        // Insert user to database
                        int id = db.insertUser(user);
                        user.user_ID = String.valueOf(id);

                        // Close database connection
                        db.close();

                        if (id < 0) { // Username already exists in database
                            Toast.makeText(this, "Username already exists, please choose a new username", Toast.LENGTH_SHORT).show();
                        } else { // New user created
                            Toast.makeText(this, "User " + user.username + " added", Toast.LENGTH_SHORT).show();

                            // Copy the database to databases folder
                            try {
                                String destPath = "/data/data/" + getPackageName() +
                                        "/databases";
                                File f = new File(destPath);
                                if (!f.exists()) {
                                    f.mkdirs();
                                    f.createNewFile();

                                    CopyDB(getBaseContext().getAssets().open("trackatraildata"),
                                            new FileOutputStream(destPath + "/TrackATrailData"));
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finish();
                        }
                    } else {
                        // Email is in an invalid format
                        Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Passwords do not match
                    Toast.makeText(this, "Password and Confirm password do not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Space exists in any field
                Toast.makeText(this, "No spaces allowed in any fields", Toast.LENGTH_SHORT).show();
            }
        } else {
            // A field is empty
            Toast.makeText(this,"Please enter data in all fields",Toast.LENGTH_SHORT).show();
        }
    }

    // Copies the database to outputStream
    public void CopyDB(InputStream inputStream,
                       OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
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
