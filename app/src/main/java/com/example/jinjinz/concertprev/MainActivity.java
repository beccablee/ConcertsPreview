package com.example.jinjinz.concertprev;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jinjinz.concertprev.databases.UserDataSource;
import com.example.jinjinz.concertprev.databases.MediaContract;
import com.example.jinjinz.concertprev.databases.MediaObserver;
import com.example.jinjinz.concertprev.fragments.ConcertDetailsFragment;
import com.example.jinjinz.concertprev.fragments.LikedConcertsFragment;
import com.example.jinjinz.concertprev.fragments.LikedSongsFragment;
import com.example.jinjinz.concertprev.fragments.PlayerBarFragment;
import com.example.jinjinz.concertprev.fragments.PlayerScreenFragment;
import com.example.jinjinz.concertprev.fragments.SearchFragment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        SearchFragment.SearchFragmentListener, PlayerScreenFragment.PlayerScreenFragmentListener,
        PlayerBarFragment.PlayerBarFragmentListener, ConcertDetailsFragment.SongsFragmentListener,
        ConcertDetailsFragment.ConcertDetailsFragmentListener, MediaObserver.ContentObserverCallback, UserFragment.UserFragmentListener,
        LikedSongsFragment.LikedSongsFragmentListener, LikedConcertsFragment.LikedConcertsFragmentListener {

    private AsyncHttpClient client;

    // Media player variables
    private Concert mConcert;
    private Song mCurrentSong;
    private Concert mCurrentConcert;
    private boolean isPlaying;
    private int progress;
    private PlayerBarFragment mBarFragment;
    private PlayerScreenFragment mPlayerFragment;
    private View mBarFragmentHolder;
    private Handler mHandler;
    private View mActivityRoot;

    //Media Service
    MediaPlayerService mediaPlayerService;
    boolean mBounded;

    // Search Fragment variables
    private SearchFragment mSearchFragment;
    private boolean fIsReadyToPopulate = false;
    private boolean fIsApiConnected = false;
    private boolean MI_LOCATION_FLAG = true;

    // Concerts details variables
    protected ConcertDetailsFragment mConcertDetailsFragment;

    private String queryText;
    private static final int LOCATION_PERMISSIONS = 10;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private String[] mLocationPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};

    // database variables
    private static int wherePlayerLaunched = 0;
    public static int fromLikedSongs = 1;
    public static int fromConcert = 2;
    public static UserDataSource userDataSource;

    String[] mSongProjection = {
            MediaContract.PlaylistTable._ID,
            MediaContract.PlaylistTable.COLUMN_SPOTIFY_ID,
            MediaContract.PlaylistTable.COLUMN_SONG_NAME,
            MediaContract.PlaylistTable.COLUMN_SONG_ARTIST,
            MediaContract.PlaylistTable.COLUMN_SONG_PREVIEW_URL,
            MediaContract.PlaylistTable.COLUMN_ALBUM_ART_URL,
            MediaContract.PlaylistTable.COLUMN_LIKED
    };
    String[] mConcertProjection = {
            MediaContract.CurrentConcertTable._ID,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_NAME,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_CITY,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_STATE,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_COUNTRY,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_TIME,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_DATE,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_VENUE,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_ARTISTS,
            MediaContract.CurrentConcertTable.COLUMN_CONCERT_IMAGE_URL,
            MediaContract.CurrentConcertTable.COLUNM_CONCERT_LIKED
    };

    String[] mCurrentProjection = {
            MediaContract.CurrentSongTable._ID,
            MediaContract.CurrentSongTable.COLUMN_CURRENT_SONG_ID,
            MediaContract.CurrentSongTable.COLUMN_IS_PLAYING,
            MediaContract.CurrentSongTable.COLUNM_CURRENT_PROGRESS
    };
    Cursor mSongCursor;
    Cursor mCurrentCursor;
    Cursor mConcertCursor;
    MediaObserver mMediaObserver;
    private int songID;

    public static int getWherePlayerLaunched() {
        return wherePlayerLaunched;
    }

    public static void setWherePlayerLaunched(int wherePlayerLaunched) {
        MainActivity.wherePlayerLaunched = wherePlayerLaunched;
    }

    /**
     * Overides super variable
     * Show search fragment first and creates an instance of GoogleApiClient
     * @param savedInstanceState super variable
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create an instance of GoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        // Search fragment should always show first
        if (savedInstanceState == null) {
            mBarFragmentHolder = findViewById(R.id.playerFragment);
            mBarFragmentHolder.setVisibility(View.GONE);
            mSearchFragment = SearchFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, mSearchFragment);
            mBarFragment = PlayerBarFragment.newInstance();
            ft.replace(R.id.playerFragment, mBarFragment, "bar");
            ft.commit();
            mHandler = new Handler(Looper.getMainLooper());
            mActivityRoot = findViewById(R.id.mainActivity);

            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mBarFragmentHolder.getVisibility() == View.VISIBLE) {
                                mActivityRoot.requestLayout();
                            }
                        }
                    });
                }
            });
        }

        // open data source
        userDataSource = new UserDataSource(MainActivity.this);
        userDataSource.openDB();
    }

    /**
     * Synce up the activity member variables with the database
     */
    public void setMediaVariables() {
        mCurrentCursor = getContentResolver().query(MediaContract.CurrentSongTable.CONTENT_URI, mCurrentProjection, null, null, null);
        mCurrentCursor.moveToFirst();
        songID = mCurrentCursor.getInt(mCurrentCursor.getColumnIndex(MediaContract.CurrentSongTable.COLUMN_CURRENT_SONG_ID));
        int play = mCurrentCursor.getInt(mCurrentCursor.getColumnIndex(MediaContract.CurrentSongTable.COLUMN_IS_PLAYING));
        if (play == 0) {
            isPlaying = false;
        } else {
            isPlaying = true;
        }
        progress = mCurrentCursor.getInt(mCurrentCursor.getColumnIndex(MediaContract.CurrentSongTable.COLUNM_CURRENT_PROGRESS));
        mConcertCursor = getContentResolver().query(MediaContract.CurrentConcertTable.CONTENT_URI, mConcertProjection, null, null, null);
        mConcertCursor.moveToFirst();
        mCurrentConcert = mediaCursorToConcert(mConcertCursor);
        Uri singleUri = ContentUris.withAppendedId(MediaContract.PlaylistTable.CONTENT_URI, songID);
        mSongCursor = getContentResolver().query(singleUri, mSongProjection, null, null, null);
        mSongCursor.moveToFirst();
        mCurrentSong = mediaCursorToSong(mSongCursor);
    }

    /**
     * connect GoogleAPIClient
     */
    protected void onStart() {
        mGoogleApiClient.connect();
        Intent i = new Intent(this, MediaPlayerService.class);
        bindService(i, mConnection, BIND_AUTO_CREATE);
        super.onStart();
    }

    /**
     * unconnect GoogleAPICLient
     */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        super.onStop();
    }

    /**
     * register MediaObserver (subclass of ContentObserver)
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaObserver == null) {
            mMediaObserver = new MediaObserver(this);
        }
        getContentResolver().registerContentObserver(MediaContract.BASE_CONTENT_URI, true, mMediaObserver);
    }

    /**
     * unregister MediaObserver (subclass of ContentObserver)
     */
    @Override
    protected void onPause() {
        getContentResolver().unregisterContentObserver(mMediaObserver);
        super.onPause();
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBounded = true;
            MediaPlayerService.LocalBinder mLocalBinder = (MediaPlayerService.LocalBinder) iBinder;
            mediaPlayerService = mLocalBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBounded = false;
            mediaPlayerService = null;
        }
    };


    //Player + MediaPlayer methods
    ////////////////////////////////////////////////////

    /**
     * Method to start playing a new concert
     * @param c current Concert
     * @param s current ArrayList of songs in playlist
     */
    private void onNewConcert(Concert c, ArrayList<Song> s) {
        Intent i = new Intent(this, MediaPlayerService.class);
        Concert concert;
        if (c == null) {
            concert = new Concert();
            concert.setEventName("My Songs");
        } else {
            concert = c;
        }
        i.putExtra("concert", Parcels.wrap(concert));
        ArrayList<Parcelable> songs = new ArrayList<>();
        for (int j = 0; j < s.size(); j++) {
            songs.add(Parcels.wrap(s.get(j)));
        }
        i.putExtra("songs", songs);
        startService(i);
    }

    /**
     * Override PlayerScreenFragmentListener
     * @return current Concert name
     */
    @Override
    public String getConcertName() {
        if (mCurrentConcert != null) {
            return mCurrentConcert.getEventName();
        } else {
            return "";
        }
    }

    /**
     * Override PlayerScreenFragmentListener
     * Goes to current concert playlist or user profile if launched from liked songs
     */
    @Override
    public void onConcertClick() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getConcertName().equals("My Songs")) {
            UserFragment userFragment = UserFragment.newInstance();
            ft.replace(R.id.mainFragment, userFragment);
            ft.addToBackStack("my music");
        } else if (mCurrentConcert != null && !getConcertName().equals("")) {
            ConcertDetailsFragment concertDetailsFragment = ConcertDetailsFragment.newInstance(Parcels.wrap(mCurrentConcert));
            ft.replace(R.id.mainFragment, concertDetailsFragment, "details");
            ft.addToBackStack("details");
        }
        ft.commit();
    }

    /**
     * Override PlayerScreenFragmentListener
     * play or pause depending state of media player
     */
    @Override
    public void playPauseSong() {
        mediaPlayerService.playPauseSong();
    }

    /**
     * Override PlayerScreenFragmentListener
     * go back one layer in backstack
     */
    @Override
    public void backInStack() {
        super.onBackPressed();
    }


    /**
     * Override PlayerScreenFragmentListener
     * Start playing next song
     */
    @Override
    public void skipNext() {
        mediaPlayerService.skipNext();
    }

    /**
     * Override PlayerScreenFragmentListener
     * Go back to previous song
     */
    @Override
    public void skipPrev() {
        mediaPlayerService.skipPrev();
    }

    /**
     * Override PlayerScreenFragmentListener
     * show bottom bar fragment
     */
    @Override
    public void onClosePlayer() {
        mBarFragmentHolder.setVisibility(View.VISIBLE);
        setBarUI();
    }

    /**
     * Override PlayerScreenFragmentListener
     * update playerScreen UI
     */
    @Override
    public void setUI() {
        if (mPlayerFragment != null && mPlayerFragment.isVisible() && mCurrentSong != null) {
            mPlayerFragment.updateInterface(mCurrentSong);
            mPlayerFragment.setPlayBtn(isPlaying);
            mPlayerFragment.setProgressBar(progress);
            mPlayerFragment.setLikeBtn(mCurrentSong);
        }
    }

    /**
     * Override PlayerScreenFragmentListener
     * close bar
     */
    @Override
    public void onPlayerOpen() {
        if (mBarFragment != null) {
            mBarFragmentHolder.setVisibility(View.GONE);
        }
        setUI();
    }

    // Search Fragment methods
    ////////////////////////////////////////////////////

    // Fragment methods

    /**f
     * Packages parameters and calls the client to retrieve concerts from Ticketmaster
     * Checks if the fragment is ready to be populated and if the google api client has connected and attempted to access location
     * */
    public void fetchConcerts() {
        if (fIsReadyToPopulate && fIsApiConnected) {
            // url includes api key and music classification
            String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json";
            // the parameter(s)
            RequestParams params = new RequestParams();
            if (queryText != null) {
                params.put("keyword", queryText);
            }
            String latlong;
            if (lastLocation != null && MI_LOCATION_FLAG) {
                latlong = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
            } else if (lastLocation == null && !MI_LOCATION_FLAG) {
                latlong = null;
            } else {
                latlong = null;
            }

            params.put("apikey", "7elxdku9GGG5k8j0Xm8KWdANDgecHMV0");
            params.put("classificationName", "Music");
            params.put("startDateTime", getUtcTime(getCurrentDate()));
            Log.d("event date", getUtcTime(getCurrentDate()));
            params.put("latlong", latlong); // must be N, E
            params.put("radius", "50");
            params.put("size", "15");
            params.put("page", 0);

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
                        SearchFragment.searchAdapter.clear();
                        mSearchFragment.addConcerts(Concert.concertsFromJsonArray(eventsArray));
                        SearchFragment.mSwipeRefreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("client calls", "error adding concerts: " + statusCode);
                        if (queryText != null) {
                            SearchFragment.searchAdapter.clear();
                            Toast.makeText(MainActivity.this, "There are no concerts for " + queryText + " in your area", Toast.LENGTH_LONG).show(); // maybe make a snack bar to go back to main page, filter, or search again
                        } else {
                            Toast.makeText(MainActivity.this, "Could not load page", Toast.LENGTH_SHORT).show();
                        }
                        SearchFragment.mSwipeRefreshLayout.setRefreshing(false);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("client calls", "TicketMaster client GET error: " + statusCode);
                    Toast.makeText(MainActivity.this, "Could not display concerts. Please wait and try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    /** Calls the next page of concerts from Ticketmaster to allow for endless scrolling
     * @param page the page number to fetch from  */
    @Override
    public void fetchMoreConcerts(int page) {
        // url includes api key and music classification
        String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json";
        // the parameters
        RequestParams params = new RequestParams();
        if (queryText != null) {
            params.put("keyword", queryText);
        }
        String latlong;
        if (lastLocation != null) {
            latlong = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
        } else {
            latlong = null;
        }

        params.put("apikey", "7elxdku9GGG5k8j0Xm8KWdANDgecHMV0");
        params.put("classificationName", "Music");
        params.put("startDateTime", getUtcTime(getCurrentDate()));
        Log.d("event date", getUtcTime(getCurrentDate()));
        params.put("latlong", latlong); // must be N, E
        params.put("radius", "50");
        params.put("size", "15");
        params.put("page", page);

        // call client
        client = new AsyncHttpClient();
        client.get(eventsURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) { // on success will return the large json obj: { _embedded: { events: [ {0, 1, 2, ..} ] } }
                // DESERIALIZE JSON
                // CREATE MODELS AND ADD TO ADAPTER
                // LOAD MODEL INTO LIST VIEW
                JSONArray eventsArray = null;
                try {
                    eventsArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                    mSearchFragment.addConcerts(Concert.concertsFromJsonArray(eventsArray)); //
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("client calls", "error adding concerts: " + statusCode);
                    if (queryText != null) {
                        Toast.makeText(MainActivity.this, "There are no concerts for " + queryText + " in your area", Toast.LENGTH_LONG).show(); // maybe make a snack bar to go back to main page, filter, or search again
                    } else {
                        Toast.makeText(MainActivity.this, "Could not load page", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("client calls", "TicketMaster client GET error: " + statusCode);
                Toast.makeText(MainActivity.this, "Could not display concerts. Please wait and try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Flags that the search fragment is ready to be populated and calls the fetch concerts method
     * @param query passes the query value to the fetch method. Null until user searches in toolbar
     * */
    @Override
    public void populateConcerts(String query) {
        // set ready flag
        fIsReadyToPopulate = true;
        // set queryText for use in concerts fetch method
        queryText = query;
        fetchConcerts();
    }

    public void toggleLocationUse(MenuItem item) {
        if (MI_LOCATION_FLAG) {
            MI_LOCATION_FLAG = false;
            item.setIcon(getDrawable(R.drawable.ic_location_deactivated));
            Toast.makeText(this, "Location off", Toast.LENGTH_SHORT).show();
            fetchConcerts();
        } else {
            MI_LOCATION_FLAG = true;
            item.setIcon(getDrawable(R.drawable.ic_location_activated));
            Toast.makeText(this, "Location on", Toast.LENGTH_SHORT).show();
            fetchConcerts();
        }
    }

    /**
     * Replaces the search fragment with the concert details fragment when a concert is tapped
     * @param concert the concert that was tapped
     * */
    @Override
    public void onConcertTap(Concert concert) {
        if (userDataSource.isConcertAlreadyInDb(concert)) {
            concert = userDataSource.getConcertFromDB(concert);
        }
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
                || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, mLocationPermissions, LOCATION_PERMISSIONS);

        } else {
            Log.d("location", String.valueOf(((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) != PackageManager.PERMISSION_GRANTED)));
            Log.d("location", String.valueOf(((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)) != PackageManager.PERMISSION_GRANTED)));
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            fIsApiConnected = true;
            fetchConcerts();

        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        //TODO
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case LOCATION_PERMISSIONS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("requestlocation", "Permissions Granted");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    fIsApiConnected = true;
                    fetchConcerts();

                } else {
                    Log.d("requestlocation", "Permissions Denied");
                    Toast.makeText(MainActivity.this, "Location is necessary to find concerts in your area", Toast.LENGTH_LONG).show();
                    //TODO: add snackbar or dialogue box to ask them to allow location
                }

            }
        }
    }


    /**
     * Override PlayerBarFragmentListener
     * opens PlayerScreen Fragment
     */
    @Override
    public void showPlayer() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mPlayerFragment, "player");
        if (mBarFragment != null) {
            mBarFragmentHolder.setVisibility(View.GONE);
        }
        ft.addToBackStack("player");
        ft.commit();
    }

    /**
     * Override PlayerBarFragmentListener
     * play/pauses MediaPlayer
     */
    @Override
    public void playPauseBarBtn() {
       mediaPlayerService.playPauseSong();
    }

    /**
     * Override PlayerBarFragmentListener
     * update player bar UI
     */
    @Override
    public void setBarUI() {
        if (mBarFragmentHolder != null && mPlayerFragment != null && !mPlayerFragment.isVisible()) {
            mBarFragmentHolder.setVisibility(View.VISIBLE);
        }
        if (mBarFragmentHolder != null && mBarFragmentHolder.getVisibility() == View.VISIBLE) {
            mBarFragment.setInterface(mCurrentSong);
            mBarFragment.setPlay(isPlaying);
        }
    }

    // Concert + Songs Fragment
    ////////////////////////////////////////////////////

    /**
     *  Searches for an artist on Spotify and gets their ID from the first search result
     *  */
    public void searchArtistOnSpotify(final SongsFragment fragment, final int artistIndex, final int songsPerArtist, final ArrayList<String> artists, final Concert concert){
        String url = "https://api.spotify.com/v1/search";

        client = new AsyncHttpClient();
        mConcert = concert;
        RequestParams params = new RequestParams();
        params.put("q", artists.get(artistIndex));
        params.put("type", "artist");
        params.put("limit", 1);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String artistJSONResult;
                try {
                    // Search for the artist's top tracks
                    artistJSONResult = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("id");
                    searchArtistTopTracks(fragment, artistJSONResult, songsPerArtist, artists, artistIndex, concert);
                } catch (JSONException e){
                    e.printStackTrace();
                    Log.d("client calls", "could not retrieve artist id: " + statusCode + artistIndex + ' ' + artists.size());
                    searchForNextArtist(artistIndex, artists, fragment, songsPerArtist, concert);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("client calls", "Spotify client GET error: " + statusCode + artistIndex + ' ' + artists.size());
                searchForNextArtist(artistIndex, artists, fragment, songsPerArtist, concert);
            }
        });
    }

    /**
     * Searches for an artist's top tracks on Spotify and adds them to the SongsFragment
     * */
    public void searchArtistTopTracks(final SongsFragment fragment, final String artistId, final int songsPerArtist, final ArrayList<String> artists, final int artistIndex, final Concert concert){
        String ISOCountryCode = "US";
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks";
        RequestParams params = new RequestParams();
        params.put("country", ISOCountryCode);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray songsJsonResult;
                try {
                    // Search for and add a specific number of artist's tracks to fragment
                    songsJsonResult = response.getJSONArray("tracks");
                    fragment.addSongs(Song.fromJSONArray(songsJsonResult, songsPerArtist));
                } catch (JSONException e){
                    e.printStackTrace();
                    Log.d("client calls", "Spotify client tracks GET error: " + statusCode);
                }
                searchForNextArtist(artistIndex, artists, fragment, songsPerArtist, concert);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("client calls", "Songs failed to load");
                searchForNextArtist(artistIndex, artists, fragment, songsPerArtist, concert);
            }
        });
    }

    /**
     * Search for the next artist in the ArrayList or check for null state for playlist
     * */
    public void searchForNextArtist(int artistIndex, ArrayList<String> artists, SongsFragment fragment, int songsPerArtist, Concert concert){
        // Search for next artist in ArrayList
        if (artistIndex + 1 < artists.size()) {
            searchArtistOnSpotify(fragment, artistIndex + 1, songsPerArtist, artists, concert);
        }
        // Null state: check if no songs have loaded from any artist
        else if (artistIndex + 1 >= artists.size() && fragment.getSongsCount() == 0){
            fragment.noSongsLoaded();
        }
    }

    /**
     * Launches song player with a specific song in a playlist
     * */
    @Override
    public void launchSongPlayer(Song song, ArrayList<Parcelable> tempSongs, Concert concert){
        if (mPlayerFragment == null) {
            mPlayerFragment = PlayerScreenFragment.newInstance();
        }
        ArrayList<Song> pSongs = new ArrayList<>();
        for (int i = 0; i < tempSongs.size(); i++) {
            Song songTemp = (Song) Parcels.unwrap(tempSongs.get(i));
            if (userDataSource.isSongAlreadyInDb(songTemp)) {
                songTemp.setLiked(true);
            }
            else {
                songTemp.setLiked(false);
            }
            pSongs.add(i, songTemp);
        }
        Collections.shuffle(pSongs);
        if(userDataSource.isSongAlreadyInDb(song)) {
            song.setLiked(true);
        }
        pSongs.add(0, song);
        onNewConcert(concert, pSongs);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mPlayerFragment, "player");
        if (mBarFragment != null) {
            mBarFragmentHolder.setVisibility(View.GONE);
        }

        ft.addToBackStack("player");
        ft.commit();
    }


    // User Profile Methods
    ////////////////////////////////////////////////////

    /** Switches to user fragment when user menu button is clicked
     * @param item the user button */
    public void getProfile(MenuItem item) {
        UserFragment userFragment = UserFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, userFragment, "user");
        ft.addToBackStack("user");
        ft.commit();
    }

    /**
     *  "Likes" a concert by calling to the datasource to add it to the database
     *  @param concert the concert to be liked
     *  */
    @Override
    public Concert likeConcert(Concert concert) {
        Concert likedConcert = userDataSource.likeConcert(concert); // right here the dbid is still -1L
        return likedConcert;
    }

    /**
     * Returns all the liked concerts in the local SQLite database
     * @return array list of liked concerts
     */
    public static ArrayList<Concert> getLikedConcerts() {
        return userDataSource.getAllLikedConcerts();
    }

    /**
     *  "Likes" a concert by calling to the datasource to add it to the database
     *  */
    @Override
    public void likeSong() {
        Log.i("songCheck", mCurrentSong.getArtistsString());
        Boolean likedSong = userDataSource.likeSong(mCurrentSong);

        // Defines selection criteria for the rows you want to update
        String mSelectionClause = MediaContract.PlaylistTable.COLUMN_SPOTIFY_ID +  " LIKE ?";
        String[] mSelectionArgs = {mCurrentSong.getSpotifyID()};

        if (likedSong == null) {
            return;
        }
        else if (likedSong) {
            ContentValues likedValue = new ContentValues();
            likedValue.put(MediaContract.PlaylistTable.COLUMN_LIKED, 1);
            getContentResolver().update(MediaContract.PlaylistTable.CONTENT_URI, likedValue, mSelectionClause, mSelectionArgs);
            mCurrentSong = mediaCursorToSong(mSongCursor);
        }
        else {
            ContentValues likedValue = new ContentValues();
            likedValue.put(MediaContract.PlaylistTable.COLUMN_LIKED, 0);
            getContentResolver().update(MediaContract.PlaylistTable.CONTENT_URI, likedValue, mSelectionClause, mSelectionArgs);
            mCurrentSong = mediaCursorToSong(mSongCursor);
        }
    }

    /**
     * Returns all the liked songs in the local SQLite database
     * @return array list of liked songs
     */
    public static ArrayList<Song> getLikedSongs() {
        return userDataSource.getAllLikedSongs();
    }

    /**
     * Gets the local time and date of the phone
     * @return the current local time and date
     */
    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currDate = cal.getTime();
        return currDate;
    }

    /**
     * converts result from PlaylistTable to Song object
     *  @param cursor the cursor of the database query
     * @return new Song object
     */
    public static Song mediaCursorToSong(Cursor cursor) {
        Song song = new Song();
        song.setDbID(-1L);
        song.setSpotifyID(cursor.getString(1));
        song.setName(cursor.getString(2));
        song.setArtistsString(cursor.getString(cursor.getColumnIndex(MediaContract.PlaylistTable.COLUMN_SONG_ARTIST)));
        song.setArtists(song.artistListToArray(song.getArtistsString()));
        song.setPreviewUrl(cursor.getString(4));
        song.setAlbumArtUrl(cursor.getString(5));
        int liked = cursor.getInt(cursor.getColumnIndex(MediaContract.PlaylistTable.COLUMN_LIKED));
        if (liked == 1) {
            song.setLiked(true);
        }
        else {
            song.setLiked(false);
        }
        return song;
    }

    /**
     * converts result from ConcertTable to Concert object
     * @param cursor the cursor of the database query
     * @return returns the concert */
    public static Concert mediaCursorToConcert(Cursor cursor) {
        Concert concert = new Concert();
        concert.setDbId(cursor.getInt(cursor.getColumnIndex(MediaContract.CurrentConcertTable.COLUNM_CONCERT_LIKED)));
        concert.setEventName(cursor.getString(1));
        concert.setCity(cursor.getString(2));
        concert.setStateCode(cursor.getString(3)); // null for international concerts
        concert.setCountryCode(cursor.getString(4));
        concert.setVenue(cursor.getString(5));
        concert.setEventTime(cursor.getString(6));
        concert.setEventDate(cursor.getString(7));
        concert.setArtistsString(cursor.getString(8));
        if (cursor.getString(8) != null) {
            String[] artists = cursor.getString(8).split(", ");
            ArrayList<String> artistList = new ArrayList<>(Arrays.asList(artists));
            concert.setArtists(artistList);
        }
        concert.setBackdropImage(cursor.getString(9));
        return concert;
    }

    /**
     * Transforms and formats the local time into Coordinated Universal Time (UTC) for the Ticketmaster API
     * @param date the current local time and date
     * @return string of current time in UTC in the acceptable Ticketmaster format
     */
    private String getUtcTime(Date date) {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dayFormatter.setTimeZone(utcTimeZone);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        timeFormatter.setTimeZone(utcTimeZone);
        String day = dayFormatter.format(date);
        String time = timeFormatter.format(date);
        String apiDate = day + "T" + time + "Z";
        return apiDate;
    }

    /**
     * updates UI of everything visible when called
     */
    @Override
    public void updateObserver() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setMediaVariables();
                setUI();
                setBarUI();
            }
        });
    }
}

