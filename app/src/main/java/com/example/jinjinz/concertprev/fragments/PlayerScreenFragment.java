package com.example.jinjinz.concertprev.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Song;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerScreenFragment extends Fragment {
    ImageView albumImg;
    TextView concertTitle; // TODO: make it clickable
    Button backBtn;
    TextView songTitle;
    TextView artistTitle;
    Button playBtn;
    View view;
    Button prevBtn;
    Button nextBtn;
    ProgressBar progressBar;
    int total = 30000;
    PlayerScreenFragmentListener listener;
    Song initialSong;

    public interface PlayerScreenFragmentListener {
        String getConcertName(); //get concert name
        void onConcertClick(); //on concert name click
        void playPauseSong(); //on play button click
        void skipNext(); //on skip next click
        void skipPrev(); //on skip previous click
        void onClosePlayer(); //on Player close (add playbar)
        void onOpenPlayer(); //on Player open (change ui)
        void backInStack();
    }
    public static PlayerScreenFragment newInstance(Song song) {
        PlayerScreenFragment fragment = new PlayerScreenFragment();
        Bundle args = new Bundle();
        args.putParcelable("song", Parcels.wrap(song));
        fragment.initialSong = song;
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        listener.onOpenPlayer();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialSong = Parcels.unwrap(getArguments().getParcelable("song"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PlayerScreenFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PlayerScreenFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_player, container, false);
        //initialize values
        albumImg = (ImageView) view.findViewById(R.id.albumImg);
        concertTitle = (TextView) view.findViewById(R.id.concertTitle);
        songTitle = (TextView) view.findViewById(R.id.songTitle);
        playBtn = (Button) view.findViewById(R.id.playBtn);
        artistTitle = (TextView) view.findViewById(R.id.artistTitle);
        //view = v.findViewById(R.id.background);
        prevBtn = (Button) view.findViewById(R.id.prevBtn);
        nextBtn = (Button) view.findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        backBtn = (Button) view.findViewById(R.id.backBtn);

        concertTitle.setText(listener.getConcertName());
        concertTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConcertClick();
            }
        });
        //initialize interface
        updateInterface(initialSong);
        setProgressBar(0);
        //set onClickListeners for buttons
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.backInStack();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playPauseSong();
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.skipPrev();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.skipNext();
            }
        });
        progressBar.setMax(total);
        //progressbar
        return view;
    }
    //update interface
    public void updateInterface(Song song) {
        //Picasso.with(this).load(song.getAlbumArtUrl()).fit().into(albumImg);
        concertTitle.setText(listener.getConcertName());
        songTitle.setText(song.getName());
        artistTitle.setText(song.getArtists().get(0));
        // Define a listener for image loading
        Target target = new Target() {
            // Fires when Picasso finishes loading the bitmap for the target
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                albumImg.setImageBitmap(bitmap);
                Palette backgrd = Palette.from(bitmap).generate();
                Palette.Swatch swatch = backgrd.getDarkVibrantSwatch();
                if (swatch != null) {
                    view.setBackgroundColor(swatch.getRgb());
                }
                else {
                    swatch = backgrd.getDarkMutedSwatch();
                    if (swatch != null)
                        view.setBackgroundColor(swatch.getRgb());
                    else
                        view.setBackgroundColor(Color.parseColor("#404040"));
                }
                // Set the result as the background color for `R.id.vPalette` view containing the contact's name.
            }

            // Fires if bitmap fails to load
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                view.setBackgroundColor(Color.parseColor("#404040"));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        albumImg.setBackgroundResource(0);
        view.setBackgroundResource(0);
        // Store the target into the tag for the profile to ensure target isn't garbage collected prematurely
        albumImg.setTag(target);
        // Instruct Picasso to load the bitmap into the target defined above
        Picasso.with(getContext()).load(song.getAlbumArtUrl()).into(target);
        //view.setBackgroundColor(Color.parseColor("#404040"));
    }
    //call by activity
    public void updatePlay(boolean isPlaying) {
        if (isPlaying) {
            playBtn.setBackground(getContext().getDrawable(R.drawable.ic_pause_circle));
        }
        else {
            playBtn.setBackground(getContext().getDrawable(R.drawable.ic_play_circle_));
        }
    }

    //call by activity
    public void setProgressBar(int time) {
        progressBar.setProgress(time);
    }

    @Override
    public void onPause() {
        listener.onClosePlayer();
        super.onPause();
    }
}
