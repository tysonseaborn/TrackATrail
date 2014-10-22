package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainMenuActivity extends Activity {
    User user;
    DBAdapter db;
    String inUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        db = new DBAdapter(this);

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
        Toast.makeText(this, "Welcome " + user.username, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            case R.id.btnProfile:
                // Pass in the user's username to the profile activity
                Intent iProfile = new Intent(this,ProfileMenuActivity.class);
                iProfile.putExtra("username", inUsername);

                startActivity(iProfile);
                break;
            case R.id.btnMyRoutes:
                Intent iRoutes = new Intent(this, RouteManagerActivity.class);
                iRoutes.putExtra("username", inUsername);
                startActivity(iRoutes);
                break;
        }
    }
}
