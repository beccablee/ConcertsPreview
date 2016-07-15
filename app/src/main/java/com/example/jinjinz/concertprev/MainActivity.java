package com.example.jinjinz.concertprev;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import com.example.jinjinz.concertprev.fragments.PlayerBarFragment;
import com.example.jinjinz.concertprev.fragments.PlayerScreenFragment;
import com.example.jinjinz.concertprev.fragments.SongsFragment;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;
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

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ConcertsFragment.ConcertsFragmentListener, PlayerScreenFragment.PlayerScreenFragmentListener, PlayerBarFragment.PlayerBarFragmentListener {
    Concert concert;
    ArrayList<Song> songs;
    MediaPlayer mediaPlayer;
    private int songNum;
    private Button testBtn;

    ConcertsFragment mConcertsFragment; // concerts fragment
    SongsFragment mSongsFragment; // songs fragment


    // Location variables
    protected String latlong;
    protected String queryText;
    protected String permissions; // array or nah
    private static final int LOCATION_PERMISSIONS = 10;
    protected GoogleApiClient mGoogleApiClient;
    protected Location lastLocation;
    private String[] locationPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

    // Concerts list client call variables
    boolean readyToPopulate = false;
    boolean apiConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        // Concerts fragment should always show first
        if (savedInstanceState == null) {
            mConcertsFragment = mConcertsFragment.newInstance(); // add params if needed
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, mConcertsFragment);
            ft.commit();
        }

/*        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateConcerts(mConcertsFragment, queryText);
            }
        });*/
        // logic to change fragments will be in concert and song click listeners


    // For dynamically showing and hiding the player frame at the bottom
    //FrameLayout playerBar = (FrameLayout)findViewById(R.id.playerFragment);
    //playerBar. // you can use INVISIBLE also instead of GONE
        testBtn = (Button) findViewById(R.id.button);

       testBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               testPlayer();
           }
       });
        //Test if playbar works
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
        ArrayList<Song> dummy_s = new ArrayList<>();
        dummy_s.add(dummy_ss);
        onNewConcert(dummy_c,dummy_s);

    }

    private void testPlayer() {
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
        ArrayList<Song> dummy_s = new ArrayList<>();
        dummy_s.add(dummy_ss);

        PlayerScreenFragment playerFragment = PlayerScreenFragment.newInstance(dummy_s.get(0));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, playerFragment, "player");
        ft.addToBackStack("player");
        PlayerBarFragment playerBar = (PlayerBarFragment) getSupportFragmentManager().findFragmentByTag("bar");
        if (playerBar != null) {
            ft.hide(playerBar);
        }
        ft.commit();
        onNewConcert(dummy_c,dummy_s);
    }

    private void onNewConcert(Concert c, ArrayList<Song> s) {
        //only play if new concert or is different from current playing
        if (concert == null) {
            songNum = 0;
            concert = c;
            songs = s;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            updateProgressBar();

            PlayerScreenFragment fragment = (PlayerScreenFragment)getSupportFragmentManager().findFragmentByTag("player");
            if (fragment == null) {
                PlayerBarFragment playerBar = PlayerBarFragment.newInstance(songs.get(songNum));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.playerFragment, playerBar, "bar");
                ft.commit();
            }
            //on prepared listener --> what happens when it is ready to play
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    PlayerScreenFragment fragment = (PlayerScreenFragment)getSupportFragmentManager().findFragmentByTag("player");
                    if (fragment != null) {
                        fragment.updateInterface(songs.get(songNum));
                        fragment.updatePlay(true);
                    }
                    PlayerBarFragment playerBar = (PlayerBarFragment) getSupportFragmentManager().findFragmentByTag("bar");
                    if (playerBar != null) {
                        playerBar.updatePlay(true);
                        playerBar.updateInterface(songs.get(songNum));
                    }
                    mediaPlayer.start();
                }
            });

            //switch between songs
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    songNum++;
                    if(songs != null && songNum == songs.size()) {
                        songNum = 0;
                    }
                    try {
                        mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Toast.makeText(MainActivity.this, "No music playing", Toast.LENGTH_SHORT);
                    return false;
                }
            });
            try {
                mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         else if( !concert.getEventName().equals(c.getEventName())) {
            //initialize
            songNum = 0;
            concert = c;
            songs = s;
            mediaPlayer.stop();
            mediaPlayer.reset();
            //start with first song
            try {
                mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }


    //Player + MediaPlayer methods
    ////////////////////////////////////////////////////
    @Override
    public String getConcertName() {
        return concert.getEventName();
    }
    //go to concert fragment
    // on concert name click?
    @Override
    public void onConcertClick() {
    }

    @Override
    public void playPauseSong() {
        PlayerScreenFragment fragment = (PlayerScreenFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            fragment.updatePlay(false);
        }
        else {
            mediaPlayer.start();
            fragment.updatePlay(true);
        }
    }

    @Override
    public void skipNext() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            if(songNum == songs.size()) {
                songNum = 0;
            }
            mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void skipPrev() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        if (songNum >= 1) {
            songNum = songNum - 1;
        }
        else if (songNum == 0) {
            songNum = songs.size() - 1;
        }
        try {
            mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClosePlayer() {
        PlayerBarFragment playerBar = (PlayerBarFragment) getSupportFragmentManager().findFragmentByTag("bar");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (playerBar == null) {
            playerBar = PlayerBarFragment.newInstance(songs.get(songNum));
            ft.replace(R.id.playerFragment, playerBar);
            ft.commit();
        }
        else {
            ft.show(playerBar);
            playerBar.updateInterface(songs.get(songNum));
        }
        if (mediaPlayer.isPlaying()) {
            playerBar.updatePlay(true);
        }
        else {
            playerBar.updatePlay(false);
        }
    }
    public void updateProgressBar() {
        //attempt at progressbar
        new Thread(new Runnable() {
            public void run() {
                int currentPosition = 0;
                while (true) {
                    try {
                        Thread.sleep(100);
                        currentPosition = mediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                    PlayerScreenFragment fragment = (PlayerScreenFragment) getSupportFragmentManager().findFragmentByTag("player");
                    if (fragment != null) {
                        fragment.setProgressBar(currentPosition);
                    }
                }
            }
        }).start();
    }
    ////////////////////////////////////////////////////

    // Search Fragment
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////



    // Fragment methods

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
                        mConcertsFragment.addConcerts(Concert.concertsFromJsonArray(eventsArray)); //
                        ConcertsFragment.mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("populateFragment", String.valueOf(eventsArray.length()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("houston", "nope");
                        if(queryText != null) {
                            ConcertsFragment.searchAdapter.clear();
                            Toast.makeText(MainActivity.this, "There are no concerts for " + queryText + "in your area", Toast.LENGTH_LONG).show(); // maybe make a snack bar to go back to main page, filter, or search again
                        } else {
                            Toast.makeText(MainActivity.this, "Could not load page", Toast.LENGTH_SHORT).show();
                        }
                        ConcertsFragment.mSwipeRefreshLayout.setRefreshing(false);
                    }
//                    mConcertsFragment.addConcerts(Concert.concertsFromJsonArray(eventsArray)); //
//                    ConcertsFragment.mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("populateFragment", "failure");
                    Toast.makeText(MainActivity.this, "Could not display concerts. Pleas wait and try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void populateConcerts(ConcertsFragment fragment, String query) {
        // set ready flag
        readyToPopulate = true;
        // set queryText
        queryText = query;
        // fetch
        fetchConcerts();
    }

    @Override
    public void onConcertTap(Concert concert) {


/*        // open songs fragment --> needs more stuff from songsfrag
        mSongsFragment = mSongsFragment.newInstance(parcelableSongs); // add params if needed
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mSongsFragment);
        ft.addToBackStack("concerts");
        ft.commit();*/

        Intent intent = new Intent(this, ConcertActivity.class);
        // call listener(concert)
        intent.putExtra("concert", Parcels.wrap(concert));
        Toast.makeText(this, concert.getEventName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
        // for some reason the tappin f2nd phish reloads main activity
    }

    // Google api methods
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(MainActivity.this, locationPermissions, LOCATION_PERMISSIONS);

            return;
        } else {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Log.d("lastlocation", lastLocation.toString());
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

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    //Playerbar
    ////////////////////////////////////////////////////
    @Override
    public void openPlayer() {
        PlayerScreenFragment fragment = PlayerScreenFragment.newInstance(songs.get(songNum));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, fragment);
    }

    @Override
    public void playPauseBarBtn() {
        PlayerBarFragment fragment = (PlayerBarFragment) getSupportFragmentManager().findFragmentById(R.id.playerFragment);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            fragment.updatePlay(false);
        }
        else {
            mediaPlayer.start();
            fragment.updatePlay(true);
        }
    }
    ////////////////////////////////////////////////////



    // Concert + Songs Fragment
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////


    ////////////////////////////////////////////////////


}
