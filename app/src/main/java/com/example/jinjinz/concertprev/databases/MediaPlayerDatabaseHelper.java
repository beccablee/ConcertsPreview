package com.example.jinjinz.concertprev.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;

import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by jinjinz on 7/25/16.
 */
public class MediaPlayerDatabaseHelper extends SQLiteOpenHelper {
    //information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mediaInfo.db";

    /**
     * constructor
     * @param context context of helper
     */
    public MediaPlayerDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Override onCreate: creates tables
     * @param db the database where we want the tables
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        MediaContract.CurrentConcertTable.onCreate(db);
        MediaContract.CurrentSongTable.onCreate(db);
        MediaContract.PlaylistTable.onCreate(db);
        intialize(db);
    }

    /**
     * not implemented
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * intialize database so with default information
     * @param db this database
     */
    private void intialize(SQLiteDatabase db) {
        //testing code
        //create dummy songs and concerts
        Concert dummy_c = new Concert();
        Song dummy_ss = new Song();
        dummy_c.setEventName("TESTING");
        dummy_ss.setAlbumArtUrl("https://i.scdn.co/image/6324fe377dcedf110025527873dafc9b7ee0bb34");
        ArrayList<String> artist = new ArrayList<>();
        artist.add("Elvis Presley");
        dummy_ss.setArtists(artist);
        dummy_ss.setName("Suspicious Minds");
        dummy_ss.setPreviewUrl("https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1");
        ArrayList<Parcelable> dummy_s = new ArrayList<>();
        dummy_s.add(Parcels.wrap(dummy_ss));
        //_id = db.insert(MediaContract.CurrentConcertTable.TABLE_NAME, MediaContract.CurrentConcertTable.COLUMN_CONCERT_NAME, values);

        ContentValues concertValues = new ContentValues();
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_NAME, dummy_c.getEventName());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_CITY, dummy_c.getCity());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_STATE, dummy_c.getStateCode());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_COUNTRY, dummy_c.getCountryCode());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_TIME, dummy_c.getEventTime());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_DATE, dummy_c.getEventDate());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_VENUE, dummy_c.getVenue());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_ARTISTS, dummy_c.getArtistsString());
        concertValues.put(MediaContract.CurrentConcertTable.COLUMN_CONCERT_IMAGE_URL, dummy_c.getBackdropImage());
        db.insert(MediaContract.CurrentConcertTable.TABLE_NAME, null, concertValues);

        for (int i = 0; i < 1; i++) {
            ContentValues songValues = new ContentValues();
            songValues.put(MediaContract.PlaylistTable.COLUMN_SPOTIFY_ID, dummy_ss.getSpotifyID());
            songValues.put(MediaContract.PlaylistTable.COLUMN_SONG_NAME, dummy_ss.getName());
            songValues.put(MediaContract.PlaylistTable.COLUMN_SONG_ARTIST, dummy_ss.getArtists().get(0));
            songValues.put(MediaContract.PlaylistTable.COLUMN_SONG_PREVIEW_URL, dummy_ss.getPreviewUrl());
            songValues.put(MediaContract.PlaylistTable.COLUMN_ALBUM_ART_URL, dummy_ss.getAlbumArtUrl());
            songValues.put(MediaContract.PlaylistTable.COLUMN_LIKED, 0);
            db.insert(MediaContract.PlaylistTable.TABLE_NAME, null, songValues);
        }

        ContentValues currentValues = new ContentValues();
        currentValues.put(MediaContract.CurrentSongTable.COLUMN_CURRENT_SONG_ID, 1);
        currentValues.put(MediaContract.CurrentSongTable.COLUMN_IS_PLAYING, 0);
        currentValues.put(MediaContract.CurrentSongTable.COLUNM_CURRENT_PROGRESS, 0);
        db.insert(MediaContract.CurrentSongTable.TABLE_NAME, null, currentValues);
    }
}
