package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class RouteManagerActivity extends TrackATrail {

    String inUsername;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_route_manager);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.route_manager, menu);
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
        // go to start new route
        Intent iStart = new Intent(this, StartRouteActivity.class);
        iStart.putExtra("username", inUsername);
        startActivity(iStart);
    }
}
