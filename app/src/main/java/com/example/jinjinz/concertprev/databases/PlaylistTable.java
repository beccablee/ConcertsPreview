package com.example.jinjinz.concertprev.databases;

import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by jinjinz on 7/25/16.
 */
public final class PlaylistTable extends MediaContract{
    // Content URI represents the base location for the table
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLIST).build();

    // These are special type prefixes that specify if a URI returns a list or a specific item
    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_PLAYLIST;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_PLAYLIST;

    // playlist table name/columns
    public static final String TABLE_NAME = "playlistTable";
    public static final String COLUMN_ENTRY_ID = "_id";
    public static final String COLUMN_SPOTIFY_ID = "spotifyID";
    public static final String COLUMN_SONG_NAME = "name";
    public static final String COLUMN_SONG_ARTIST = "artist"; //only need first artist right now
    public static final String COLUMN_SONG_PREVIEW_URL = "songURL";
    public static final String COLUMN_ALBUM_ART_URL = "albumArt";

    // Define a function to build a URI to find a specific movie by it's identifier
    public static Uri buildPlaylistUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    // Create table SQL string
    private static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SPOTIFY_ID + " TEXT NOT NULL, "
            + COLUMN_SONG_NAME + " TEXT NOT NULL, " + COLUMN_SONG_ARTIST + " TEXT NOT NULL, " + COLUMN_SONG_PREVIEW_URL + " TEXT NOT NULL, " + COLUMN_ALBUM_ART_URL + " TEXT NOT NULL" + ")";

    // Deletes the entire table (if it exists)
    private static final String DROP_PLAYLIST_TABLE = "DROP IF EXISTS " + TABLE_NAME;

    /** Creates the songs table in the SQLite database
     * Runs when database is being created or updated */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAYLIST_TABLE); // create the table in the db
    }

    /** Drops and recreates songs table
     *  Runs when database is upgrading */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dbCommands", "Songs table. " + "Upgrading db from version " + oldVersion + " to version " + newVersion + ", destroying all data.");
        database.execSQL(DROP_PLAYLIST_TABLE);
        onCreate(database);
    }
}
