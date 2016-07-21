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
    SongsFragmentListener songsFragmentListener;

    /* Communicates between SongsFragment and Main Activity */
    public interface SongsFragmentListener {
        void setUpArtistSearch(SongsFragment fragment, Concert concert, int artistIndex, int songsPerArtist);
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

            int numberOfArtists = concert.getArtists().size();
            int songsPerArtist = computeSongsPerArtist(numberOfArtists);

            for (int i = 0; i < numberOfArtists; i++){
                listener.setUpArtistSearch(this, concert, i, songsPerArtist);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
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
        if (llLoading.getVisibility() == View.VISIBLE && songs.size() != 0) {
            llLoading.setVisibility(View.GONE);
            rlRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /* Adds songs to the ArrayList and populates the RecyclerView */
    public void addSongs(ArrayList<Parcelable> songsArrayList) {
        if (getView().findViewById(R.id.llLoading) != null) {
            llLoading = (RelativeLayout) getView().findViewById(R.id.llLoading);
            llLoading.setVisibility(View.GONE);
        }
        rlRecyclerView = (RelativeLayout) getView().findViewById(R.id.rlRecyclerView);
        songs.addAll(songsArrayList);
        rlRecyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public void onSongClicked(Song song){
        listener.launchSongView(song, songs);
    }

    /* Returns the number of songs each artist will have in the playlist */
    public static int computeSongsPerArtist(int numberOfArtists){
        int songsPerPlaylist = 70;
        int songsPerArtist;
        if (numberOfArtists > 70){
            songsPerArtist = 1;
        } else if (numberOfArtists > 8){
            songsPerArtist = songsPerPlaylist / numberOfArtists;
        } else if (numberOfArtists > 4){
            songsPerArtist = 7;
        } else {
            songsPerArtist = 8;
        }
        return songsPerArtist;
    }

}

