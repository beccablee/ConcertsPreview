package com.example.jinjinz.concertprev.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jinjinz.concertprev.MainActivity;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.databases.UserDataSource;
import com.example.jinjinz.concertprev.models.Song;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 * A fragment which allows for control of a media player
 */
public class PlayerScreenFragment extends Fragment {
    //UI elements
    private ImageView mAlbumImg;
    private TextView mConcertTitle;

    private TextView mTvSongTitle;
    private TextView mTvArtistTitle;
    private Button mBtnPlay;
    private View view;
    private Button mBtnBack;
    private Button mBtnPrev;
    private Button mBtnNext;
    private Button mBtnLike;
    private ProgressBar mProgressBar;
    Boolean play;
    //private Song currentSong; //TODO: NEED TO IMPLEMENT THIS
    private UserDataSource mUserDataSource;
    //total time of song
    private final int TOTAL = 30000;

    //listener
    private PlayerScreenFragmentListener listener;

    /**
     * Interface to be implemented by parent activity
     */
    public interface PlayerScreenFragmentListener {
        String getConcertName(); //get concert name
        void onConcertClick(); //on concert name click
        void playPauseSong(); //on play button click
        void skipNext(); //on skip next click
        void skipPrev(); //on skip previous click
        void onClosePlayer(); //on Player close (add playbar)
        void setUI(); //set UI
        void onPlayerOpen();
        void backInStack(); //go back
        void likeSong(); // like the song
    }

    /**
     * @return A new instance of fragment PlayerBarFragment.
     */
    public static PlayerScreenFragment newInstance() {
        PlayerScreenFragment fragment = new PlayerScreenFragment();
        return fragment;
    }

    /**
     * Empty constructor
     * Should never be called
     */
    public PlayerScreenFragment() {
    }

    /**
     * Update interface on start
     * Used whenever this fragment is opened
     * Overrides onStart()
     */
    @Override
    public void onStart() {
        listener.onPlayerOpen();
        super.onStart();
    }

    /**
     * Get listener from context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PlayerScreenFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PlayerScreenFragmentListener");
        }
    }

    /**
     * Set UI variables and onClick listeners
     * Overrides onCreateView
     * @param inflater same as super param
     * @param container same as super param
     * @param savedInstanceState same as super param
     * @return current view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_player, container, false);
        mUserDataSource = MainActivity.userDataSource;

        //Initialize values
        mAlbumImg = (ImageView) view.findViewById(R.id.albumImg);
        mConcertTitle = (TextView) view.findViewById(R.id.concertTitle);
        mTvSongTitle = (TextView) view.findViewById(R.id.songTitle);
        mBtnLike = (Button) view.findViewById(R.id.btnLikeSong);
        mBtnPlay = (Button) view.findViewById(R.id.playBtn);
        mTvArtistTitle = (TextView) view.findViewById(R.id.artistTitle);
        mBtnPrev = (Button) view.findViewById(R.id.prevBtn);
        mBtnNext = (Button) view.findViewById(R.id.nextBtn);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mBtnBack = (Button) view.findViewById(R.id.backBtn);
        mProgressBar.setMax(TOTAL);

        //set onClickListeners for buttons
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.backInStack();
            }
        });
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playPauseSong();
            }
        });
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.skipPrev();
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.skipNext();
            }
        });
        mBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tapped));
                listener.likeSong();
            }
        });
        mConcertTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConcertClick();
            }
        });
        return view;
    }

    /**
     * update the player's interface
     * @param song current song playing
     */
    public void updateInterface(Song song) {
        if (!mTvSongTitle.getText().equals(song.getName())) {
            //set text
            mConcertTitle.setText(listener.getConcertName());
            mTvSongTitle.setText(song.getName());
            mTvArtistTitle.setText(song.getArtists().get(0));

            // Define a listener for image loading
            Target target = new Target() {
                // Fires when Picasso finishes loading the bitmap for the target
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mAlbumImg.setImageBitmap(bitmap);
                    Palette backgrd = Palette.from(bitmap).generate();
                    Palette.Swatch swatch = backgrd.getDarkVibrantSwatch();
                    if (swatch != null) {
                        view.setBackgroundColor(swatch.getRgb());
                    } else {
                        swatch = backgrd.getDarkMutedSwatch();
                        if (swatch != null)
                            view.setBackgroundColor(swatch.getRgb());
                        else
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));
                    }
                }

                // Fires if bitmap fails to load
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            //clear images
            mAlbumImg.setBackgroundResource(0);
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));

            // Store the target into the tag for the profile to ensure target isn't garbage collected prematurely
            mAlbumImg.setTag(target);
            // Instruct Picasso to load the bitmap into the target defined above
            Picasso.with(getContext()).load(song.getAlbumArtUrl()).into(target);
        }
    }

    /**
     * Update the play button
     * @param isPlaying if MediaPlayer is playing
     */
    public void setPlayBtn(boolean isPlaying) {
        if (play == null || play != (Boolean) isPlaying) {
            if (isPlaying) {
                mBtnPlay.setBackground(getContext().getDrawable(R.drawable.ic_pause_circle));
            } else {
                mBtnPlay.setBackground(getContext().getDrawable(R.drawable.ic_play_circle));
            }
            play = (Boolean) isPlaying;
        }
    }

    /**
     * Update the progress bar
     * @param time time in milliseconds of player
     */
    public void setProgressBar(int time) {
        mProgressBar.setProgress(time);
    }

    /**
     * Update the like btn
     */
    public void setLikeBtn(Song song) {
        if (song.isLiked()) {
            mBtnLike.setBackgroundResource(R.drawable.ic_star);
        }

        else {
            mBtnLike.setBackgroundResource(R.drawable.ic_unstar);
        }
    }

    /**
     * Let activity perform action onPause() of fragment
     * calls onClosePlayer()
     */
    @Override
    public void onPause() {
        listener.onClosePlayer();
        super.onPause();
    }
}
