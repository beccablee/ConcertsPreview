package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jinjinz.concertprev.Adapters.SongArrayAdapter;
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
    // TODO: Setup interface and load songs into ListView

    private ArrayList<Parcelable> songs;
    private Concert concert;

    Button player;

    public ListView lvSongs;
    public SongArrayAdapter adapter;

    public interface SongsFragmentListener {
        void setUpArtistSearch(SongsFragment fragment, Concert concert, int artist_index);
        void launchSongView(Song song);

    }

    SongsFragmentListener songsFragmentListener;

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance(Parcelable concert) { //ArrayList<Parcelable> paramSongs,
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList("songs", paramSongs);
        args.putParcelable("concert", concert);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //songs = getArguments().getParcelableArrayList("songs"); // might be unnecessary
            songs = new ArrayList<>();
            concert = Parcels.unwrap(getArguments().getParcelable("concert"));
            adapter = new SongArrayAdapter(getActivity(), songs, this);
            //adapter.addAll(songs);
            //adapter.notifyDataSetChanged();
            //TO DO: get concert to pass through
        }
        for (int i = 0; i < concert.getArtists().size(); i++){
            songsFragmentListener.setUpArtistSearch(this, concert, i);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        lvSongs = (ListView) view.findViewById(R.id.lvSongs);
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Toast.makeText(getContext(), "Clicked" + pos, Toast.LENGTH_SHORT).show();
            }
        });

        lvSongs.setAdapter(adapter);

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
        adapter.notifyDataSetChanged();
        songs.addAll(songsArrayList);
        adapter.notifyDataSetChanged();
    }

    public void onSongClicked(Song song){
        songsFragmentListener.launchSongView(song);
    }

    /**  // TODO: Rename method, update argument and hook method into UI event
     public void onButtonPressed(Uri uri) {
     if (mListener != null) {
     mListener.onFragmentInteraction(uri);
     }
     }
     @Override
     public void onDetach() {
     super.onDetach();
     mListener = null;
     }*/

}

