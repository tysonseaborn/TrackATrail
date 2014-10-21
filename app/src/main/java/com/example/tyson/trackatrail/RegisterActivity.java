package com.example.tyson.trackatrail;

import android.app.Activity;
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

public class RegisterActivity extends TrackATrail {
    //private DBHelper dbHelper;
    EditText etFirstName, etLastName, etUsername, etPassword, etConfirm, etEmail;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DBAdapter(this);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.register, menu);
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

    public void onButtonClick(View view) {
        etFirstName = (EditText)findViewById(R.id.editTextFirstName);
        etLastName = (EditText)findViewById(R.id.editTextLastName);
        etUsername = (EditText)findViewById(R.id.editTextUsername);
        etPassword = (EditText)findViewById(R.id.editTextPassword);
        etConfirm = (EditText)findViewById(R.id.editTextConfirm);
        etEmail = (EditText)findViewById(R.id.editTextEmail);

        // check passwords are same and that all fields are filled out
        if(!etFirstName.getText().toString().equals("") &&
                !etLastName.getText().toString().equals("") &&
                !etUsername.getText().toString().equals("") &&
                !etPassword.getText().toString().equals("") &&
                !etConfirm.getText().toString().equals("") &&
                !etEmail.getText().toString().equals("")) {

            // check that the confirm password is valid
            if (etPassword.getText().toString().equals(etConfirm.getText().toString())) {
                db.open();
                User user = new User();
                user.firstname = etFirstName.getText().toString();
                user.lastname = etLastName.getText().toString();
                user.username = etUsername.getText().toString();
                user.password = etPassword.getText().toString();
                user.email = etEmail.getText().toString();
                int id = db.insertUser(user);
                user.user_ID = String.valueOf(id);
                db.close();

                if (id < 0 ) {
                    Toast.makeText(this, "Username already exists, please choose a new username", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "User " + user.username + " added", Toast.LENGTH_SHORT).show();

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
                }

            } else {
                // msg stating they aren't the same
                Toast.makeText(this, "Password and Confirm password are not the same", Toast.LENGTH_SHORT).show();
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
