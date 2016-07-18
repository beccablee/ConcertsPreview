package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinjinz.concertprev.Adapters.SongArrayAdapter;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by beccalee on 7/18/16.
 */
public class ConcertDetailsFragment extends SongsFragment {

    private ArrayList<Parcelable> songs;
    private Concert concert;
    public String artists; // Fragment
    SongsFragment sFragment; // Main
    public SongArrayAdapter adapter;

    //Button player;

    public AppBarLayout appBar;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public ImageView ivHeader;
    public Toolbar toolbar;
    public TextView tvEvent;
    public TextView tvDate;
    public TextView tvArtists;

    public ConcertDetailsFragment() {
        // Required empty public constructor
    }

    public static ConcertDetailsFragment newInstance(Parcelable concert) {
        ConcertDetailsFragment fragment = new ConcertDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("concert", concert);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        concert = Parcels.unwrap(getArguments().getParcelable("concert"));
        songs = new ArrayList<>();

        if (savedInstanceState == null) {
            sFragment = SongsFragment.newInstance(Parcels.wrap(concert));
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.songContainer, sFragment).commit();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concert_details, container, false);
        setUpViews(view);
        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public void setUpViews(View view){
        appBar = (AppBarLayout) view.findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        ivHeader = (ImageView) view.findViewById(R.id.ivHeader);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar); //Ask Rebecca: what is this for?
        tvEvent = (TextView) view.findViewById(R.id.tvEvent);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvArtists = (TextView) view.findViewById(R.id.tvArtists);

        artists = artistsToString(concert.getArtists());

        String date = formatDate(concert.getEventDate());

        tvEvent.setText(concert.getEventName());
        tvDate.setText(date);
        tvArtists.setText(artists + " at " + concert.getVenue());
        Picasso.with(getContext()).load(concert.backdropImage).into(ivHeader);

//        player = (Button) view.findViewById(R.id.playerBtn2);
//        player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(ConcertActivity.this, PlayerActivity.class);
//                startActivity(i);
//            }
//        });
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
            newDate = format.parse(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy");
        String date = format.format(newDate);
        return date;
    }


}
