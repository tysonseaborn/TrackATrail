package com.example.tyson.trackatrail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class RouteManagerActivity extends TrackATrail {

    String inUsername;
    User user;
    private ListView lv;

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

        // Set list view of saved routes
        setContentView(R.layout.activity_route_manager);

        lv = (ListView) findViewById(R.id.routeListView);
        List<String> routeArray = new ArrayList<String>();

        Cursor routeCursor = db.getAllRoutes();

        if (routeCursor.moveToFirst()) {
            do {
                int routeName = routeCursor.getColumnIndex("name");
                routeArray.add(routeCursor.getString(routeName));

            } while(routeCursor.moveToNext());
        }

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                routeArray );

        lv.setAdapter(arrayAdapter);

        db.close();

        // List view item click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(RouteManagerActivity.this, SavedRouteActivity.class);
                String routeID = lv.getItemAtPosition(position).toString();
                intent.putExtra("routeID", routeID);
                startActivity(intent);
            }

        });

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
