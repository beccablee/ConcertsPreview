package com.example.jinjinz.concertprev.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SongsTable {

    // songs table name/columns
    public static final String TABLE_NAME = "songs";
    public static final String COLUMN_ENTRY_ID = "_id";
    public static final String COLUMN_SPOTIFY_ID = "spotifyID";
    public static final String COLUMN_SONG_NAME = "name";
    public static final String COLUMN_SONG_ARTISTS = "artists";
    public static final String COLUMN_SONG_PREVIEW_URL = "songURL";
    public static final String COLUMN_ALBUM_ART_URL = "albumArt";

    // Create table SQL string
    private static final String createSongsTable = "CREATE TABLE " +
            TABLE_NAME + "(" +
            COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  // 0
            COLUMN_SPOTIFY_ID + " TEXT NOT NULL, " +                    // 1
            COLUMN_SONG_NAME + " TEXT NOT NULL, " +                     // 2
            COLUMN_SONG_ARTISTS + " TEXT NOT NULL, " +                  // 3
            COLUMN_SONG_PREVIEW_URL + " TEXT NOT NULL, " +              // 4
            COLUMN_ALBUM_ART_URL + " TEXT NOT NULL" + ")";              // 5

    // Deletes the entire table (if it exists)
    private static final String dropSongsTable = "DROP IF EXISTS " + TABLE_NAME;

    /** Creates the songs table in the SQLite database
     * Runs when database is being created or updated */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(createSongsTable); // create the table in the db
    }

    /** Drops and recreates songs table
     *  Runs when database is upgrading */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dbCommands", "Songs table. " + "Upgrading db from version " + oldVersion + " to version " + newVersion + ", destroying all data.");
        database.execSQL(dropSongsTable);
        onCreate(database);
    }
}
