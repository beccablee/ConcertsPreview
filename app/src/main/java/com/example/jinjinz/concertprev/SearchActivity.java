package com.example.jinjinz.concertprev;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinjinz.concertprev.fragments.ConcertsFragment;
import com.example.jinjinz.concertprev.models.Concert;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


//@RuntimePermissions
public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ConcertsFragment.ConcertsFragmentListener {
    /*implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener*/
    ConcertsFragment cFragment;
    protected Context context;
    TextView tvLocationTest;
    TextView responseText;
    Button player;
    Button concert;

    // client call variables
    Concert event = new Concert();


    // Location variables
    protected String latitude, longitude;
    protected String latlong;
    protected String permissions; // array or nah
    private int LOCATION_PERMISSIONS = 10;
    private String[] locationPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    // Google Location API: AIzaSyBHPJnNxwfY8H_Uo6eGsbKw7Xp8Yag3xUM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

/*        // Create the location client to start receiving updates
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();*/

/*
        // Find the toolbar view inside the activity layout
        Toolbar tbSearch = (Toolbar) findViewById(R.id.tbSearch);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(tbSearch);
*/

        /*responseText = (TextView) findViewById(R.id.response);

        // make client call
        // url: includes api key and music classification
        String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&classificationName=Music";
        // the parameters
        RequestParams params = new RequestParams();
        params.put("latlong", "34.0928090,-118.3286610");

        // the client
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(eventsURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { // here we are given the entire outer object
                //ArrayList<Concert> arrayList = new ArrayList<Concert>();
                //arrayList = Concert.concertsFromJsonArray(response);
                Log.d("ticketresponse", "success");

                event = Concert.forTesting(response);

                responseText.setText(event.getEventName());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("ticketresponse", "failure");
            }
        });
*/

        player = (Button) findViewById(R.id.playerBtn);
        concert = (Button) findViewById(R.id.concertBtn);
        tvLocationTest = (TextView) findViewById(R.id.tvLocationTest);


        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, PlayerActivity.class);
                startActivity(i);
            }
        });

        concert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, ConcertActivity.class);
                startActivity(i);
            }
        });

        if (savedInstanceState == null) {
            cFragment = cFragment.newInstance("param1", "param2");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.concertContainer, cFragment);
            ft.commit();
        }

    }

/*    // Menu icons are inflated just as they were with actionbar
    @Override
    //        @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }*/


    // Google locationservices API
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    AsyncHttpClient client;

    @Override
    public void populateConcertsNearYou(final ConcertsFragment fragment) {
        // url: includes api key and music classification
        String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&classificationName=Music";
        // the parameter(s)
        RequestParams params = new RequestParams();
        //params.put("latlong", dummyLatlong);
        params.put("latlong", "29.563034,-95.262090"); // must be N, E (in the us the last should def be -) that num + is W
        params.put("radius", "50");
        params.put("size", "200");

        // call client
        client = new AsyncHttpClient();
        client.get(eventsURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) { // on success I will get back the large json obj: { _embedded: { events: [ {0, 1, 2, ..} ] } }
                // DESERIALIZE JSON
                // CREATE MODELS AND ADD TO ADAPTER
                // LOAD MODEL INTO LIST VIEW
                JSONArray eventsArray = null;
                try {
                    eventsArray = jsonObject.getJSONObject("_embedded").getJSONArray("events"); // size = 0 rn
                    Log.d("populateFragment", String.valueOf(eventsArray.length()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fragment.addConcerts(Concert.concertsFromJsonArray(eventsArray)); //
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("populateFragment", "failure");
            }
        });
    }

    @Override
    public void onConcertTap(Concert concert) {
        Intent intent = new Intent(this, ConcertActivity.class);
        // call listener(concert)
        intent.putExtra("concert", Parcels.wrap(concert));
        Toast.makeText(this, concert.getEventName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }



    /*  Log.d("Latitude","location");
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        latlong = latitude + "," + longitude;
        tvLocationTest.setText(latlong);*/
}

