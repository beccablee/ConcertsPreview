package com.example.jinjinz.concertprev.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import java.util.ArrayList;

public class UserDataSource { // Our DAO (data access object) that is responsible for handling db connection and accessing and modifying the data in the db
    // it also converts the data into objects that our app can be readily used for our ui

    private SQLiteDatabase database;
    private UserDatabaseHelper dbHelper;

    public UserDataSource(Context context) {
        dbHelper = new UserDatabaseHelper(context);
    }

    public UserDataSource() {

    }

    // all the columns in the song table
    String[] allSongColumns = {SongsTable.COLUMN_ENTRY_ID, SongsTable.COLUMN_SPOTIFY_ID, SongsTable.COLUMN_SONG_NAME,
            SongsTable.COLUMN_SONG_ARTISTS, SongsTable.COLUMN_SONG_PREVIEW_URL, SongsTable.COLUMN_ALBUM_ART_URL};

    // all the columns in the concert table
    String [] allConcertColumns = {ConcertsTable.COLUMN_ENTRY_ID,
            ConcertsTable.COLUMN_CONCERT_NAME,
            ConcertsTable.COLUMN_CONCERT_CITY,
            ConcertsTable.COLUMN_CONCERT_STATE,
            ConcertsTable.COLUMN_CONCERT_COUNTRY,
            ConcertsTable.COLUMN_CONCERT_VENUE,
            ConcertsTable.COLUMN_CONCERT_TIME,
            ConcertsTable.COLUMN_CONCERT_DATE,
            ConcertsTable.COLUMN_CONCERT_ARTISTS,
            ConcertsTable.COLUMN_CONCERT_IMAGE_URL};

    public void openDB() {
        Log.d("dbCommands", "opening " + dbHelper.getDatabaseName() + " from UserDataSource");
        database = dbHelper.getWritableDatabase();
    }

    public void closeDB() {
        Log.d("dbCommands", "closing " + dbHelper.getDatabaseName() + " from UserDataSource");
        dbHelper.close();
    }

    // insert concert to db
    public Concert insertLikedConcert(Concert concert) { //gets concert from like button click

        // set key-value pairs for columns of concert table
        ContentValues values = new ContentValues();
        values.put(ConcertsTable.COLUMN_CONCERT_NAME, concert.getEventName());
        values.put(ConcertsTable.COLUMN_CONCERT_CITY, concert.getCity());
        values.put(ConcertsTable.COLUMN_CONCERT_STATE, concert.getStateCode());
        values.put(ConcertsTable.COLUMN_CONCERT_COUNTRY, concert.getCountryCode());
        values.put(ConcertsTable.COLUMN_CONCERT_TIME, concert.getEventTime());
        values.put(ConcertsTable.COLUMN_CONCERT_DATE, concert.getEventDate());
        values.put(ConcertsTable.COLUMN_CONCERT_VENUE, concert.getVenue());
        values.put(ConcertsTable.COLUMN_CONCERT_ARTISTS, concert.getArtistsString());
        values.put(ConcertsTable.COLUMN_CONCERT_IMAGE_URL, concert.getBackdropImage());

        // insert and return the concert
        long insertId = database.insert(ConcertsTable.TABLE_NAME, ConcertsTable.COLUMN_CONCERT_STATE, values); // inserts values in every column for the new row (liked concert) and returns the row id of the inserted concert ( or -1 if an error occurred)
        if (insertId != -1) {
            Cursor cursor = database.query(ConcertsTable.TABLE_NAME, allConcertColumns,
                    ConcertsTable.COLUMN_ENTRY_ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst(); // move to the first row if the query results (whatever was queried) the single liked concert in this case
            Concert myConcert = cursorToConcert(cursor); // turn row into concert
            cursor.close();
            Log.d("dbCommands", "liked a concert");
            return myConcert; // return it for UI
        }

        return null; // return null for error handling
        // in parent activity: if insertLikedConcert(..) returns null, toast error message
        // Toast.makeText(MainActivity.this, "Error adding liked concert, try again later", Toast.LENGTH_SHORT).show();
    }

    // remove concert from db
    public void deleteLikedConcert(Concert concert) {
        long deleteId = concert.getDbId(); // the db id of the concert
        database.delete(ConcertsTable.TABLE_NAME, ConcertsTable.COLUMN_ENTRY_ID + " = " + deleteId, null);
        Log.d("dbCommands", "Concert deleted with id: " + concert.getDbId());
    }

    public ArrayList<Concert> getAllLikedConcerts() {
        ArrayList<Concert> allConcerts = new ArrayList<Concert>();

        Cursor cursor = database.query(ConcertsTable.TABLE_NAME, allConcertColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Concert concert = cursorToConcert(cursor);
            allConcerts.add(concert);
            cursor.moveToNext();
        }
        cursor.close();
        return allConcerts;
    }

    // insert song from db
    public Song insertLikedSong(Song likedSong) { // gets song from like button click

        // set key-value pairs for columns of song table
        ContentValues values = new ContentValues();
        values.put(SongsTable.COLUMN_SPOTIFY_ID, likedSong.getId());
        values.put(SongsTable.COLUMN_SONG_NAME, likedSong.getName());
        values.put(SongsTable.COLUMN_SONG_ARTISTS, likedSong.getArtistsString());
        values.put(SongsTable.COLUMN_SONG_PREVIEW_URL, likedSong.getPreviewUrl());
        values.put(SongsTable.COLUMN_ALBUM_ART_URL, likedSong.getAlbumArtUrl());

        // insert and return the song
        long insertId = database.insert(SongsTable.TABLE_NAME, null, values); // insert the values for the liked song, and return its entry row id
        Log.d("dbCommands", "inserted liked song at row: " + insertId);
        if (insertId != -1) { // if an error did not occur
            Cursor cursor = database.query(SongsTable.TABLE_NAME, allSongColumns,
                    SongsTable.COLUMN_ENTRY_ID + " = " + insertId, null, null, null, null); // get the whole liked song
            cursor.moveToFirst(); // move to the beginning of the query (in this case just the one row)
            Song mySong = cursorToSong(cursor); // turn the row into a song
            cursor.close(); // close the cursor
            return mySong; // return the song
        }
        return null; // return null for error handling
        // in parent activity: if insertLikedSong(..) returns null, toast error message
        // Toast.makeText(MainActivity.this, "Error adding liked song, try again later", Toast.LENGTH_SHORT).show();
    }

    // remove song from db
    public void deleteLikedSongs(Song song) {
        long deleteId = song.getDbID();
        database.delete(SongsTable.TABLE_NAME, SongsTable.COLUMN_ENTRY_ID + " = " + deleteId, null);
        Log.d("dbCommands", "Song deleted with: " + song.getDbID());
    }

    public ArrayList<Song> getAllLikedSongs() {
        ArrayList<Song> likedSongs = new ArrayList<Song>();
        Cursor cursor = database.query(SongsTable.TABLE_NAME, allSongColumns,
                null, null, null, null, null); // query all songs in db
        cursor.moveToFirst(); // move cursor to first song returned in query
        while (!cursor.isAfterLast()) { // while cursor is not at the end
            Song song = cursorToSong(cursor); // turn row to a song
            likedSongs.add(song); // add song to arraylist
            cursor.moveToNext(); // move to the next row
        }
        cursor.close();
        return likedSongs; // return the arraylist of songs
    }
    
    public static Song cursorToSong(Cursor cursor) {
        Song song = new Song();
        song.setDbID(cursor.getInt(0)); // db id
        song.setSpotifyID(cursor.getString(1)); // spotify id
        song.setName(cursor.getString(2));
        song.setArtistsString(cursor.getString(3));
        song.setPreviewUrl(cursor.getString(4));
        song.setAlbumArtUrl(cursor.getString(5));

        return song;
    }

    public static Concert cursorToConcert(Cursor cursor) {
        Concert concert = new Concert();
        concert.setDbId(cursor.getInt(0));
        concert.setEventName(cursor.getString(1));
        concert.setCity(cursor.getString(2));
        concert.setStateCode(cursor.getString(3)); // null for intl concerts
        concert.setCountryCode(cursor.getString(4));
        concert.setVenue(cursor.getString(5));
        concert.setEventTime(cursor.getString(6));
        concert.setEventDate(cursor.getString(7));
        concert.setArtistsString(cursor.getString(8));
        concert.setBackdropImage(cursor.getString(9));

        return concert;
    }

}
