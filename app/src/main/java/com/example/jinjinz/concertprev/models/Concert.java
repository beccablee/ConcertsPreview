package com.example.jinjinz.concertprev.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by jinjinz on 7/7/16.
 */
@Parcel
public class Concert {


    //root url: https://app.ticketmaster.com/discovery/v2/
    // events url: https://app.ticketmaster.com/discovery/v2/events

    public String backdropImage;
    //private final String headliner;
    public ArrayList<String> artists;
    public String venue; // may be null (tba)

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setBackdropImage(String backdropImage) {
        this.backdropImage = backdropImage;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String eventName;
    public String eventTime; // may be null (tba)
    public String eventDate; // may be null (tba)
    public String city;
    public String stateCode; // may be null (international events)
    public String countryCode;

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

    public static Concert forTesting(JSONObject jsonObject) {
        Concert concert = new Concert();
        try {
            concert.eventName = jsonObject.getJSONObject("_embedded").getJSONArray("events").getJSONObject(0).getString("name");
            Log.d("ticketrespone", "testing");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return concert;
    }

    public static ArrayList<Concert> concertsFromJsonArray(JSONArray jsonArray) { // looking for the "events" array --> { _embedded: { events: [ {0, 1, 2, ..} ] } } within the larger "_embedded" array and the largest object that you get from the client response
        ArrayList<Concert> concert = new ArrayList<>();
        // iterate
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eventObj = null;
            try {
                eventObj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject artistObj = null;
            try {
                if (eventObj != null) {
                    artistObj = eventObj.getJSONObject("_embedded");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!artistObj.has("attractions")) { // if the event object does not have an artist array, continue
            } else { // if it contains the attractions array (it is a concert and has at least one artist)
                concert.add(Concert.fromJsonObject(eventObj)); // else, add the object
                Log.d("populateFragment", "concertarray");
            }
        }

        return concert;
    }



    private static ArrayList<String> artistsFromJsonArray(JSONArray attractions) { // the attractions array: { _embedded:{ events:[ { ..., _embedded:{ venues:[...], attractions:[ list of at least one artist ] } } ] } }
        ArrayList<String> array = new ArrayList<>();
        // iterate
        for (int i = 0; i < attractions.length(); i++) {
            try {
                array.add(attractions.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

/*    private static String ratioImg(JSONArray images) {
        String imgURL = "http://www.schaliken.be/sites/default/files/files/afbeeldingen/Seizoen_15-16/BelegenHelden.jpg";
        try {
            imgURL = images.getJSONObject(0).getString("url");
            Log.d("ratImg", "default");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // iterate through image array to find 16_9 ratio image
        for(int i = 0; i < images.length(); i++) {
            try {
                if("16_9" == images.getJSONObject(i).getString("url")) {
                    imgURL = images.getJSONObject(i).getString("url");
                    Log.d("ratImg", "preferred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return imgURL;
    }*/



    public static Concert fromJsonObject(JSONObject event){ // will give the concert each obj from the "events" json array (each index) // then will form each obj from the fromJsonArray method
        Concert concert = new Concert();
        // extract the values from the json, store them
        try {
            //concert.backdropImage = ratioImg(event.getJSONArray("images"));
            concert.backdropImage = event.getJSONArray("images").getJSONObject(0).getString("url");
            concert.eventName = event.getString("name");
            concert.artists = artistsFromJsonArray(event.getJSONObject("_embedded").getJSONArray("attractions"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            concert.countryCode = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0)
                    .getJSONObject("country").getString("countryCode"); // if country != "US", use country code in place of stateCode
            concert.city = event.getJSONObject("_embedded").getJSONArray("venues")
                    .getJSONObject(0).getJSONObject("city").getString("name");
            concert.venue = event.getJSONObject("_embedded").getJSONArray("venues")
                    .getJSONObject(0).getString("name");
            concert.stateCode = event.getJSONObject("_embedded").getJSONArray("venues")
                    .getJSONObject(0).optJSONObject("state").optString("stateCode"); // changed to optJSONObject and optString to avoid catch issues

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            concert.eventDate = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            concert.eventTime = event.getJSONObject("dates").getJSONObject("start").getString("localTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return concert;
    }

}
