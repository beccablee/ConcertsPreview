package com.example.jinjinz.concertprev;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service {
    MediaPlayer mMediaPlayer;
    Concert mConcert;
    ArrayList<Song> mSongs;
    int iCurrentSongIndex;
    private IBinder mBinder = new LocalBinder();

    //TODO: stopForground when application is exited
    //TODO: implement database to update ui
    /**
     * Initialize MediaPlayer
     */
    public MediaPlayerService() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mSongs = new ArrayList<>();

        //on prepared listener --> what happens when it is ready to play
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });

        //switch between songs on completion
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaPlayer.reset();
                iCurrentSongIndex++;
                if(mSongs != null && iCurrentSongIndex == mSongs.size()) {
                    iCurrentSongIndex = 0;
                }
                try {
                    mMediaPlayer.setDataSource(mSongs.get(iCurrentSongIndex).getPreviewUrl());
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("music player", "unknown error");

                }
            }
        });

        //error check
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Start new concert
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mConcert = Parcels.unwrap(intent.getParcelableExtra("concert"));
        ArrayList<Parcelable> parcelables = intent.getParcelableArrayListExtra("songs");
        for (int i = 0; i < parcelables.size(); i++) {
            mSongs.add(i, (Song) Parcels.unwrap(parcelables.get(i)));
        }
        iCurrentSongIndex = 0;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        //start with first song
        try {
            mMediaPlayer.setDataSource(mSongs.get(iCurrentSongIndex).getPreviewUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("music player", "unknown error");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * play/pause song
     */
    public void playPauseSong() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        else {
            mMediaPlayer.start();
        }
    }

    public void skipNext() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        try {
            iCurrentSongIndex++;
            if(iCurrentSongIndex == mSongs.size()) {
                iCurrentSongIndex = 0;
            }
            mMediaPlayer.setDataSource(mSongs.get(iCurrentSongIndex).getPreviewUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("music player", "unknown error: skipNext");

        }
    }

    public void skipPrev() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        if (iCurrentSongIndex >= 1) {
            iCurrentSongIndex = iCurrentSongIndex - 1;
        }
        else if (iCurrentSongIndex == 0) {
            iCurrentSongIndex = mSongs.size() - 1;
        }
        try {
            mMediaPlayer.setDataSource(mSongs.get(iCurrentSongIndex).getPreviewUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("music player", "unknown error: skipPrev");
        }
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}
