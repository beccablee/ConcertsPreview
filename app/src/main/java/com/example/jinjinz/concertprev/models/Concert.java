package com.example.jinjinz.concertprev.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jinjinz on 7/7/16.
 */
public class Concert {


    //root url: https://app.ticketmaster.com/discovery/v2/
    // events url: https://app.ticketmaster.com/discovery/v2/events

    private String backdropImage;
    //private final String headliner;
    private ArrayList<String> artists;
    private String venue; // may be null (tba)
    private String eventName;
    private String eventTime; // may be null (tba)
    private String eventDate; // may be null (tba)
    private String city;
    private String stateCode; // may be null (international events)
    private String countryCode;

    public String getCountryCode() {
        return countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getCity() {
        return city;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getVenue() {
        return venue;
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public String getBackdropImage() {
        return backdropImage;
    }


    public Concert() {

    }

    private static ArrayList<Concert> concertsFromJsonArray(JSONArray jsonArray) {
        ArrayList<Concert> concert = new ArrayList<>();
        // iterate
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                concert.add(Concert.fromJsonObject(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return concert;
    }

    private static ArrayList<String> artistsFromJsonArray(JSONArray jsonArray) {
        ArrayList<String> array = new ArrayList<>();
        // iterate
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                array.add(jsonArray.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public static Concert fromJsonObject(JSONObject jsonObject){
        Concert concert = new Concert();
        // extract the values from the json, store them
        try {
            concert.backdropImage = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
            concert.eventName = jsonObject.getString("name");
            concert.artists = artistsFromJsonArray(jsonObject.getJSONArray("attractions"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            concert.countryCode = jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("country").getString("countryCode"); // if country != "US", use country code in place of stateCode
            concert.city = jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("city").getString("name");
            concert.venue = jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            concert.stateCode = jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("state").getString("stateCode");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            concert.eventDate = jsonObject.getJSONObject("dates").getJSONObject("start").getString("localDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            concert.eventTime = jsonObject.getJSONObject("dates").getJSONObject("start").getString("localTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return concert;
    }

}
