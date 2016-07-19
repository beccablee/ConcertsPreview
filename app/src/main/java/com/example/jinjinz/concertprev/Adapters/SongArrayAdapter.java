package com.example.jinjinz.concertprev.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by beccalee on 7/13/16.
 */
public class SongArrayAdapter extends RecyclerView.Adapter<SongArrayAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<Parcelable> mSongs;
    OnSongClickListener mOnSongClickListener;

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
        mContext = context;
        mSongs = songs;
        mOnSongClickListener = songClickListener;
    }

    @Override
    public void onClick(View view) {
        TextView tvSongName = (TextView) view.findViewById(R.id.tvSongName);
        Song song = (Song) tvSongName.getTag();
        mOnSongClickListener.onSongClicked(song);
    }

    public interface OnSongClickListener {
        void onSongClicked(Song song);
    }

    @Override
    public SongArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflate the custom layout
        View songView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        songView.setOnClickListener(this);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(songView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SongArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Song song = Parcels.unwrap(mSongs.get(position));

        // Set item views based on your views and data model
        TextView tvSongName = viewHolder.tvSongName;
        tvSongName.setText(song.getName());
        tvSongName.setTag(song);

        TextView tvSongArtist = viewHolder.tvSongArtist;
        tvSongArtist.setText(artistsToString(song.getArtists()));

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public String artistsToString(ArrayList<String> artist_list) {
        String artistNames = "";
        for (int i = 0; i < artist_list.size(); i++){
            if (i == 0) {
                artistNames += artist_list.get(i);
            } else {
                artistNames += " & " + artist_list.get(i);
            }
        }
        return artistNames;
    }
}
