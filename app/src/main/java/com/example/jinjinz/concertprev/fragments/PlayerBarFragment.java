package com.example.jinjinz.concertprev.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Song;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 * The bottom playerbar of a music player
 */
public class PlayerBarFragment extends Fragment {
    //UI variables
    private TextView tvSongTitle;
    private TextView tvArtistTitle;
    private Button btnPlay;

    //listener
    private PlayerBarFragmentListener listener;
    /**
     * Interface to be implemented by activities which contains this fragment
     */
    public interface PlayerBarFragmentListener {
        void openPlayer();
        void playPauseBarBtn();
        void onOpenBar();
    }

    /**
     * Empty constructor
     * Should never be called, use newInstance() instead
     */
    public PlayerBarFragment() {
    }

    /**
     * @return A new instance of fragment PlayerBarFragment.
     */
    public static PlayerBarFragment newInstance() {
        PlayerBarFragment fragment = new PlayerBarFragment();
        return fragment;
    }

    /**
     * Refreshes UI to current song on resume
     */
    @Override
    public void onResume() {
        super.onResume();
        listener.onOpenBar();
    }

    /**
     * Create listener onAttach
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PlayerBarFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PlayerBarFragmentListener");
        }
    }

    /**
     * Initialize UI variables and sets onClickListeners
     * Overrides onCreateView
     * @param inflater same as super param
     * @param container same as super param
     * @param savedInstanceState same as super param
     * @return view inflated in fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player_bar, container, false);
        tvSongTitle = (TextView) v.findViewById(R.id.songTitle2);
        tvArtistTitle = (TextView) v.findViewById(R.id.artistTitle2);
        btnPlay = (Button) v.findViewById(R.id.playBtn2);
        listener.onOpenBar();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playPauseBarBtn();
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openPlayer();
            }
        });
        v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));
        return v;
    }

    /**
     * update the play button
     * @param isPlaying whether the MediaPlayer is playing
     */
    public void updatePlay(boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setBackground(getContext().getDrawable(R.drawable.ic_pause_circle));
        }
        else {
            btnPlay.setBackground(getContext().getDrawable(R.drawable.ic_play_circle));
        }
    }

    public void updateInterface(Song song) {
        tvSongTitle.setText(song.getName());
        tvArtistTitle.setText(song.getArtists().get(0));
    }

}
