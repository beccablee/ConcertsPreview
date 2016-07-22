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

/**
 * Created by beccalee on 7/18/16.
 */
public class ConcertDetailsFragment extends SongsFragment {
    private Concert concert;
    public String artists;

    public AppBarLayout appBar;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public ImageView ivHeader;
    public Toolbar toolbar;
    public TextView tvEvent;
    public TextView tvDate;
    public TextView tvVenue;
    public TextView tvArtists;
    public Button btnLikeConcert;
    public Button btnUnlikeConcert;

    SongsFragment mSongsFragment;
    ConcertDetailsFragmentListener concertDetailsFragmentListener;

    // Required empty public constructor
    public ConcertDetailsFragment() {
    }

    /* Communicates between ConcertDetailsFragment and MainActivity */
    public interface ConcertDetailsFragmentListener {
        void onLikeConcert(Concert concert);
        void onUnlikeConcert(Concert concert);
    }

    /* Creates a new instance of the ConcertDetailsFragment and gets concert Object (Parcelable) */
    public static ConcertDetailsFragment newInstance(Parcelable concert) {
        ConcertDetailsFragment fragment = new ConcertDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("concert", concert);
        fragment.setArguments(args);
        return fragment;
    }

    /* Creates a SongsFragment nested in the ConcertDetailsFragment */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        concert = Parcels.unwrap(getArguments().getParcelable("concert"));

        if (savedInstanceState == null) {
            SongsFragment songsFragment = SongsFragment.newInstance(Parcels.wrap(concert));
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.songContainer, songsFragment).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concert_details, container, false);
        setUpViews(view);
        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        concertDetailsFragmentListener = (ConcertDetailsFragmentListener) context;
    }

    /** Finds and populates views for the fragment */
    public void setUpViews(View view){
        appBar = (AppBarLayout) view.findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        ivHeader = (ImageView) view.findViewById(R.id.ivHeader);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvEvent = (TextView) view.findViewById(R.id.tvEvent);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvVenue = (TextView) view.findViewById(R.id.tvVenue);
        tvArtists = (TextView) view.findViewById(R.id.tvArtists);
        btnLikeConcert = (Button) view.findViewById(R.id.btnLikeConcert);
        btnUnlikeConcert = (Button) view.findViewById(R.id.btnUnlikeConcert);
        setUpListeners();

        artists = concert.getArtistsString();
        String date = concert.getEventDate();

        ivHeader.setTag(concert);
        tvEvent.setText(concert.getEventName());
        tvDate.setText(date);
        if (concert.getVenue() != null) {
            tvVenue.setText(concert.getVenue());
        }
        tvArtists.setText(artists);
        Picasso.with(getContext()).load(concert.getBackdropImage()).into(ivHeader);

    }

    /** Sets up 'like' button and makes title appear in the AppBar when collapsed */
    public void setUpListeners(){

        btnLikeConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Concert concertToLike = concert;
                concertDetailsFragmentListener.onLikeConcert(concertToLike);
            }
        });

        btnUnlikeConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Concert concertToUnlike = concert;
                concertDetailsFragmentListener.onUnlikeConcert(concertToUnlike);
            }
        });

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(concert.getEventName());
                    isShow = true;
                    tvEvent.setVisibility(View.GONE);
                    tvArtists.setVisibility(View.GONE);
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                    tvEvent.setVisibility(View.VISIBLE);
                    tvArtists.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
