package com.example.jinjinz.concertprev.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerBarFragment extends Fragment {
    Song song;
    TextView songTitle2;
    TextView artistTitle2;
    Button playBtn2;
    PlayerBarFragmentListener listener;

    /**
     * stuff Activity should do: change interface when song changes, react when player tries to be opened, react play button
     */
    public interface PlayerBarFragmentListener {
        void openPlayer();
        void playPauseBarBtn();
    }
    public PlayerBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlayerBarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerBarFragment newInstance(Song song) {
        PlayerBarFragment fragment = new PlayerBarFragment();
        Bundle args = new Bundle();
        args.putParcelable("song", Parcels.wrap(song));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (PlayerBarFragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            song = Parcels.unwrap(getArguments().getParcelable("song"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player_bar, container, false);
        songTitle2 = (TextView) v.findViewById(R.id.songTitle2);
        artistTitle2 = (TextView) v.findViewById(R.id.artistTitle2);
        playBtn2 = (Button) v.findViewById(R.id.playBtn2);

        songTitle2.setText(song.getName());
        artistTitle2.setText(song.getArtists().get(0));
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
        v.setBackgroundColor(Color.parseColor("#404040"));
        return v;
    }

    //call by activity
    public void updatePlay(boolean isPlaying) {
        if (isPlaying) {
            playBtn2.setBackground(getContext().getDrawable(R.drawable.ic_pause_circle));
        }
        else {
            playBtn2.setBackground(getContext().getDrawable(R.drawable.ic_play_circle_));
        }
    }

    public  void updateInterface(Song song) {
        songTitle2.setText(song.getName());
        artistTitle2.setText(song.getArtists().get(0));
    }

}
