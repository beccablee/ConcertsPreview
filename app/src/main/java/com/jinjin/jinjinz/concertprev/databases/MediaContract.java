package com.jinjin.jinjinz.concertprev.databases;

import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jinjinz on 7/25/16.
 * Contract class that outlines the media database
 */
public final class MediaContract {
    // The Content Authority is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.jinjinz.concertprev.databases";
    // the base of all URIs which apps will use to contact this content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //A list of possible paths that will be appended to the base URI
    public static final String PATH_PLAYLIST = "playlist";
    public static final String PATH_NOW = "current";
    public static final String PATH_CONCERT = "thisConcert";

    /**
     * current concert table
     */
    public static final class CurrentConcertTable implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONCERT).build();
        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_CONCERT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONCERT;
        //Current concerts table name/columns
        public static final String TABLE_NAME = "concertTable";
        public static final String COLUMN_CONCERT_NAME = "name";
        public static final String COLUMN_CONCERT_CITY = "city";
        public static final String COLUMN_CONCERT_STATE = "state";
        public static final String COLUMN_CONCERT_COUNTRY = "country";
        public static final String COLUMN_CONCERT_TIME = "time";
        public static final String COLUMN_CONCERT_DATE = "date";
        public static final String COLUMN_CONCERT_VENUE = "venue";
        public static final String COLUMN_CONCERT_ARTISTS = "artists";
        public static final String COLUMN_CONCERT_IMAGE_URL = "imageURL";
        public static final String COLUNM_CONCERT_LIKED = "dbID";
        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildCurrConcertUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        // Create table SQL string
        private static final String createCurrentConcertsTable = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONCERT_NAME + " TEXT, "
                + COLUMN_CONCERT_CITY + " TEXT, " + COLUMN_CONCERT_STATE + " TEXT, " + COLUMN_CONCERT_COUNTRY + " TEXT, "
                + COLUMN_CONCERT_VENUE + " TEXT, "  + COLUMN_CONCERT_TIME + " TEXT, " + COLUMN_CONCERT_DATE + " TEXT, "
                + COLUMN_CONCERT_ARTISTS + " TEXT, " + COLUMN_CONCERT_IMAGE_URL + " TEXT, " + COLUNM_CONCERT_LIKED +  " INTEGER" + ");";
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
            database.execSQL(dropCurrentConcertsTable);
            onCreate(database);
        }
    }
    /**
     * current song table
     */
    public static final class CurrentSongTable implements BaseColumns {
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
        public static final String COLUNM_CURRENT_PROGRESS = "progress";
        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildCurrentUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        // Create table SQL string
        private static final String CREATE_CURRENT_SONG_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CURRENT_SONG_ID + " INTEGER, "
                + COLUMN_IS_PLAYING + " INTEGER NOT NULL, " + COLUNM_CURRENT_PROGRESS + " INTEGER NOT NULL" + ");";
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
            database.execSQL(DROP_CURRENT_SONG_TABLE);
            onCreate(database);
        }
    }
    /**
     * playlist song table
     */
    public static final class PlaylistTable implements BaseColumns{
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
        public static final String COLUMN_SPOTIFY_ID = "spotifyID";
        public static final String COLUMN_SONG_NAME = "name";
        public static final String COLUMN_SONG_ARTIST = "artist"; //only need first artist right now
        public static final String COLUMN_SONG_PREVIEW_URL = "songURL";
        public static final String COLUMN_ALBUM_ART_URL = "albumArt";
        public static final String COLUMN_LIKED = "liked";
        // Define a function to build a URI to find a specific song by it's identifier
        public static Uri buildPlaylistUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        // Create table SQL string
        private static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SPOTIFY_ID + " TEXT, "
                + COLUMN_SONG_NAME + " TEXT, " + COLUMN_SONG_ARTIST + " TEXT, " + COLUMN_SONG_PREVIEW_URL + " TEXT NOT NULL, " + COLUMN_ALBUM_ART_URL + " TEXT, " + COLUMN_LIKED + " INTEGER"+ ");";
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
            database.execSQL(DROP_PLAYLIST_TABLE);
            onCreate(database);
        }
    }
}
