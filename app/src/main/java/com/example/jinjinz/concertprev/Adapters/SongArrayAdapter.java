package com.example.jinjinz.concertprev;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jinjinz.concertprev.models.Song;

import java.util.List;

/**
 * Created by beccalee on 7/13/16.
 */
public class SongArrayAdapter extends ArrayAdapter<Song> {

    private Context context;

    public SongArrayAdapter(Context context, List<Song> songs) {
        super(context, R.layout.item_song, songs);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        context = convertView.getContext();

        TextView tvSongName;
        TextView tvSongArtist;

        tvSongName = (TextView) convertView.findViewById(R.id.tvSongName);
        tvSongArtist = (TextView) convertView.findViewById(R.id.tvSongArtist);

        tvSongName.setText("One Dance");
        tvSongArtist.setText("Drake");

        return convertView;
    }
}
