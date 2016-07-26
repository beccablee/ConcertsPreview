package com.example.jinjinz.concertprev.databases;

import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by jinjinz on 7/25/16.
 */
public final class CurrentConcertTable extends MediaContract {
    // Content URI represents the base location for the table
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONCERT).build();

    // These are special type prefixes that specify if a URI returns a list or a specific item
    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_CONCERT;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONCERT;

    //Current concerts table name/columns
    public static final String COLUMN_ENTRY_ID = "_id";
    public static final String TABLE_NAME = "concerts";
    public static final String COLUMN_CONCERT_NAME = "name";
    public static final String COLUMN_CONCERT_CITY = "city";
    public static final String COLUMN_CONCERT_STATE = "state";
    public static final String COLUMN_CONCERT_COUNTRY = "country";
    public static final String COLUMN_CONCERT_TIME = "time";
    public static final String COLUMN_CONCERT_DATE = "date";
    public static final String COLUMN_CONCERT_VENUE = "venue";
    public static final String COLUMN_CONCERT_ARTISTS = "artists";
    public static final String COLUMN_CONCERT_IMAGE_URL = "imageURL";

    // Define a function to build a URI to find a specific movie by it's identifier
    public static Uri buildCurrConcertUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    // Create table SQL string
    private static final String createCurrentConcertsTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONCERT_NAME + " TEXT NOT NULL, "
            + COLUMN_CONCERT_CITY + " TEXT NOT NULL, " + COLUMN_CONCERT_STATE + " TEXT NOT NULL, " + COLUMN_CONCERT_COUNTRY + " TEXT NOT NULL, "
            + COLUMN_CONCERT_VENUE + " TEXT NOT NULL, "  + COLUMN_CONCERT_TIME + " TEXT NOT NULL, " + COLUMN_CONCERT_DATE + " TEXT NOT NULL, "
            + COLUMN_CONCERT_ARTISTS + " TEXT NOT NULL, " + COLUMN_CONCERT_IMAGE_URL + " TEXT NOT NULL" + ")";

    // Deletes the entire table (if it exists)
    private static final String dropCurrentConcertsTable = "DROP IF EXISTS " + TABLE_NAME;

    /** Creates the concerts table in the SQLite database
     * Runs when database is being created or updated */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(createCurrentConcertsTable);
    }

    /** Drops and recreates concerts table
     *  Runs when database is upgrading */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dbCommands", "Current concerts table. " + "Upgrading db from version " + oldVersion + " to version " + newVersion + ", destroying all data.");
        database.execSQL(dropCurrentConcertsTable);
        onCreate(database);
    }
}
