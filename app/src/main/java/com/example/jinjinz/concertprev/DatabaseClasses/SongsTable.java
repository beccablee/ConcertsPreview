package com.example.jinjinz.concertprev.DatabaseClasses;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by noradiegwu on 7/18/16.
 */
public class SongsTable {

    // songs table name/columns
    public static final String TABLE_NAME = "songs";
    public static final String COLUMN_ENTRY_ID = "_id";
    public static final String COLUMN_SPOTIFY_ID = "spotifyID";
    public static final String COLUMN_SONG_NAME = "name";
    // public static final String COLUMN_MAIN_ARTIST = "mainArtist"; // if i plan to split for better layout (ex. Chance the Rapper ft. Saba // but also probs won't
    public static final String COLUMN_SONG_ARTISTS = "artists";
    public static final String COLUMN_SONG_PREVIEW_URL = "songURL";
    public static final String COLUMN_ALBUM_ART_URL = "albumArt";

    // Create table SQL string
    private static final String createSongsTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SPOTIFY_ID + " TEXT NOT NULL, "
            + COLUMN_SONG_NAME + " TEXT NOT NULL, " + COLUMN_SONG_ARTISTS + " TEXT NOT NULL, " + COLUMN_SONG_PREVIEW_URL + " TEXT NOT NULL, " + COLUMN_ALBUM_ART_URL + " TEXT NOT NULL" + ")";

    // Deletes the entire table (if it exists)
    private static final String dropSongsTable = "DROP IF EXISTS " + TABLE_NAME;

    // onCreate of DB
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(createSongsTable); // create the table in the db
    }

    // onUpgrade (of DB)
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dbCommands", "Songs table. " + "Upgrading db from version " + oldVersion + " to version " + newVersion + ", destroying all data.");
        database.execSQL(dropSongsTable);
        onCreate(database);
    }
}
