package com.example.jinjinz.concertprev.databases;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ConcertsTable {

    // concerts table name/columns
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
    public static final String COLUMN_CONCERT_TICKET_LINK = "tickets";


    // Create table SQL string
    private static final String createConcertsTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONCERT_NAME + " TEXT NOT NULL, "
            + COLUMN_CONCERT_CITY + " TEXT, " + COLUMN_CONCERT_STATE + " DEFAULT NULL, " + COLUMN_CONCERT_COUNTRY + " TEXT, "
            + COLUMN_CONCERT_VENUE + " DEFAULT NULL, "  + COLUMN_CONCERT_TIME + " TEXT, " + COLUMN_CONCERT_DATE + " TEXT, "
            + COLUMN_CONCERT_ARTISTS + " TEXT, " + COLUMN_CONCERT_IMAGE_URL + " TEXT, " + COLUMN_CONCERT_TICKET_LINK + " DEFAULT NULL" + ")";

    // Deletes the entire table (if it exists)
    private static final String dropConcertsTable = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /** Creates the concerts table in the SQLite database
     * Runs when database is being created or updated */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(createConcertsTable);
    }

    /** Drops and recreates concerts table
     *  Runs when database is upgrading */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dbCommands", "Concerts table. " + "Upgrading db from version " + oldVersion + " to version " + newVersion + ", destroying all data.");
        database.execSQL(dropConcertsTable);
        onCreate(database);
    }
}
