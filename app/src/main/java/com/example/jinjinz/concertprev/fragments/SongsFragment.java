package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.adapters.SongArrayAdapter;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;


public class SongsFragment extends Fragment implements SongArrayAdapter.OnSongClickListener{

    private ArrayList<Parcelable> songs;
    private Concert concert;

    private RecyclerView rvSongs;
    private RelativeLayout llLoading;
    private RelativeLayout rlRecyclerView;
    SongsFragmentListener listener;

    public static SongArrayAdapter adapter;

    /* Communicates between SongsFragment and Main Activity */
    public interface SongsFragmentListener {
        void setUpArtistSearch(SongsFragment fragment, Concert concert, int artistIndex, int songsPerArtist, ArrayList<String> artists);
        void launchSongView(Song song, ArrayList<Parcelable> songs);
    }

    /* Required empty public constructor */
    public SongsFragment() {
    }

    /* Creates a new instance of the SongsFragment and gets concert Object (Parcelable) */
    public static SongsFragment newInstance(Parcelable concert) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putParcelable("concert", concert);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songs = new ArrayList<>();
        if (getArguments() != null) {
            concert = Parcels.unwrap(getArguments().getParcelable("concert"));
            adapter = new SongArrayAdapter(getActivity(), songs, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        int numberOfArtists = concert.getArtists().size();
        int songsPerArtist = computeSongsPerArtist(numberOfArtists);

        listener.setUpArtistSearch(this, concert, 0, songsPerArtist, concert.getArtists());

        rvSongs = (RecyclerView) view.findViewById(R.id.rvSongs);
        llLoading = (RelativeLayout) view.findViewById(R.id.llLoading);
        rlRecyclerView = (RelativeLayout) view.findViewById(R.id.rlRecyclerView);
        rlRecyclerView.setVisibility(View.GONE);

        rvSongs.setAdapter(adapter);
        rvSongs.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SongsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    /* Removes loading symbol and displays populated RecyclerView when returning to the fragment */
    @Override
    public void onResume() {
        super.onResume();
        llLoading = (RelativeLayout) getView().findViewById(R.id.llLoading);
        rlRecyclerView = (RelativeLayout) getView().findViewById(R.id.rlRecyclerView);
        if (songs != null && songs.size() != 0) {
            llLoading.setVisibility(View.GONE);
            rlRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /* Adds songs to the ArrayList and populates the RecyclerView after moer than 5 songs load*/
    public void addSongs(ArrayList<Parcelable> songsArrayList) {
        if (songs.size() > 5 || concert.getArtists().size() < 3) {
            try {
                llLoading = (RelativeLayout) getView().findViewById(R.id.llLoading);
                llLoading.setVisibility(View.GONE);
                rlRecyclerView = (RelativeLayout) getView().findViewById(R.id.rlRecyclerView);
                rlRecyclerView.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                Log.i("no loading screen", "0");
            }
        }
        songs.addAll(songsArrayList);
        adapter.notifyDataSetChanged();
    }

    public void onSongClicked(Song song){
        listener.launchSongView(song, songs);
    }

    /* Returns the number of songs each artist will have in the playlist */
    public static int computeSongsPerArtist(int numberOfArtists){
        int maxSongsPerPlaylist = 80;
        int songsPerArtist;
        if (numberOfArtists > 40){
            songsPerArtist = 2;
        } else if (numberOfArtists > 4){
            songsPerArtist = maxSongsPerPlaylist / numberOfArtists;
        } else {
            songsPerArtist = 8;
        }
        return songsPerArtist;
    }

}

