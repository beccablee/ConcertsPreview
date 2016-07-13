package com.example.jinjinz.concertprev.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jinjinz on 7/7/16.
 */
public class Song {

    public String name;
    public String id;
    public ArrayList<String> artists; //names
    public String previewUrl;
    public String albumArtUrl;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
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

    public static ArrayList<Song> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Song> tracks = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                tracks.add(Song.fromJSON(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static Song fromJSON(JSONObject jsonObject){
        Song song = new Song();
        JSONArray artist_list;
        song.artists = new ArrayList<>();

        try {
            song.name = jsonObject.getString("name");
            song.id = jsonObject.getString("id");
            song.previewUrl = jsonObject.getString("preview_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            song.albumArtUrl = jsonObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
        } catch (JSONException e){
            e.printStackTrace();
        }
        try {
            artist_list = jsonObject.getJSONArray("artists");
            for (int i = 0; i < artist_list.length(); i++) {
                song.artists.add(artist_list.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return song;
    }


}
