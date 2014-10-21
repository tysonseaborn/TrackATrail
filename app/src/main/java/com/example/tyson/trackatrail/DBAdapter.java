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
    static final int DATABASE_VERSION = 1;
    static final String TAG = "DBAdapter";

    // Create database sql string
    static final String DATABASE_CREATE =
            "create table users (id integer primary key autoincrement, "
                    + "firstname text not null, lastname text not null, username text not null unique,"
                    + "password text not null, email text not null);";

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
            onCreate(db);
        }
    }

    // Opens the database
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
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
}
