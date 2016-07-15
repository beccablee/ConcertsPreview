package com.example.jinjinz.concertprev;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jinjinz.concertprev.fragments.PlayerScreenFragment;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlayerScreenFragment.PlayerScreenFragmentListener {
    Concert concert;
    ArrayList<Song> songs;
    MediaPlayer mediaPlayer;
    private int songNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    // Player
    ////////////////////////////////////////////////////
    @Override
    public String getConcertName() {
        if (concert != null) {
            return concert.getEventName();
        }
        return null;
    }
    //go to concert fragment
    @Override
    public void onConcertClick() {
    }

    @Override
    public void playPauseSong(PlayerScreenFragment fragment) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            fragment.updatePlay(false);
        }
        else {
            mediaPlayer.start();
            fragment.updatePlay(true);
        }
    }

    @Override
    public void skipNext() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            if(songNum == songs.size()) {
                songNum = 0;
            }
            mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void skipPrev() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        if (songNum >= 1) {
            songNum = songNum - 1;
        }
        else if (songNum == 0) {
            songNum = songs.size() - 1;
        }
        try {
            mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //We want everything to be happening with the media player here
    ////////////////////////////////////////////////////

    // Search
    ////////////////////////////////////////////////////



    ////////////////////////////////////////////////////

    // Concert + Songs
    ////////////////////////////////////////////////////


    ////////////////////////////////////////////////////


}
