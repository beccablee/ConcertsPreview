package com.jinjin.jinjinz.concertprev.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.jinjin.jinjinz.concertprev.models.Concert;
import com.jinjin.jinjinz.concertprev.models.Song;

import java.util.ArrayList;

public class UserDataSource { // Our DAO (data access object) that is responsible for handling db connection and accessing and modifying the data in the db
    // it also converts the data into objects that our app can be readily used for our ui

    private SQLiteDatabase database;
    private UserDatabaseHelper dbHelper;
    private Context c;

    public UserDataSource(Context context) {
        c = context;
        dbHelper = new UserDatabaseHelper(context);
    }

    // all the columns in the song table
    String[] allSongColumns =
            {SongsTable.COLUMN_ENTRY_ID,
            SongsTable.COLUMN_SPOTIFY_ID,
            SongsTable.COLUMN_SONG_NAME,
            SongsTable.COLUMN_SONG_ARTISTS,
            SongsTable.COLUMN_SONG_PREVIEW_URL,
            SongsTable.COLUMN_ALBUM_ART_URL};

    // all the columns in the concert table
    String [] allConcertColumns =
            {ConcertsTable.COLUMN_ENTRY_ID,
            ConcertsTable.COLUMN_CONCERT_NAME,
            ConcertsTable.COLUMN_CONCERT_CITY,
            ConcertsTable.COLUMN_CONCERT_STATE,
            ConcertsTable.COLUMN_CONCERT_COUNTRY,
            ConcertsTable.COLUMN_CONCERT_VENUE,
            ConcertsTable.COLUMN_CONCERT_TIME,
            ConcertsTable.COLUMN_CONCERT_DATE,
            ConcertsTable.COLUMN_CONCERT_ARTISTS,
            ConcertsTable.COLUMN_CONCERT_IMAGE_URL,
            ConcertsTable.COLUMN_CONCERT_TICKET_LINK};

    /**
     *  Creates and/or opens a database that will be used for reading and writing
     * */
    public void openDB() {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Inserts the liked concert into the database after checking for the possibility of duplication
     * @param concert the concert to be liked
     * @return the liked concert*/
    public Concert likeConcert(Concert concert) { //gets concert from like button click


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
        values.put(ConcertsTable.COLUMN_CONCERT_TICKET_LINK, concert.getEventUrl());

        // check if it exists already
        if(isConcertAlreadyInDb(concert)) { // if the concert is already in the db
            deleteLikedConcert(concert);
            Toast.makeText(c, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            return concert;
        } else {
            long insertId = database.insert(ConcertsTable.TABLE_NAME, ConcertsTable.COLUMN_CONCERT_STATE, values); // inserts values in every column for the new row (liked concert)
                                                                                                                  // and returns the row id of the inserted concert ( or -1 if an error occurred)
            if (insertId != -1L) { // setdbid
                Cursor cursor = database.query(ConcertsTable.TABLE_NAME, allConcertColumns,
                        ConcertsTable.COLUMN_ENTRY_ID + " = " + insertId, null, null, null, null);
                cursor.moveToFirst();
                concert = cursorToConcert(cursor); // turn row into concert
                cursor.close();
                Toast.makeText(c, "Added to Favorites", Toast.LENGTH_SHORT).show();
                return concert; // return it for UI
            } else {
                Toast.makeText(c, "Error adding concert. Please try again later", Toast.LENGTH_SHORT).show();
            }
        }
        return null; // return null for error handling
    }

    /**
     * Removes liked concert from database
     * @param concert the concert to be deleted
     * */
    public void deleteLikedConcert(Concert concert) {
        database.delete(ConcertsTable.TABLE_NAME, ConcertsTable.COLUMN_ENTRY_ID + " = " + concert.getDbId(), null);
        concert.setDbId(-1L);
    }

    /**
     *  Builds and returns an ArrayList of Concerts from the database with newest additions at the beginning of the array
     *  @return returns an array of all the liked concerts */
    public ArrayList<Concert> getAllLikedConcerts() {
        ArrayList<Concert> allConcerts = new ArrayList<>();

        Cursor cursor = database.query(ConcertsTable.TABLE_NAME, allConcertColumns,
                null, null, null, null, null);

        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            Concert concert = cursorToConcert(cursor);
            allConcerts.add(concert);
            cursor.moveToPrevious();
        }
        cursor.close();
        return allConcerts;
    }

    /**
     * Creates and returns a Concert object from the cursor
     * @param cursor the cursor of the database query
     * @return returns the concert */
    public static Concert cursorToConcert(Cursor cursor) {
        Concert concert = new Concert();
        concert.setDbId(cursor.getLong(0));
        concert.setEventName(cursor.getString(1));
        concert.setCity(cursor.getString(2));
        concert.setStateCode(cursor.getString(3)); // null for international concerts
        concert.setCountryCode(cursor.getString(4));
        concert.setVenue(cursor.getString(5));
        concert.setEventTime(cursor.getString(6));
        concert.setEventDate(cursor.getString(7));
        concert.setArtistsString(cursor.getString(8));
        concert.setBackdropImage(cursor.getString(9));
        concert.setEventUrl(cursor.getString(10));
        concert.setArtists(concert.artistListToArray(concert.getArtistsString()));

        return concert;
    }

    /**
     * Checks if a certain concert is already in the database
     * @param concert the concert to check for
     * @return true if the concert is already in the database, false otherwise */
    public boolean isConcertAlreadyInDb(Concert concert) {
        Cursor cursor = database.query(ConcertsTable.TABLE_NAME, allConcertColumns,
                null, null, null, null, null); // query whole table
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            if(cursor.getString(1).equals(concert.getEventName()) && cursor.getString(7).equals(concert.getEventDate())) {
                concert.setDbId(cursor.getLong(0));
                cursor.close();
                return true;
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();

        return false;
    }

    public Concert getConcertFromDB(Concert concert) {
        Cursor cursor = database.query(ConcertsTable.TABLE_NAME, allConcertColumns,
                null, null, null, null, null); // query whole table
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            if(cursor.getString(1).equals(concert.getEventName()) && cursor.getString(7).equals(concert.getEventDate())) {
                Concert myConcert = cursorToConcert(cursor);
                cursor.close();
                return myConcert;
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return null;
    }

    public void deleteAllConcerts() {
        database.delete(ConcertsTable.TABLE_NAME, null, null);
    }

    /**
    * Inserts the liked song into the database after checking for the possibility of duplication
    * @param song the song to be liked
    * @return the liked song
    * */
    public Boolean likeSong(Song song) { // gets song from like button click

        // set key-value pairs for columns of song table
        ContentValues values = new ContentValues();
        values.put(SongsTable.COLUMN_SPOTIFY_ID, song.getSpotifyID());
        values.put(SongsTable.COLUMN_SONG_NAME, song.getName());
        values.put(SongsTable.COLUMN_SONG_ARTISTS, song.getArtistsString());
        values.put(SongsTable.COLUMN_SONG_PREVIEW_URL, song.getPreviewUrl());
        values.put(SongsTable.COLUMN_ALBUM_ART_URL, song.getAlbumArtUrl());

        if (isSongAlreadyInDb(song)) {
            deleteLikedSong(song);
            Toast.makeText(c, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            return false;
        } else { // insert and return the song
            long insertId = database.insert(SongsTable.TABLE_NAME, null, values); // insert the values for the liked song, and return its entry row id
            if (insertId != -1L) {
                Cursor cursor = database.query(SongsTable.TABLE_NAME, allSongColumns,
                        SongsTable.COLUMN_ENTRY_ID + " = " + insertId, null, null, null, null);
                song.setDbID(insertId);
                cursor.moveToFirst();
                song = cursorToSong(cursor);
                cursor.close();
                Toast.makeText(c, "Added to Favorites", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(c, "Error adding " + song.getName() + ". " + "Please try again later", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    /**
     * Removes liked song from database
     * @param song the song to be deleted
     * */
    public void deleteLikedSong(Song song) {
        String mSelectionClause =  SongsTable.COLUMN_SPOTIFY_ID  + " LIKE ?";
        String[] mSelectionArgs = {song.getSpotifyID()};

        database.delete(SongsTable.TABLE_NAME, mSelectionClause, mSelectionArgs);
       song.setDbID(-1L);
    }

    /**
     * Builds and returns an ArrayList of Songs from the database with newest additions at the beginning of the array
     * @return returns an array of all the liked songs
     * */
    public ArrayList<Song> getAllLikedSongs() {
        ArrayList<Song> likedSongs = new ArrayList<Song>();
        Cursor cursor = database.query(SongsTable.TABLE_NAME, allSongColumns,
                null, null, null, null, null); // query all songs in db
        cursor.moveToLast(); // move to end of query
        while (!cursor.isBeforeFirst()) {
            Song song = cursorToSong(cursor); // turn row to a song
            likedSongs.add(song);
            cursor.moveToPrevious(); // move to previous row
        }
        cursor.close();
        return likedSongs;
    }

    /**
     * Creates and returns a Song object from the cursor
     * @param cursor the cursor of the database query
     * @return returns the song
     * */
    public static Song cursorToSong(Cursor cursor) {
        Song song = new Song();
        song.setDbID(cursor.getLong(0));
        song.setSpotifyID(cursor.getString(1));
        song.setName(cursor.getString(2));
        song.setArtistsString(cursor.getString(3));
        song.setArtists(song.artistListToArray(song.getArtistsString()));
        song.setPreviewUrl(cursor.getString(4));
        song.setAlbumArtUrl(cursor.getString(5));
        return song;
    }



    public boolean isSongAlreadyInDb(Song song){
        Cursor cursor = database.query(SongsTable.TABLE_NAME, allSongColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(1).equals(song.getSpotifyID())) {
                cursor.close();
                return true;
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return false;
    }

    /**
     * Retrieves the input song with the database id from the SQLite database
     * @param song the song to search for
     * @return the song in the form it was input into the database
     */
    public Song getSongFromDB(Song song) {
        Cursor cursor = database.query(SongsTable.TABLE_NAME, allSongColumns,
                null, null, null, null, null); // query whole table
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            if(cursor.getString(1).equals(song.getSpotifyID())) {
                Song mySong = cursorToSong(cursor);
                cursor.close();
                return mySong;
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return null;
    }

    public void deleteAllSongs() {
        database.delete(SongsTable.TABLE_NAME, null, null);
    }

}
