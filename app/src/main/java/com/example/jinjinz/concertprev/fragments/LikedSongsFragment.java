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
import android.widget.TextView;

import com.example.jinjinz.concertprev.MainActivity;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.adapters.LikedSongArrayAdapter;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;


public class LikedSongsFragment extends Fragment implements LikedSongArrayAdapter.OnSongClickListener{

    private ArrayList<Song> mySongs;
    private LikedSongArrayAdapter mLikedSongArrayAdapter;
    private LikedSongsFragmentListener mLikedSongsFragmentListener;

    /**
     *  Communicates between LikedSongsFragment and Main Activity
     *  */
    public interface LikedSongsFragmentListener {
        void launchSongPlayer(Song song, ArrayList<Parcelable> songs, Concert concert);
    }

    /**
     * Required empty public constructor
     * */
    public LikedSongsFragment() {
    }

    public static LikedSongsFragment newInstance() {
        LikedSongsFragment fragment = new LikedSongsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySongs = new ArrayList<>();
        mLikedSongArrayAdapter = new LikedSongArrayAdapter(getActivity(), mySongs, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked_songs, container, false);
        RecyclerView rvMySongs = (RecyclerView) view.findViewById(R.id.rvLikedSongs);
        TextView tvNoSongs = (TextView) view.findViewById(R.id.tvNoSongs);
        mySongs.clear();
        ArrayList<Song> songs = MainActivity.getLikedSongs();
        mySongs.addAll(songs);
        mLikedSongArrayAdapter.notifyDataSetChanged();
        if(mySongs.size() == 0) {
            rvMySongs.setVisibility(View.GONE);
            tvNoSongs.setVisibility(View.VISIBLE);
        } else {
            rvMySongs.setVisibility(View.VISIBLE);
            tvNoSongs.setVisibility(View.GONE);
        }
        rvMySongs.setAdapter(mLikedSongArrayAdapter);
        rvMySongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    /**
     * Gets mLikedSongsFragmentListener from context
     * */
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLikedSongsFragmentListener = (LikedSongsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LikedSongsFragmentListener");
        }
    }


    /**
     * Launches the player starting with the selected song
     * @param song the song to be played
     * */
    public void onSongClicked(Song song){
        mLikedSongsFragmentListener.launchSongPlayer(song, songsToParcelable(mySongs), null);
    }

    /**
     * Turns an arraylist of Song objects into an arraylist of Parcelable objects for the media player's use
     * @param songs array list of Song objects
     * @return array list of Parcelabel objects
     */
    private ArrayList<Parcelable> songsToParcelable(ArrayList<Song> songs) {
        ArrayList<Parcelable> parcelables = new ArrayList<>();
        for(int i = 0; i < songs.size(); i++) {
            parcelables.add(i, Parcels.wrap(songs.get(i)));
        }
        return parcelables;
    }


}

