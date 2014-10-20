package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class TrackATrail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_atrail);
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
        if(menuItem == "about") {
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        else if(menuItem == "routemanager") {
            Intent routemanager = new Intent(this, RouteManagerActivity.class);
            startActivity(routemanager);
        }
    }


    public void onButtonClick(View view) {
        // register button click, go to register page
        if(view.getId() == R.id.buttonRegister) {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }


}
