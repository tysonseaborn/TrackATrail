package com.example.tyson.trackatrail;

import android.os.Bundle;
import android.view.Menu;

/*
*   Name: About.java class
*   Description: About activity that displays the authors names and year.
*   Authors: Becky Harris, Werner Uetz and Tyson Seaborn
*/

public class About extends TrackATrail {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }
}