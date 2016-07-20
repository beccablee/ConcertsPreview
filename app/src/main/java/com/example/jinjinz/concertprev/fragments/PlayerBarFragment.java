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
 */
public class PlayerBarFragment extends Fragment {
    private TextView songTitle2;
    private TextView artistTitle2;
    private Button playBtn2;
    private PlayerBarFragmentListener listener;

    public interface PlayerBarFragmentListener {
        void openPlayer();
        void playPauseBarBtn();
        void onOpenBar();
    }
    public PlayerBarFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment PlayerBarFragment.
     */
    public static PlayerBarFragment newInstance() {
        PlayerBarFragment fragment = new PlayerBarFragment();
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        listener.onOpenBar();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (PlayerBarFragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player_bar, container, false);
        songTitle2 = (TextView) v.findViewById(R.id.songTitle2);
        artistTitle2 = (TextView) v.findViewById(R.id.artistTitle2);
        playBtn2 = (Button) v.findViewById(R.id.playBtn2);
        listener.onOpenBar();
        playBtn2.setOnClickListener(new View.OnClickListener() {
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

    //set play button visuals
    public void updatePlay(boolean isPlaying) {
        if (isPlaying) {
            playBtn2.setBackground(getContext().getDrawable(R.drawable.ic_pause_circle));
        }
        else {
            playBtn2.setBackground(getContext().getDrawable(R.drawable.ic_play_circle));
        }
    }

    public void updateInterface(Song song) {
        songTitle2.setText(song.getName());
        artistTitle2.setText(song.getArtists().get(0));
    }

}
