package com.example.jinjinz.concertprev;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jinjinz.concertprev.fragments.ConcertsFragment;
import com.example.jinjinz.concertprev.models.Concert;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


//@RuntimePermissions
public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ConcertsFragment.ConcertsFragmentListener {

    ConcertsFragment mFragment;
    protected Context context;
    Button player;
    Button concert;

    // client call variables
    Concert event = new Concert();
    boolean readyToPopulate = false;
    boolean apiConnected = false;

    // Location variables
    protected String latlong;
    protected String queryText;
    protected String permissions; // array or nah
    private static final int LOCATION_PERMISSIONS = 10;
    protected GoogleApiClient mGoogleApiClient;
    protected Location lastLocation;
    private String[] locationPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(SearchActivity.this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        player = (Button) findViewById(R.id.playerBtn);
        concert = (Button) findViewById(R.id.concertBtn);


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
            mFragment = mFragment.newInstance("param1", "param2");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.concertContainer, mFragment);
            ft.commit();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    AsyncHttpClient client;

    // Fetch
    public void fetchConcerts() {
        if (readyToPopulate && apiConnected) {
            // url: includes api key and music classification
            String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&classificationName=Music";
            // the parameter(s)
            RequestParams params = new RequestParams();
            if (queryText != null) {
                params.put("keyword", queryText);
            }
            if (lastLocation != null) {
                latlong = lastLocation.getLatitude() + "," + lastLocation.getLongitude(); //(MessageFormat.format("{0},{1}", lastLocation.getLatitude(), lastLocation.getLongitude()));
                // getLastLocation()
            } else {
                latlong = null;
            }

            params.put("latlong", latlong); // must be N, E (in the us the last should def be -) that num + is W
            params.put("radius", "50");
            params.put("size", "100");

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
                        eventsArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                        Log.d("populateFragment", String.valueOf(eventsArray.length()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("houston", "nope");
                    }
                    mFragment.addConcerts(Concert.concertsFromJsonArray(eventsArray)); //
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("populateFragment", "failure");
                    Toast.makeText(SearchActivity.this, "Could not display concerts. Pleas wait and try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void populateConcerts(final ConcertsFragment fragment, String query) {
        // set ready flag
        readyToPopulate = true;
        // set queryText
        queryText = query;
        // fetch
        fetchConcerts();

    }

    @Override
    public void onConcertTap(Concert concert) {
        Intent intent = new Intent(this, ConcertActivity.class);
        // call listener(concert)
        intent.putExtra("concert", Parcels.wrap(concert));
        Toast.makeText(this, concert.getEventName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    // Google LocationServices Api

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(SearchActivity.this, locationPermissions, LOCATION_PERMISSIONS);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d("lastlocation", lastLocation.toString());
            apiConnected = true;
            fetchConcerts();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case LOCATION_PERMISSIONS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("requestlocation", "Permissions Granted");

                } else {
                    Log.d("requestlocation", "Permissions Denied");
                }

            }
        }
    }

}

