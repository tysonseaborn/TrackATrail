package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class StartRouteActivity extends TrackATrail {

    //String username;
    User user;
    String inUsername;

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

        setContentView(R.layout.activity_start_route);
    }

    public void onButtonClick(View view) {
        switch(view.getId()) {
            case R.id.buttonStart:
                // TODO
                break;
            case R.id.buttonSaveRoute:
                Intent iSave = new Intent(this, SaveRouteActivity.class);
                iSave.putExtra("username", inUsername);
                startActivity(iSave);
                break;
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.start_route, menu);
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
