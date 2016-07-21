package com.example.jinjinz.concertprev.models;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by jinjinz on 7/7/16.
 */
@Parcel
public class Song {

    public long dbID;
    public String name;
    public String spotifyID;
    public ArrayList<String> artists; //names
    public String artistsString; // formatted for details view and db
    public String previewUrl;
    public String albumArtUrl;
    public int songsPerArtist;

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSpotifyID(String id) {
        this.spotifyID = id;
    }
    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
    public void setAlbumArtUrl(String albumArtUrl) {
        this.albumArtUrl = albumArtUrl;
    }
    public void setArtistsString(String artistsString) {
        this.artistsString = artistsString;
    }

    public String getArtistsString() {
        return artistsString;
    }
    public long getDbID() {
        return dbID;
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return spotifyID;
    }
    public ArrayList<String> getArtists() {
        return artists;
    }
    public String getPreviewUrl() {
        return previewUrl;
    }
    public String getAlbumArtUrl() {
        return albumArtUrl;
    }

    /* Adds songs to the tracks ArrayList from JSON */
    public static ArrayList<Parcelable> fromJSONArray(JSONArray jsonArray, int songsPerArtist) {
        ArrayList<Parcelable> tracks = new ArrayList<>();
        try {
            for (int i = 0; i < songsPerArtist; i++) {
                tracks.add(Parcels.wrap(Song.fromJSON(jsonArray.getJSONObject(i))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    /* Gets song details from JSON */
    public static Song fromJSON(JSONObject jsonObject){
        Song song = new Song();
        JSONArray artist_list;
        song.artists = new ArrayList<>();
        try {
            song.name = jsonObject.getString("name");
            song.spotifyID = jsonObject.getString("id");
            song.previewUrl = jsonObject.getString("preview_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            song.albumArtUrl = jsonObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
        } catch (JSONException e){
            e.printStackTrace();
        }
        // Gets each artist's name from JSONArray
        try {
            artist_list = jsonObject.getJSONArray("artists");
            for (int i = 0; i < artist_list.length(); i++) {
                song.artists.add(artist_list.getJSONObject(i).getString("name"));
            }
            song.artistsString = android.text.TextUtils.join(" & ", song.artists);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return song;
    }
}