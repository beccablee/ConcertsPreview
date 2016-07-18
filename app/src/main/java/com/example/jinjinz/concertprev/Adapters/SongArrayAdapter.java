package com.example.jinjinz.concertprev.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by beccalee on 7/13/16.
 */
public class SongArrayAdapter extends ArrayAdapter<Parcelable> implements View.OnClickListener {

    private Context mContext;
    private Song song;
    TextView tvSongName;
    TextView tvSongArtist;
    OnSongClickListener mOnSongClickListener;

    public SongArrayAdapter(Context context, List<Parcelable> songs, OnSongClickListener songClickListener) {
        super(context, R.layout.item_song, songs);
        mContext = context;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        //context = convertView.getContext();
        song = Parcels.unwrap(getItem(position));
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song, parent, false);
        convertView.setOnClickListener(this);

        tvSongName = (TextView) convertView.findViewById(R.id.tvSongName);
        tvSongArtist = (TextView) convertView.findViewById(R.id.tvSongArtist);

        tvSongName.setText(song.getName());
        tvSongName.setTag(song);
        tvSongArtist.setText(song.getArtists().get(0));

        //mOnSongClickListener = (OnSongClickListener) this;

        return convertView;
    }
}
