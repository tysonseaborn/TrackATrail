package com.example.tyson.trackatrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    static final String DATABASE_NAME = "TrackATrailData";
    static final String DATABASE_TABLE = "users";
    static final String DATABASE_TABLE_ROUTES = "routes";
    static final String DATABASE_TABLE_ROUTE_LOCATIONS = "routelocations";
    static final int DATABASE_VERSION = 1;
    static final String TAG = "DBAdapter";

    // Create database sql string
    static final String DATABASE_CREATE =
            "create table users (id integer primary key autoincrement, "
                    + "firstname text not null, lastname text not null, username text not null unique,"
                    + "password text not null, email text not null);";
    static final String DATABASE_CREATE_ROUTES =
            "create table routes (id integer primary key autoincrement, user_id integer not null, "
                    + "name text not null unique, description text not null, type text not null,"
                    + "distance text not null, FOREIGN KEY(user_id) REFERENCES users(id));";
    static final String DATABASE_CREATE_ROUTE_LOCATIONS =
            "create table routelocations (id integer primary key autoincrement," +
                    "route_id integer not null, lat float not null, long float not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        // Set database information
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // When creating the database new
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DATABASE_CREATE_ROUTES);
                db.execSQL(DATABASE_CREATE_ROUTE_LOCATIONS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // When calling an update on the database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS routes");
            onCreate(db);
        }
    }

    // Opens the database
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
        return this;
    }

    // Closes the database
    public void close()
    {
        DBHelper.close();
    }

    // Add user into the database
    public int insertUser (User user) {
        ContentValues values = new ContentValues();
        values.put(User.KEY_firstname, user.firstname);
        values.put(User.KEY_lastname, user.lastname);
        values.put(User.KEY_username, user.username);
        values.put(User.KEY_password, user.password);
        values.put(User.KEY_email, user.email);

        long user_id = db.insert(DATABASE_TABLE, null, values);
        return (int) user_id;
    }

    // Retrieves all the users
    public Cursor getAllUsers()
    {
        return db.query(DATABASE_TABLE, new String[] {User.KEY_ID, User.KEY_firstname, User.KEY_lastname,
                User.KEY_username, User.KEY_password, User.KEY_email}, null, null, null, null, null);
    }

    // Updates a user
    public boolean updateUser(User user)
    {
        ContentValues values = new ContentValues();

        values.put(User.KEY_firstname, user.firstname);
        values.put(User.KEY_lastname, user.lastname);
        values.put(User.KEY_username, user.username);
        values.put(User.KEY_email, user.email);

        return db.update(DATABASE_TABLE, values, User.KEY_ID + "=" + user.user_ID, null) > 0;
    }

    // Retrieve the user based off information passed in cursor
    public User RetrieveUser(Cursor c)
    {
        User user = new User();
        user.user_ID = c.getString(0);
        user.firstname = c.getString(1);
        user.lastname = c.getString(2);
        user.username = c.getString(3);
        user.password = c.getString(4);
        user.email = c.getString(5);

        return user;
    }

    // ------------------------------------------------------------------------
    // Routes Database Methods
    public int insertRoute (Route route) {
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Route.KEY_USER_ID, route.user_ID);
        values.put(Route.KEY_name, route.name);
        values.put(Route.KEY_description, route.description);
        values.put(Route.KEY_type, route.type);
        values.put(Route.KEY_distance, route.distance);

        long route_id = db.insert(DATABASE_TABLE_ROUTES, null, values);
        return (int) route_id;
    }

    //---deletes a particular contact---
    public boolean deleteRoute(long rowId)
    {
        return db.delete(DATABASE_TABLE_ROUTES, Route.KEY_ID + "=" + rowId, null) > 0;
    }

    //---retrieves all the routes---
    public Cursor getAllRoutes()
    {
        return db.query(DATABASE_TABLE_ROUTES, new String[] {Route.KEY_ID, Route.KEY_USER_ID, Route.KEY_name, Route.KEY_description,
                Route.KEY_type, Route.KEY_distance}, null, null, null, null, null);
    }

    public Cursor getAllRoutesForUser(String id)
    {
        String whereClause = "user_ID = ?";

        return db.query(DATABASE_TABLE_ROUTES, new String[] {Route.KEY_ID, Route.KEY_USER_ID,  Route.KEY_name, Route.KEY_description,
                Route.KEY_type, Route.KEY_distance}, whereClause, new String[]{id}, null, null, null);
    }

    public boolean updateRoute(Route route)
    {
        ContentValues values = new ContentValues();

        values.put(Route.KEY_name, route.name);
        values.put(Route.KEY_description, route.description);
        values.put(Route.KEY_type, route.type);
        values.put(Route.KEY_distance, route.distance);

        return db.update(DATABASE_TABLE_ROUTES, values, Route.KEY_ID + "=" + route.route_ID, null) > 0;
    }

    public Route RetrieveRoute(Cursor c)
    {
        Route route = new Route();
        route.route_ID = c.getString(0);
        route.user_ID = c.getString(1);
        route.name = c.getString(2);
        route.description = c.getString(3);
        route.type = c.getString(4);
        route.distance = c.getString(5);

        return route;
    }

    // ------------------------------------------------------------------------
    //Location Database Methods
    public int insertLocation(RouteLocation loc) {
        ContentValues values = new ContentValues();
        values.put(RouteLocation.KEY_ROUTE_ID, loc.route_ID);
        values.put(RouteLocation.KEY_lat, loc.latitude);
        values.put(RouteLocation.KEY_long, loc.longitude);

        long route_id = db.insert(DATABASE_TABLE_ROUTE_LOCATIONS, null, values);
        return (int) route_id;
    }
}
