package com.example.jinjinz.concertprev.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Parcel
public class Concert {


    //root url: https://app.ticketmaster.com/discovery/v2/
    // events url: https://app.ticketmaster.com/discovery/v2/events

    public long dbId;
    public String backdropImage;
    public String headliner;
    public String venue; // may be null (tba)
    public String artistsString;
    public String eventName;
    public String eventTime; // may be null (tba)
    public String eventDate; // may be null (tba)
    public String city;
    public String stateCode;
    public String countryCode;
    public ArrayList<String> artists;


    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
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
    public void setHeadliner(String headliner) {
        this.headliner = headliner;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public void setArtistsString(String artistsString) {
        this.artistsString = artistsString;
    }
    public String getArtistsString() {
        return artistsString;
    }

    public long getDbId() {
        return dbId;
    }
    public String getCountryCode() {
        return countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }
    public String getHeadliner() {
        return headliner;
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

    /** Builds and returns an ArrayList of Concerts from the supplied JSONArray from the Ticketmaster API */
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

    /** Builds and returns an ArrayList of artist names with the supplied artist(attractions) JSONArray from the Ticketmaster API */
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

    // get a wide image that's large enough to be pretty
    /** Finds and returns a backdrop image for a concert with a ratio made to fit cleanly into the concert ImageView and a size large enough to appear crisp onscreen */
    private static String ratioImg(JSONObject event) { //takes an event obj --> { events:[ {0}, {1}, ...]} needs: 0:{images:[ {0}, {1}, ... ]}
        // start with event obj

        JSONArray images = null;
        try {
            images = event.getJSONArray("images"); // get the json array of images
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (images != null) { // if the images jsonarray exists
            for (int i = 0; i < images.length(); i++) { // step through array
                try {                                                               // get ratio of the image obj
                    if(images.getJSONObject(i).getString("ratio").equals("16_9")) { // if the ratio is 16_9
                        if(images.getJSONObject(i).getInt("width") >= 400) {
                            return images.getJSONObject(i).getString("url"); // return the url of that img obj
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try { // if none are 16_9, go get the first one
                return event.getJSONArray("images").getJSONObject(0).getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // if no image array exists, use stock img
        return "http://www.wallpapersxl.com/wallpapers/1920x1080/concerts/1018922/concerts-skate-music-concert-noise-jpg-with-resolution-1018922.jpg";

    }

    /** Formats given date from the Ticketmaster API into a clean, easily readable format */
    public static String formatDate(String originalDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy");
        String date = format.format(newDate);
        return date;
    }

    /** Builds and returns a concert from the event JSONObject retrieved from the Ticketmaster API */
    public static Concert fromJsonObject(JSONObject event){ // will give the concert each obj from the "events" json array (each index) // then will form each obj from the fromJsonArray method
        Concert concert = new Concert();
        // extract the values from the json, store them
        try {
            //concert.backdropImage = ratioImg(event.getJSONArray("images"));
            concert.backdropImage = ratioImg(event);
            concert.eventName = event.getString("name");
            // because I love Chance
            if(concert.getEventName().contains("Chance The Rapper")) {
                concert.backdropImage = "https://wallpapers.wallhaven.cc/wallpapers/full/wallhaven-372461.png";
            }
            concert.artists = artistsFromJsonArray(event.getJSONObject("_embedded").getJSONArray("attractions"));
            concert.artistsString = android.text.TextUtils.join(", ", concert.artists);
            if (concert.artists.size() != 0) {
                concert.headliner = concert.artists.get(0);
            }
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
            concert.eventDate = formatDate(event.getJSONObject("dates").getJSONObject("start").getString("localDate"));
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
