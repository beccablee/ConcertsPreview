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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by beccalee on 7/18/16.
 */
public class ConcertDetailsFragment extends SongsFragment {

    private ArrayList<Parcelable> songs;
    private Concert concert;
    public String artists; // Fragment
    SongsFragment mSongsFragment; // Main

    public AppBarLayout appBar;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public ImageView ivHeader;
    public Toolbar toolbar;
    public TextView tvEvent;
    public TextView tvDate;
    public TextView tvArtists;
    public Button btnLikeConcert;

    public ConcertDetailsFragment() {
        // Required empty public constructor
    }

    public interface ConcertDetailsFragmentListener {
        void onLikeConcert(Concert concert);
    }

    ConcertDetailsFragmentListener concertDetailsFragmentListener;

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

        if (savedInstanceState == null) {
            mSongsFragment = SongsFragment.newInstance(Parcels.wrap(concert));
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.songContainer, mSongsFragment).commit();
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
        concertDetailsFragmentListener = (ConcertDetailsFragmentListener) context;
    }


    public void setUpViews(View view){
        appBar = (AppBarLayout) view.findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        ivHeader = (ImageView) view.findViewById(R.id.ivHeader);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //tvEvent = (TextView) view.findViewById(R.id.tvEvent);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvArtists = (TextView) view.findViewById(R.id.tvArtists);
        btnLikeConcert = (Button) view.findViewById(R.id.btnLikeConcert);
        btnLikeConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Concert concertToLike = new Concert();
                concertToLike = concert;
                concertDetailsFragmentListener.onLikeConcert(concertToLike);
            }
        });

        artists = concert.artistsString;

        String date = concert.getEventDate();

        ivHeader.setTag(concert);

        tvEvent.setText(concert.getEventName());
        tvDate.setText(date);

        if (concert.getVenue() == null) {
            tvArtists.setText(artists);
        }
        else {
            tvArtists.setText(artists + " at " + concert.getVenue());
        }
        Picasso.with(getContext()).load(concert.backdropImage).into(ivHeader);
        toolbar.setTitle(concert.getEventName());
    }

}
