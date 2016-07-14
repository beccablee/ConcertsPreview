package com.example.jinjinz.concertprev.Adapters;

import android.content.Context;
import android.os.Parcelable;
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
public class SongArrayAdapter extends ArrayAdapter<Parcelable> {

    private Context context;
    private Song song;
    TextView tvSongName;
    TextView tvSongArtist;

    public SongArrayAdapter(Context context, List<Parcelable> songs) {
        super(context, R.layout.item_song, songs);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        context = convertView.getContext();
        song = Parcels.unwrap(getItem(position));

        tvSongName = (TextView) convertView.findViewById(R.id.tvSongName);
        tvSongArtist = (TextView) convertView.findViewById(R.id.tvSongArtist);

        tvSongName.setText(song.getName());
        tvSongArtist.setText("Drake");

        return convertView;
    }
}
