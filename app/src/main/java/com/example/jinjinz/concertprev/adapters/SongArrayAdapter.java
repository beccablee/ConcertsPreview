package com.example.jinjinz.concertprev.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinjinz.concertprev.MainActivity;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;

public class SongArrayAdapter extends RecyclerView.Adapter<SongArrayAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<Parcelable> mSongs;
    private OnSongClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName;
        TextView tvSongArtist;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            tvSongArtist = (TextView) itemView.findViewById(R.id.tvSongArtist);
        }
    }

    public SongArrayAdapter(Context context, ArrayList<Parcelable> songs, OnSongClickListener songClickListener) {
        mSongs = songs;
        listener = songClickListener;
    }

    @Override
    public void onClick(View view) {
        TextView tvSongName = (TextView) view.findViewById(R.id.tvSongName);
        Song song = (Song) tvSongName.getTag();
        MainActivity.setWherePlayerLaunched(MainActivity.fromConcert);
        listener.onSongClicked(song);
    }

    /* Communicates between SongArrayAdapter and SongsFragment */
    public interface OnSongClickListener {
        void onSongClicked(Song song);
    }

    /* Inflates layout and returns new ViewHolder instance */
    @Override
    public SongArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View songView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        songView.setOnClickListener(this);

        ViewHolder viewHolder = new ViewHolder(songView);
        return viewHolder;
    }

    /* Populates data into each item through the ViewHolder */
    @Override
    public void onBindViewHolder(SongArrayAdapter.ViewHolder viewHolder, int position) {
        // Gets the data model based on position
        Song song = Parcels.unwrap(mSongs.get(position));

        // Sets the item views based on the data model
        TextView tvSongName = viewHolder.tvSongName;
        tvSongName.setText(song.getName());
        tvSongName.setTag(song);
        TextView tvSongArtist = viewHolder.tvSongArtist;
        tvSongArtist.setText(song.getArtistsString());
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }


}
