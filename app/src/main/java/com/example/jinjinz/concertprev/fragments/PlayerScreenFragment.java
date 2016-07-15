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

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerScreenFragment extends Fragment {
    ImageView albumImg; //done -sh
    TextView concertTitle; // TODO: make it clickable
    Button backBtn; //TODO: take out
    TextView songTitle; //done -sh
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
        void playPauseSong(PlayerScreenFragment fragment); //on play button click
        void skipNext(); //on skip next click
        void skipPrev(); //on skip previous click
    }
    public static PlayerScreenFragment newInstance(Song song) {
        PlayerScreenFragment fragment = new PlayerScreenFragment();
        Bundle args = new Bundle();
        args.putSerializable("song", song);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialSong = getArguments().getParcelable("song");
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
        View v = inflater.inflate(R.layout.activity_player, container, false);
        //initialize values
        albumImg = (ImageView) v.findViewById(R.id.albumImg);
        concertTitle = (TextView) v.findViewById(R.id.concertTitle);
        songTitle = (TextView) v.findViewById(R.id.songTitle);
        playBtn = (Button) v.findViewById(R.id.playBtn);
        artistTitle = (TextView) v.findViewById(R.id.artistTitle);
        view = v.findViewById(R.id.background);
        prevBtn = (Button) v.findViewById(R.id.prevBtn);
        nextBtn = (Button) v.findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

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
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playPauseSong((PlayerScreenFragment) getParentFragment());
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
        return v;
    }
    //update interface
    public void updateInterface(Song song) {
        //Picasso.with(this).load(song.getAlbumArtUrl()).fit().into(albumImg);
        songTitle.setText(song.getName());
        artistTitle.setText(song.getArtists().get(0));
        // Define a listener for image loading
        Target target = new Target() {
            // Fires when Picasso finishes loading the bitmap for the target
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                albumImg.setImageBitmap(bitmap);
                Palette backgrd = Palette.from(bitmap).generate();
                Palette.Swatch swatch = backgrd.getDarkMutedSwatch();
                if (swatch != null) {
                    view.setBackgroundColor(swatch.getRgb());
                }
                else {
                    swatch = backgrd.getDarkVibrantSwatch();
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
    }
    //call by activity
    public void updatePlay(boolean isPlaying) {
        if (isPlaying) {
            playBtn.setBackground(getContext().getDrawable(R.drawable.ic_play_circle_));
        }
        else {
            playBtn.setBackground(getContext().getDrawable(R.drawable.ic_pause_circle));
        }
    }

    //call by activity
    public void setProgressBar(int time) {
        progressBar.setProgress(time);
    }
    //move time to activity
    //method for changing visuals called by activity
    /**public void updateProgressBar() {
        //attempt at progressbar
        new Thread(new Runnable() {
            public void run() {
                int currentPosition = 0;
                while (true) {
                    try {
                        Thread.sleep(100);
                        currentPosition = listener.checkProgress();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                    progressBar.setProgress(currentPosition);
                }
            }
        }).start();
    }*/
}
