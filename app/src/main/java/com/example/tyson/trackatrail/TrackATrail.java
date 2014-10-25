package com.example.tyson.trackatrail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*
*   Name: TrackATrail.java class
*   Description: Main start up page the houses the login process,
*   as well as the register button.
*   Authors: Becky Harris, Werner Uetz and Tyson Seaborn
*/

public class TrackATrail extends  FragmentActivity {
    DBAdapter db;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_atrail);
        db = new DBAdapter(this);

        // Set the username and password controls
        etUsername = (EditText)findViewById(R.id.usernameInput);
        etPassword = (EditText)findViewById(R.id.passwordInput);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_atrail, menu);
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
        switch(view.getId()) {
            case R.id.buttonRegister:
                Intent iReg = new Intent(this,RegisterActivity.class);
                startActivity(iReg);
                break;
            case R.id.buttonSignin:
                if(login() == true) {
                    Intent iLogin = new Intent(this,MainMenuActivity.class);
                    String sUsername = etUsername.getText().toString().trim();
                    iLogin.putExtra("username", sUsername);

                    startActivity(iLogin);
                }

                break;
        }
    }

    public boolean login() {
        db.open();

        Cursor c = db.getAllUsers();

        if(etUsername.getText().toString().equals("") ||
                etPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter data in all fields", Toast.LENGTH_LONG).show();
        }
        else {
            if (c.moveToFirst()) {
                do {
                    User dbUser = db.RetrieveUser(c);

                    String user = etUsername.getText().toString().trim();
                    String pass = etPassword.getText().toString().trim();
                    if (dbUser.username.equals(user) &&
                            dbUser.password.equals(pass)) {
                        db.close();
                        return true;
                    }
                } while (c.moveToNext());

                Toast.makeText(this, "No user found", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "No user found", Toast.LENGTH_LONG).show();
            }
        }

        db.close();
        return false;
    }

    // Clear the username and password fields to make sure the user is not signed in by anyone else
    @Override
    protected void onPause() {
        etUsername.setText("");
        etPassword.setText("");
        super.onPause();
    }
}
