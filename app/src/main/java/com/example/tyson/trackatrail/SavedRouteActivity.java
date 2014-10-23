package com.example.tyson.trackatrail;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SavedRouteActivity extends TrackATrail {

//    TextView tvName;
//    TextView tvType;
//    TextView tvDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String routeName = "";
        String routeType = "";
        String routeDescription = "";

        setContentView(R.layout.activity_saved_route);

        String inRouteID = getIntent().getExtras().getString("routeID");

//        Cursor routeCursor = db.getAllRoutes();

//        tvName = (TextView)findViewById(R.id.textViewSavedRouteName);
//        //tvType = (TextView)findViewById(R.id.textViewSavedRouteName);
//        tvDescription = (TextView)findViewById(R.id.editTextRouteDescription);

//        if (routeCursor.moveToFirst()) {
//            do {
//                Route refRoute = db.RetrieveRoute(routeCursor);
//
//
//                if (refRoute.route_ID.equals(inRouteID)) {
//
//                    routeName = refRoute.name;
//                    //routeType = refRoute.type;
//                    routeDescription = refRoute.description;
//
//                    break;
//                }
//
//
//
//            } while(routeCursor.moveToNext());
//        }

//        tvName.setText(routeName);
//        tvDescription.setText(routeDescription);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.saved_route, menu);
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
