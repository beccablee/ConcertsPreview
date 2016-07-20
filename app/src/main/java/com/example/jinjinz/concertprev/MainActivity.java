package com.example.jinjinz.concertprev;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.jinjinz.concertprev.databases.UserDataSource;
import com.example.jinjinz.concertprev.fragments.ConcertDetailsFragment;
import com.example.jinjinz.concertprev.fragments.ConcertsFragment;
import com.example.jinjinz.concertprev.fragments.PlayerBarFragment;
import com.example.jinjinz.concertprev.fragments.PlayerScreenFragment;
import com.example.jinjinz.concertprev.fragments.SongsFragment;
import com.example.jinjinz.concertprev.fragments.UserFragment;
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
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ConcertsFragment.ConcertsFragmentListener,
        PlayerScreenFragment.PlayerScreenFragmentListener, PlayerBarFragment.PlayerBarFragmentListener, ConcertDetailsFragment.SongsFragmentListener, ConcertDetailsFragment.ConcertDetailsFragmentListener {

    Concert concert;
    ArrayList<Song> songs;
    ArrayList<Parcelable> pSongs; //I need this now
    Concert pConcert;
    MediaPlayer mediaPlayer;
    private int songNum;
    private Button testBtn;

    ConcertsFragment mConcertsFragment; // concerts fragment
    ConcertDetailsFragment mConcertDetailsFragment; // songs fragment

    PlayerBarFragment barFragment;
    PlayerScreenFragment playerFragment;

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

        // open datasource for use
        userDataSource = new UserDataSource(MainActivity.this);
        userDataSource.openDB();

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

            //on prepared listener --> what happens when it is ready to play
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (playerFragment != null && playerFragment.isVisible()) {
                        playerFragment.updateInterface(songs.get(songNum));
                        playerFragment.updatePlay(true);
                    }
                    if (barFragment != null && barFragment.isVisible()) {
                        barFragment.updatePlay(true);
                        barFragment.updateInterface(songs.get(songNum));
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
         else {
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
    public void backInStack() {
        super.onBackPressed();
    }
    @Override
    public void skipNext() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            songNum++;
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
        //PlayerBarFragment playerBar = (PlayerBarFragment) getSupportFragmentManager().findFragmentByTag("bar");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (barFragment == null) {
            barFragment = PlayerBarFragment.newInstance(songs.get(songNum));
            ft.replace(R.id.playerFragment, barFragment, "bar");
            ft.commit();
        }
        else {
            ft.show(barFragment);
            ft.commit();
            onOpenBar();
        }

    }

    @Override
    public void onOpenPlayer() {
        playerFragment.updateInterface(songs.get(songNum));
        playerFragment.updatePlay(mediaPlayer.isPlaying());
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
                    //PlayerScreenFragment fragment = (PlayerScreenFragment) getSupportFragmentManager().findFragmentByTag("player");
                    if (playerFragment != null && playerFragment.isVisible()) {
                        playerFragment.setProgressBar(currentPosition);
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
        if (readyToPopulate && apiConnected) { //////////// should be && --> onConnected()
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
    //TODO figure out what's up
    @Override
    public void onConcertTap(Concert concert) {
        // open songs fragment --> needs more stuff from songsfrag
        mConcertDetailsFragment = mConcertDetailsFragment.newInstance(Parcels.wrap(concert)); // add params if needed
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mConcertDetailsFragment);
        ft.addToBackStack("concerts");
        ft.commit();
    }

    ///// Google api methods /////
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, locationPermissions, LOCATION_PERMISSIONS);

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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, playerFragment, "player");
        if (barFragment != null) {
            ft.hide(getSupportFragmentManager().findFragmentByTag("bar"));
        }
        ft.addToBackStack("player");
        ft.commit();
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
    @Override
    public void onOpenBar() {
        barFragment.updateInterface(songs.get(songNum));
        if (mediaPlayer.isPlaying()) {
            barFragment.updatePlay(true);
        }
        else {
            barFragment.updatePlay(false);
        }

    }
    ////////////////////////////////////////////////////



    // Concert + Songs Fragment
    ////////////////////////////////////////////////////

    public void setUpArtistSearch(final SongsFragment fragment, Concert concert, int artistIndex, final int songsPerArtist){
        String url = "https://api.spotify.com/v1/search";

        client = new AsyncHttpClient();
        pSongs = new ArrayList<>();
        pConcert = concert;
        RequestParams params = new RequestParams();
        params.put("q", concert.getArtists().get(artistIndex));
        params.put("type", "artist");
        params.put("limit", 1);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String artistJSONResult;
                try {
                    artistJSONResult = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("id");
                    searchArtistPlaylist(fragment, artistJSONResult, songsPerArtist);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", "" + statusCode);
            }
        });
    }

    public void searchArtistPlaylist(final SongsFragment fragment, String artistId, final int songsPerArtist){
        String ISOCountryCode = "US";
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks";
        RequestParams params = new RequestParams();
        params.put("country", ISOCountryCode);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray songsJSONResult;
                try {
                    songsJSONResult = response.getJSONArray("tracks");
                    pSongs.addAll(Song.fromJSONArray(songsJSONResult, songsPerArtist));
                    fragment.addSongs(Song.fromJSONArray(songsJSONResult, songsPerArtist));

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "songs failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void launchSongView(Song song, ArrayList<Parcelable> tempSongs){
        if (playerFragment == null) {
            playerFragment = playerFragment.newInstance(song);
        }
        ArrayList<Song> pSongs2 = new ArrayList<>();
        for (int i = 0; i < tempSongs.size(); i++) {
            pSongs2.add(i, (Song) Parcels.unwrap(tempSongs.get(i)));
        }
        Collections.shuffle(pSongs2);
        pSongs2.add(0, song);
        onNewConcert(pConcert, pSongs2);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, playerFragment, "player");
        if (barFragment != null) {
            ft.hide(getSupportFragmentManager().findFragmentByTag("bar"));
        }

        ft.addToBackStack("player");
        ft.commit();
    }
    public static UserDataSource userDataSource;

    @Override
    public void onLikeConcert(Concert concert) {
        userDataSource.insertLikedConcert(concert); // adds concert to db
    }

    public void userProfile(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, DBTestActivity.class);
        ArrayList<Concert> likedConcerts = userDataSource.getAllLikedConcerts();
        intent.putExtra("concerts", Parcels.wrap(likedConcerts));
        startActivity(intent);
    }


    ////////////////////////////////////////////////////

    public void getProfile(MenuItem item) {
        UserFragment userFragment = UserFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, userFragment, "user");
        ft.addToBackStack("user");
        ft.commit();
    }


}
