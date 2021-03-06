/**
 * ConcertDetailsFragment
 *
 * Contains concert details including event name, venue, artists, image, and date
 * in an expandable AppBar
 * Displays playlist of songs for every artist in the concert
 */

package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

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
    private Button btnLeaveWebView;
    private Button btnShare;
    private WebView webView;
    private RelativeLayout webLayout;

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
        btnLeaveWebView = (Button) view.findViewById(R.id.btnLeaveWebView);
        btnShare = (Button) view.findViewById(R.id.btnShare);
        webView = (WebView) view.findViewById(R.id.webView);
        webLayout = (RelativeLayout) view.findViewById(R.id.webLayout);
        webLayout.setVisibility(View.GONE);
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
            webLayout.setVisibility(View.VISIBLE);
        }
    }

    /** Sets up button and AppBar listeners for details view */
    public void setUpListeners(){
        // Allows user to 'like' a concert
        btnLikeConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tapped));
                if (concert == null) {
                    Log.i("Check", "concert is null");
                }
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
                if (concert.getEventUrl() != null) {
                    openWebView();
                } else {
                    Toast.makeText(getContext(), "Concert web page not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Launches sharing content options for user and passes in link and concert information
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Going to " + concert.getEventName() + "/n" + "Check out: " + concert.getEventUrl();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, concert.getEventName());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
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
                                webLayout.setVisibility(View.GONE);
                            }
                            return true;
                    }
                }
                return false;
            }
        });
        // Allows user to exit WebView at any time
        btnLeaveWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webLayout.setVisibility(View.GONE);
            }
        });
    }

}
