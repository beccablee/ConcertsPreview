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

import com.example.jinjinz.concertprev.adapters.SongArrayAdapter;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SongsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment implements SongArrayAdapter.OnSongClickListener{

    private ArrayList<Parcelable> songs;
    private Concert concert;

    public static SongArrayAdapter adapter;
    RecyclerView rvSongs;
    RelativeLayout llLoading;
    RelativeLayout rlRecyclerView;

    public interface SongsFragmentListener {
        void setUpArtistSearch(SongsFragment fragment, Concert concert, int artistIndex, int songsPerArtist);
        void launchSongView(Song song, ArrayList<Parcelable> songs);
    }

    SongsFragmentListener songsFragmentListener;

    public SongsFragment() {
        // Required empty public constructor
    }

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
                songsFragmentListener.setUpArtistSearch(this, concert, i, songsPerArtist);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        rvSongs = (RecyclerView) view.findViewById(R.id.rvSongs);
        llLoading = (RelativeLayout) view.findViewById(R.id.llLoading);
        rlRecyclerView = (RelativeLayout) view.findViewById(R.id.rlRecyclerView);

        rvSongs.setAdapter(adapter);
        rvSongs.setLayoutManager(new LinearLayoutManager(getActivity()));

        rlRecyclerView.setVisibility(View.GONE);

        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            songsFragmentListener = (SongsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public void addSongs(ArrayList<Parcelable> songsArrayList) {
        llLoading = (RelativeLayout) getView().findViewById(R.id.llLoading);
        rlRecyclerView = (RelativeLayout) getView().findViewById(R.id.rlRecyclerView);

        songs.addAll(songsArrayList);
        llLoading.setVisibility(View.GONE);
        rlRecyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public void onSongClicked(Song song){
        songsFragmentListener.launchSongView(song, songs);
    }

    public int computeSongsPerArtist(int numberOfArtists){
        int songsPerPlaylist = 70;
        int songsPerArtist;
        if (numberOfArtists > 70){
            songsPerArtist = 1;
        }
        else if (numberOfArtists > 8){
            songsPerArtist = songsPerPlaylist / numberOfArtists;
        }
        else if (numberOfArtists > 4){
            songsPerArtist = 7;
        }
        else {
            songsPerArtist = 8;
        }
        return songsPerArtist;
    }

}

