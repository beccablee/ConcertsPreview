package com.example.jinjinz.concertprev;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinjinz.concertprev.fragments.ConcertsFragment;
import com.example.jinjinz.concertprev.models.Concert;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.MessageFormat;

import cz.msebera.android.httpclient.Header;


//@RuntimePermissions
public class SearchActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ConcertsFragment.ConcertsFragmentListener {

    ConcertsFragment cFragment;
    protected Context context;
    TextView tvLocationTest;
    TextView responseText;
    Button player;
    Button concert;

    // client call variables
    Concert event = new Concert();

    /////////////////////////////////////////////
    private Location mLastLocation;
    public LocationManager mLocationManager;

    int updates;
    ////////////////////////////////////////////////
    // Location variables
    protected String latitude, longitude;
    protected String latlong;
    protected String permissions; // array or nah
    private static final int LOCATION_PERMISSIONS = 10;
    private String[] locationPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    private LocationManager locationManager;
    private String provider;
    // Google Location API: AIzaSyBHPJnNxwfY8H_Uo6eGsbKw7Xp8Yag3xUM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        updates = 0;
        handlePermissionsAndGetLocation();

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

    ////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Accepted
                    getLocation();
                } else {
                    // Denied
                    Toast.makeText(SearchActivity.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handlePermissionsAndGetLocation() {
        Log.d("tryingagain", "handlePermissionsAndGetLocation");
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS);
            return;
        }
        getLocation();//if already has permission
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void getLocation() {
        Log.d("tryingagain", "GetLocation");
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.v("WEAVER_", "Has permission");
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        } else {
            Log.v("WEAVER_", "Does not have permission");
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v("WEAVER_", "Location Change");
            tvLocationTest.setText(String.valueOf(location.getLatitude()));
            latlong = (MessageFormat.format("{0},{1}", location.getLatitude(), location.getLongitude()));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    ///////////////////////////////////////////

    AsyncHttpClient client;

    @Override
    public void populateConcerts(final ConcertsFragment fragment, String query) {
        // url: includes api key and music classification
        String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&classificationName=Music";
        // the parameter(s)
        RequestParams params = new RequestParams();
        //params.put("latlong", dummyLatlong);
        if (query != null) {
            params.put("keyword", query); // get from search toolbar in ConcertsFragment
        }
        params.put("latlong", "29.563034,-95.262090"); // must be N, E (in the us the last should def be -) that num + is W
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater, final ConcertsFragment cFragment) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = new SearchView(this);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                populateConcerts(cFragment, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        tvLocationTest.setText("Latitude: " + location.getLatitude());
    }



    /*  Log.d("Latitude","location");
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        latlong = latitude + "," + longitude;
        tvLocationTest.setText(latlong);*/
}

