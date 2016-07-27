package com.example.jinjinz.concertprev.databases;

import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by jinjinz on 7/25/16.
 */
public final class CurrentSongTable extends MediaContract implements BaseColumns {
    // Content URI represents the base location for the table
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOW).build();


    // These are special type prefixes that specify if a URI returns a list or a specific item
    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_NOW;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_NOW;

    //Current Song playing name/column
    public static final String TABLE_NAME = "currentStatus";
    public static final String COLUMN_CURRENT_SONG_ID = "currSongID";
    public static final String COLUMN_IS_PLAYING = "play";
    //Note: concert is in its own table completely
    public static final String COLUNM_CURRENT_PROGRESS = "progress";

    // Define a function to build a URI to find a specific movie by it's identifier
    public static Uri buildCurrentUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    // Create table SQL string
    private static final String CREATE_CURRENT_SONG_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CURRENT_SONG_ID + " TEXT NOT NULL, "
            + COLUMN_IS_PLAYING + " INTEGER NOT NULL, " + COLUNM_CURRENT_PROGRESS + " INTEGER NOT NULL" + ")";

    // Deletes the entire table (if it exists)
    private static final String DROP_CURRENT_SONG_TABLE = "DROP IF EXISTS " + TABLE_NAME;

    /** Creates the songs table in the SQLite database
     * Runs when database is being created or updated */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_CURRENT_SONG_TABLE); // create the table in the db
    }

    /** Drops and recreates songs table
     *  Runs when database is upgrading */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dbCommands", "Current table. " + "Upgrading db from version " + oldVersion + " to version " + newVersion + ", destroying all data.");
        database.execSQL(DROP_CURRENT_SONG_TABLE);
        onCreate(database);
    }
}
