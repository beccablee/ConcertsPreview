package com.example.jinjinz.concertprev.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jinjinz.concertprev.models.Song;

/**
 * Created by noradiegwu on 7/16/16.
 */
public class UserDataSource {

    private SQLiteDatabase database;
    private UserDatabaseHelper dbHelper;

    public UserDataSource(Context context) {
        dbHelper = new UserDatabaseHelper(context);
    }

    String[] allColumns = {SongsTable.COLUMN_ENTRY_ID, SongsTable.COLUMN_SPOTIFY_ID, SongsTable.COLUMN_SONG_NAME,
            SongsTable.COLUMN_SONG_ARTISTS, SongsTable.COLUMN_SONG_PREVIEW_URL, SongsTable.COLUMN_ALBUM_ART_URL};

    public void openDB() {
        Log.d("dbCommands", "opening " + dbHelper.getDatabaseName() + " from UserDataSource");
        dbHelper.getWritableDatabase();
    }

    public void closeDB() {
        Log.d("dbCommands", "closing " + dbHelper.getDatabaseName() + " from UserDataSource");
        dbHelper.close();
    }

    public Song insertLikedSong(Song likedSong) { // gets song from like button click

        // set values for columns
        ContentValues values = new ContentValues();
        values.put(SongsTable.COLUMN_SPOTIFY_ID, likedSong.getId());
        values.put(SongsTable.COLUMN_SONG_NAME, likedSong.getName());
        values.put(SongsTable.COLUMN_SONG_ARTISTS, likedSong.getArtistsString());
        values.put(SongsTable.COLUMN_SONG_PREVIEW_URL, likedSong.getPreviewUrl());
        values.put(SongsTable.COLUMN_ALBUM_ART_URL, likedSong.getAlbumArtUrl());

        // insert
        long insertID = database.insert(SongsTable.TABLE_NAME, null, values);
        Cursor cursor = database.query(SongsTable.TABLE_NAME, allColumns, SongsTable.COLUMN_ENTRY_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        Song mySong = cursorToSong(cursor);
        cursor.close();
        return mySong;
    }

    public static Song cursorToSong(Cursor cursor) {
        Song song = new Song();
        song.setDbID(cursor.getInt(0)); // db id
        song.setSpotifyID(cursor.getString(1)); // spotify id
        song.setName(cursor.getString(2));
        song.setArtistsString(cursor.getString(3));
        song.setPreviewUrl(cursor.getString(4));
        song.setAlbumArtUrl(cursor.getString(5));
    }

}
