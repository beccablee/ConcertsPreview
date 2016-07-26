package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinjinz.concertprev.MainActivity;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.adapters.LikedSongArrayAdapter;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;


public class LikedSongsFragment extends Fragment implements LikedSongArrayAdapter.OnSongClickListener{

    private ArrayList<Song> mySongs;
    private LikedSongArrayAdapter mLikedSongArrayAdapter;
    private LikedSongsFragmentListener mLikedSongsFragmentListener;

    /* Communicates between SongsFragment and Main Activity */
    public interface LikedSongsFragmentListener {
        void launchSongPlayer(Song song, ArrayList<Parcelable> songs);
    }

    /** Required empty public constructor */
    public LikedSongsFragment() {
    }

    /** Creates a new instance of the SongsFragment and gets concert Object (Parcelable) */
    public static LikedSongsFragment newInstance() {
        LikedSongsFragment fragment = new LikedSongsFragment();
        return fragment;
    }

    /** Gets the concert data passed into the fragment */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySongs = new ArrayList<>();
        mLikedSongArrayAdapter = new LikedSongArrayAdapter(getActivity(), mySongs, this);
        ArrayList<Song> songs = MainActivity.getLikedSongs();
        mySongs.addAll(songs);
        mLikedSongArrayAdapter.notifyDataSetChanged();
    }

    /** Sets up views and listeners for the fragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked_songs, container, false);
        RecyclerView rvMySongs = (RecyclerView) view.findViewById(R.id.rvLikedSongs);
        rvMySongs.setAdapter(mLikedSongArrayAdapter);
        rvMySongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    /** Gets mLikedSongsFragmentListener from context */
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLikedSongsFragmentListener = (LikedSongsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LikedSongsFragmentListener");
        }
    }


    /** Launches the player starting with the selected song */
    public void onSongClicked(Song song){
        mLikedSongsFragmentListener.launchSongPlayer(song, songsToParcelable(mySongs));
    }

    private ArrayList<Parcelable> songsToParcelable(ArrayList<Song> songs) {
        ArrayList<Parcelable> parcelables = new ArrayList<>();
        for(int i = 0; i < songs.size(); i++) {
            parcelables.add(i, Parcels.wrap(songs.get(i)));
        }
        return parcelables;
    }


}

