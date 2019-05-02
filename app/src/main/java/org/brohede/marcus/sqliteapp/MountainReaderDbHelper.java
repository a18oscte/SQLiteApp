package org.brohede.marcus.sqliteapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marcus on 2018-04-25.
 */


public class MountainReaderDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MountainReaderContract.MountainEntry.TABLE_NAME + " (" +
                    MountainReaderContract.MountainEntry._ID + " INTEGER PRIMARY KEY," +
                    MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " TEXT," +
                    MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION  + " TEXT," +
                    MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH + " TEXT," +
                    MountainReaderContract.MountainEntry.COLUMN_NAME_BILD+ " TEXT," +
                    MountainReaderContract.MountainEntry.COLUMN_NAME_URL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MountainReaderContract.MountainEntry.TABLE_NAME;

    // TODO: You need to add member variables and methods to this helper class
    // See: https://developer.android.com/training/data-storage/sqlite.html#DbHelper
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public MountainReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
