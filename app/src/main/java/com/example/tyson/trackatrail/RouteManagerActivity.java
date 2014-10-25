package com.example.tyson.trackatrail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
*   Name: RouteManagerActivity.java class
*   Description: Route manager functionality that displays a list view of
*   all saved routes for the user or lets them create a new route.
*   Authors: Becky Harris, Werner Uetz and Tyson Seaborn
*/

public class RouteManagerActivity extends TrackATrail {

    String inUsername;
    User user;
    TextView tvNoRoutes;
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

        db.close();

        populateList();

        // List view item click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(RouteManagerActivity.this, SavedRouteActivity.class);
                String routeName = lv.getItemAtPosition(position).toString();
                intent.putExtra("routeName", routeName);
                intent.putExtra("username", inUsername);
                startActivity(intent);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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

    public void populateList() {
        db.open();

        lv.setAdapter(null); // clear
        List<String> routeArray = new ArrayList<String>();

        Cursor routeCursor = db.getAllRoutesForUser(user.user_ID);
        if (routeCursor.moveToFirst()) {
            do {
                int routeName = routeCursor.getColumnIndex("name");
                routeArray.add(routeCursor.getString(routeName));

            } while(routeCursor.moveToNext());
        }
        else {
            tvNoRoutes = (TextView) findViewById(R.id.textViewNoRoutes);
            tvNoRoutes.setText("No routes found");
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
    }

    @Override
    protected void onResume() {
        populateList();
        super.onResume();
    }

    public void onButtonClick(View view) {
        // go to start new route
        Intent iStart = new Intent(this, StartRouteActivity.class);
        iStart.putExtra("username", inUsername);
        startActivity(iStart);
    }
}
