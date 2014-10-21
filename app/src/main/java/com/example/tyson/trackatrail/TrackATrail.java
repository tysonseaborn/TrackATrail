package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class TrackATrail extends Activity {
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_atrail);
        db = new DBAdapter(this);
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
        else if (id == R.id.action_route_manager) {
            openMenu("routemanager");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openMenu(String menuItem) {
        if(menuItem.equals("about")) {
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        else if(menuItem.equals("routemanager")) {
            Intent routemanager = new Intent(this, RouteManagerActivity.class);
            startActivity(routemanager);
        }
    }


    public void onButtonClick(View view) {
        switch(view.getId()) {
            case R.id.buttonRegister:
                Intent iReg = new Intent(this,RegisterActivity.class);
                startActivity(iReg);
                break;
            case R.id.buttonSignin:
                EditText etUsername = (EditText)findViewById(R.id.usernameInput);

                if(login()) {
                    Intent iLogin = new Intent(this,MainMenuActivity.class);
                    String sUsername = etUsername.getText().toString();
                    iLogin.putExtra("username",sUsername);

                    startActivity(iLogin);
                }

                break;
        }
    }

    public boolean login() {
        EditText etUsername = (EditText)findViewById(R.id.usernameInput);
        EditText etPassword = (EditText)findViewById(R.id.passwordInput);

        db.open();

        Cursor c = db.getAllUsers();

        if(etUsername.getText().toString().equals("") ||
                etPassword.getText().toString().equals("")) {

            if (c.moveToFirst()) {
                do {
                    User dbUser = db.RetrieveUser(c);

                    // *** DELETE WHEN DONE TESTING ***
                    Toast.makeText(this,
                            dbUser.user_ID + "\n" +
                            dbUser.username + "\n" +
                            dbUser.password
                            ,Toast.LENGTH_SHORT).show();
                } while (c.moveToNext());
            }

            Toast.makeText(this, "Please enter data in all fields", Toast.LENGTH_LONG).show();
        }
        else {
            if (c.moveToFirst()) {
                do {
                    User dbUser = db.RetrieveUser(c);

                    if (dbUser.username.equals(etUsername.getText().toString()) &&
                            dbUser.password.equals(etPassword.getText().toString())) {
                        db.close();
                        return true;
                    }
                } while (c.moveToNext());

                Toast.makeText(this, "No user found", Toast.LENGTH_LONG).show();
            }
        }

        db.close();
        return false;
    }
}
