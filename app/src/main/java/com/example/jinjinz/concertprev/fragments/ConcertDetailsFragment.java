package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    private String artists;
    private String date;

    private AppBarLayout appBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView ivHeader;
    private TextView tvEvent;
    private TextView tvDate;
    private TextView tvVenue;
    private TextView tvArtists;
    private Button btnLikeConcert;
    private Button btnPurchaseTickets;
    private WebView webView;

    ConcertDetailsFragmentListener concertDetailsFragmentListener;
    SongsFragment songsFragment;

    /** Required empty public constructor */
    public ConcertDetailsFragment() {
    }

    /** Communicates between ConcertDetailsFragment and MainActivity */
    public interface ConcertDetailsFragmentListener {
        Concert likeConcert(Concert concert);
    }

    /** Creates a new instance of the ConcertDetailsFragment and gets concert Object (Parcelable) */
    public static ConcertDetailsFragment newInstance(Parcelable concert) {
        ConcertDetailsFragment fragment = new ConcertDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("concert", concert);
        fragment.setArguments(args);
        return fragment;
    }

    /** Creates a SongsFragment nested in the ConcertDetailsFragment */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        concert = Parcels.unwrap(getArguments().getParcelable("concert"));
        if (savedInstanceState == null) {
            songsFragment = SongsFragment.newInstance(Parcels.wrap(concert));
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.songContainer, songsFragment).commit();

        }
    }

    /** Inflate the layout and call setUpViews */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concert_details, container, false);
        setUpViews(view);

        return view;
    }

    /** Gets listener from context */
    public void onAttach(Context context) {
        super.onAttach(context);
        concertDetailsFragmentListener = (ConcertDetailsFragmentListener) context;
    }

    /** Finds and populates views for the fragment */
    public void setUpViews(View view){
        appBar = (AppBarLayout) view.findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        ivHeader = (ImageView) view.findViewById(R.id.ivHeader);
        tvEvent = (TextView) view.findViewById(R.id.tvEvent);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvVenue = (TextView) view.findViewById(R.id.tvVenue);
        tvArtists = (TextView) view.findViewById(R.id.tvArtists);
        btnLikeConcert = (Button) view.findViewById(R.id.btnLikeConcert);
        btnPurchaseTickets = (Button) view.findViewById(R.id.btnPurchaseTickets);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.setVisibility(View.GONE);
        if(concert.getDbId() == -1L) { // not in db
            btnLikeConcert.setBackgroundResource(R.drawable.ic_unstar);
        } else {
            btnLikeConcert.setBackgroundResource(R.drawable.ic_star);
        }
        setUpListeners();

        artists = concert.getArtistsString();
        date = concert.getEventDate();

        ivHeader.setTag(concert);
        tvEvent.setText(concert.getEventName());
        tvDate.setText(date);
        if (concert.getVenue() != null) {
            tvVenue.setText(concert.getVenue());
        }
        tvArtists.setText(artists);
        Picasso.with(getContext()).load(concert.getBackdropImage()).into(ivHeader);

    }

    /** Sets up and opens WebView for ticket purchasing */
    public void openWebView(){
        if (webView != null) {
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webView.loadUrl(concert.getEventUrl());
            webView.setVisibility(View.VISIBLE);
        }
    }

    /** Sets up button and AppBar listeners for details view*/
    public void setUpListeners(){
        // Allows user to 'like' a concert
        btnLikeConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tapped));
                Concert likedConcert = concertDetailsFragmentListener.likeConcert(concert);
                if(likedConcert.getDbId() == -1L) {
                    btnLikeConcert.setBackgroundResource(R.drawable.ic_unstar);
                } else {
                    btnLikeConcert.setBackgroundResource(R.drawable.ic_star);
                }

            }
        });
        // Takes user to WebView of Ticketmaster event details
        btnPurchaseTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView();
            }
        });
        // Makes title appear in the AppBar when collapsed
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
        // Sets up back button functionality for WebView when launched
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && webView.getVisibility() == View.VISIBLE){
                    switch(i) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            } else {
                                webView.setVisibility(View.GONE);
                            }
                            return true;
                    }
                }
                return false;
            }
        });
    }

}
