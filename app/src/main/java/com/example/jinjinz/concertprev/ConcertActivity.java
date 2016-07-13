package com.example.jinjinz.concertprev;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ConcertActivity extends AppCompatActivity {
    /*
        public static final String client_id = "a021f18f7ee14283b99e1cd75952c4a7"; // Your client id
        public static final String client_secret = "74973f24f5014ce9bb26d8883283ca6a"; // Your secret
    */
    SongsFragment sFragment;
    Button player;
    ArrayList<Song> songs;
    com.example.jinjinz.concertprev.SongArrayAdapter adapter;
    JSONArray allResults;

    //TEMPORARY VARIABLES FOR TESTING
    public String backdropImage;
    public ArrayList<String> artistList;
    public String venue; // may be null (tba)
    public String eventTime; // may be null (tba)
    public String city;
    public String stateCode; // may be null (international events)
    public String countryCode;

    public String artists;

    public AppBarLayout appBar;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public ImageView ivHeader;
    public Toolbar toolbar;
    public TextView tvDate;
    public TextView tvArtists;

    Concert concert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert);

        setUpArtistSearch();

        concert = Parcels.unwrap(getIntent().getParcelableExtra("concert"));
        songs = new ArrayList<>();

        setUpViews();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(concert.getEventName());
        }

        if (savedInstanceState == null) {
            sFragment = SongsFragment.newInstance("params1", "params2");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.songContainer, sFragment);
            ft.commit();
        }
    }

    public void setUpArtistSearch(){
        AsyncHttpClient client = new AsyncHttpClient();
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
        AsyncHttpClient client = new AsyncHttpClient();
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
                    //adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "works", Toast.LENGTH_SHORT).show();

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void setUpViews(){
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ivHeader = (ImageView) findViewById(R.id.ivHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvArtists = (TextView) findViewById(R.id.tvArtists);

        artists = artistsToString(concert.getArtists());


        tvDate.setText(concert.getEventDate());
        tvArtists.setText(artists);
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
                artistNames += ", " + artist_list.get(i);
            }
        }
        return artistNames;
    }
}
