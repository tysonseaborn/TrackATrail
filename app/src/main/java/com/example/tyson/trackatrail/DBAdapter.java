package com.example.tyson.trackatrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Becky on 20/10/2014.
 */
public class DBAdapter {
    static final String DATABASE_NAME = "TrackATrailData";
    static final String DATABASE_TABLE = "users";
    static final int DATABASE_VERSION = 1;
    static final String TAG = "DBAdapter";

    static final String DATABASE_CREATE =
            "create table users (id integer primary key autoincrement, "
                    + "firstname text not null, lastname text not null, username text not null unique,"
                    + "password text not null, email text not null);";

    //private DBHelper dbHelper;
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        //dbHelper = new DBHelper(context);
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    public int insertUser (User user) {
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_firstname, user.firstname);
        values.put(User.KEY_lastname, user.lastname);
        values.put(User.KEY_username, user.username);
        values.put(User.KEY_password, user.password);
        values.put(User.KEY_email, user.email);

        long user_id = db.insert(DATABASE_TABLE, null, values);
        return (int) user_id;
    }

    //---deletes a particular contact---
    public boolean deleteUser(long rowId)
    {
        return db.delete(DATABASE_TABLE, User.KEY_ID + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllUsers()
    {
        return db.query(DATABASE_TABLE, new String[] {User.KEY_ID, User.KEY_firstname, User.KEY_lastname,
                User.KEY_username, User.KEY_password, User.KEY_email}, null, null, null, null, null);
    }

    /*** DOES NOT WORK CURRENTLY ***
    //---retrieves a particular contact---
    public Cursor getUser(String username) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {User.KEY_ID, User.KEY_firstname,
                                User.KEY_lastname, User.KEY_username, User.KEY_password, User.KEY_email}, User.KEY_username + "=" + username, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    */

    // Updates a user
    public boolean updateUser(User user)
    {
        ContentValues values = new ContentValues();

        values.put(User.KEY_firstname, user.firstname);
        values.put(User.KEY_lastname, user.lastname);
        values.put(User.KEY_username, user.username);
        values.put(User.KEY_password, user.password);
        values.put(User.KEY_email, user.email);

        return db.update(DATABASE_TABLE, values, User.KEY_ID + "=" + user.user_ID, null) > 0;
    }

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