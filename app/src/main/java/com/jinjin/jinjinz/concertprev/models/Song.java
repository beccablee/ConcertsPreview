package com.jinjin.jinjinz.concertprev.models;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;

@Parcel
public class Song {

    private long dbID = -1L;
    private String name;
    private String spotifyID;
    private ArrayList<String> artists; //names
    private String previewUrl;
    private String albumArtUrl;
    private int songsPerArtist;
    private boolean liked;
    private String artistsString; // formatted for details view and db

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

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
    public void setSongsPerArtist(int songsPerArtist) {
        this.songsPerArtist = songsPerArtist;
    }
    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }
    public void setDbID(long dbID) {
        this.dbID = dbID;
    }
    public void setArtistsString(String artistsString) {
        this.artistsString = artistsString;
    }

    public boolean isLiked() {
        return liked;
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

    public int getSongsPerArtist() {
        return songsPerArtist;
    }
    public String getSpotifyID() {
        return spotifyID;
    }

    /** Adds songs to the tracks ArrayList from JSON */
    public static ArrayList<Parcelable> fromJSONArray(JSONArray jsonArray, int songsPerArtist) {
        ArrayList<Parcelable> tracks = new ArrayList<>();
        try {
            for (int i = 0; i < songsPerArtist; i++) {
                tracks.add(Parcels.wrap(Song.fromJSON(jsonArray.getJSONObject(i))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("song calls", "couldn't get at least one song");
        }
        return tracks;
    }

    /** Gets song details from JSON */
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
        song.setLiked(false);
        return song;
    }

    public ArrayList<String> artistListToArray(String artistList) {
        ArrayList<String> artists = new ArrayList<>();
        String[] artistArray = TextUtils.split(artistList, ", ");
        for(int i = 0; i < artistArray.length; i++) {
            artists.add(artistArray[i]);
        }
        return artists;
    }
}