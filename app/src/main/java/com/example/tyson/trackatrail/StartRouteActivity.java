package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class StartRouteActivity extends TrackATrail {

    boolean startedTracking, currentlyTracking = false;
    Button btnStart, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_route);
        btnStart = (Button)findViewById(R.id.buttonStart);
        btnSave = (Button)findViewById(R.id.buttonSaveRoute);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_route, menu);
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
            case R.id.buttonStart:
                startTracking();
                break;
            case R.id.buttonSaveRoute:
                Intent iSave = new Intent(this, SaveRouteActivity.class);
                startActivity(iSave);
                break;
        }
    }

    public void startTracking() {
        startedTracking = true;

        if(btnStart.getText() == "Start Tracking") {
            btnStart.setText("Stop Tracking");
            btnSave.setEnabled(true);
            currentlyTracking = true;
        }
        else {
            btnStart.setText("Start Tracking");
            btnSave.setEnabled(false);
            currentlyTracking = false;
        }

    }
}
