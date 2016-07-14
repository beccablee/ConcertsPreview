package com.example.jinjinz.concertprev;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinjinz.concertprev.Adapters.SongArrayAdapter;
import com.example.jinjinz.concertprev.fragments.SongsFragment;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ConcertActivity extends AppCompatActivity {
    /*
        public static final String client_id = "a021f18f7ee14283b99e1cd75952c4a7"; // Your client id
        public static final String client_secret = "74973f24f5014ce9bb26d8883283ca6a"; // Your secret
    */
    SongsFragment sFragment;
    Button player;
    ArrayList<Parcelable> songs;
    SongArrayAdapter adapter;
    JSONArray allResults;

    //TEMPORARY VARIABLES FOR TESTING
    public String eventTime; // may be null (tba)
    public String city;
    public String stateCode; // may be null (international events)
    public String countryCode;

    public String artists;

    public AppBarLayout appBar;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public ImageView ivHeader;
    public Toolbar toolbar;
    public TextView tvEvent;
    public TextView tvDate;
    public TextView tvArtists;

    AsyncHttpClient client;
    Concert concert;

    int numberOfSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert);

        concert = Parcels.unwrap(getIntent().getParcelableExtra("concert"));
        songs = new ArrayList<>();
        //adapter = new SongArrayAdapter(this, songs);
        client = new AsyncHttpClient();

        setUpArtistSearch();

        setUpViews();

        //setSupportActionBar(toolbar);
        //if (getSupportActionBar() != null) {
        //    getSupportActionBar().setTitle(concert.getEventName());
        //}

        if (savedInstanceState == null) {
            sFragment = SongsFragment.newInstance(songs);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.songContainer, sFragment);
            ft.commit();
        }
    }

    public void setUpArtistSearch(){
        String url = "https://api.spotify.com/v1/search";

        RequestParams params = new RequestParams();
        params.put("q", "Drake");
        params.put("type", "artist");
        params.put("limit", 1);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String artistJSONResult;
                try {
                    artistJSONResult = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("id");
                    searchArtistPlaylist(artistJSONResult);
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

    public void searchArtistPlaylist(String artistId){
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
                    songs.addAll(Song.fromJSONArray(songsJSONResult));
                    Toast.makeText(getApplicationContext(), "songs loaded: " + songs.size(), Toast.LENGTH_SHORT).show();

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

    public void setUpViews(){
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ivHeader = (ImageView) findViewById(R.id.ivHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvEvent = (TextView) findViewById(R.id.tvEvent);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvArtists = (TextView) findViewById(R.id.tvArtists);

        artists = artistsToString(concert.getArtists());

        String date = formatDate(concert.getEventDate());

        tvEvent.setText(concert.getEventName());
        tvDate.setText(date);
        tvArtists.setText(artists + " at " + concert.getVenue());
        Picasso.with(this).load(concert.backdropImage).into(ivHeader);

        player = (Button) findViewById(R.id.playerBtn2);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConcertActivity.this, PlayerActivity.class);
                startActivity(i);
            }
        });
    }

    public String artistsToString(ArrayList<String> artist_list) {
        String artistNames = "";
        for (int i = 0; i < artist_list.size(); i++){
            if (i == 0) {
                artistNames += artist_list.get(i);
            } else {
                artistNames += " & " + artist_list.get(i);
            }
        }
        return artistNames;
    }

    public String formatDate(String originalDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(concert.getEventDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy");
        String date = format.format(newDate);
        return date;
    }
}
