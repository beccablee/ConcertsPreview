package com.example.jinjinz.concertprev;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.example.jinjinz.concertprev.databases.MediaContract.*;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service {
    MediaPlayer mMediaPlayer;
    //Concert mConcert;
    //ArrayList<Song> mSongs;
    int iSize;
    int lastProgress = 0;
    private IBinder mBinder = new LocalBinder();
    String[] mSongProjection = {
            PlaylistTable._ID,
            PlaylistTable.COLUMN_SPOTIFY_ID,
            PlaylistTable.COLUMN_SONG_NAME,
            PlaylistTable.COLUMN_SONG_ARTIST,
            PlaylistTable.COLUMN_SONG_PREVIEW_URL,
            PlaylistTable.COLUMN_ALBUM_ART_URL
    };
    String[] mConcertProjection = {
            CurrentConcertTable._ID,
            CurrentConcertTable.COLUMN_CONCERT_NAME,
            CurrentConcertTable.COLUMN_CONCERT_CITY,
            CurrentConcertTable.COLUMN_CONCERT_STATE,
            CurrentConcertTable.COLUMN_CONCERT_COUNTRY,
            CurrentConcertTable.COLUMN_CONCERT_TIME,
            CurrentConcertTable.COLUMN_CONCERT_DATE,
            CurrentConcertTable.COLUMN_CONCERT_VENUE,
            CurrentConcertTable.COLUMN_CONCERT_ARTISTS,
            CurrentConcertTable.COLUMN_CONCERT_IMAGE_URL
    };

    String[] mCurrentProjection = {
            CurrentSongTable._ID,
            CurrentSongTable.COLUMN_CURRENT_SONG_ID,
            CurrentSongTable.COLUMN_IS_PLAYING,
            CurrentSongTable.COLUNM_CURRENT_PROGRESS
    };
    Cursor mCursor;
    //TODO: stopForground when application is exited
    //TODO: implement database to update ui
    /**
     *
     */
    public MediaPlayerService() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //on prepared listener --> what happens when it is ready to play
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                getContentResolver().delete(CurrentSongTable.CONTENT_URI, null, null);
                ContentValues currentValues = new ContentValues();
                currentValues.put(CurrentSongTable.COLUMN_CURRENT_SONG_ID, mCursor.getInt(mCursor.getColumnIndex(PlaylistTable._ID)));
                currentValues.put(CurrentSongTable.COLUMN_IS_PLAYING, 1);
                currentValues.put(CurrentSongTable.COLUNM_CURRENT_PROGRESS, 0);
                getContentResolver().insert(CurrentSongTable.CONTENT_URI, currentValues);
                mMediaPlayer.start();
            }
        });

        //switch between songs on completion
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaPlayer.reset();
                if (mCursor.isLast()) {
                    mCursor.moveToFirst();
                }
                else {
                    mCursor.moveToNext();
                }
                try {
                    mMediaPlayer.setDataSource(mCursor.getString(mCursor.getColumnIndex(PlaylistTable.COLUMN_SONG_PREVIEW_URL)));
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
                Log.d("error", "media player");
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (mMediaPlayer.isPlaying()) {
                            int currentPosition = mMediaPlayer.getCurrentPosition();
                            if (currentPosition != lastProgress) {
                                ContentValues progressValue = new ContentValues();
                                progressValue.put(CurrentSongTable.COLUNM_CURRENT_PROGRESS, currentPosition);
                                getContentResolver().update(CurrentSongTable.CONTENT_URI, progressValue, null, null);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     *
     */

    Uri mConcertUri;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Update Concert
        getContentResolver().delete(CurrentConcertTable.CONTENT_URI, null, null);

        Concert concert = Parcels.unwrap(intent.getParcelableExtra("concert"));
        ContentValues concertValues = new ContentValues();
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_NAME, concert.getEventName());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_CITY, concert.getCity());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_STATE, concert.getStateCode());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_COUNTRY, concert.getCountryCode());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_TIME, concert.getEventTime());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_DATE, concert.getEventDate());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_VENUE, concert.getVenue());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_ARTISTS, concert.getArtistsString());
        concertValues.put(CurrentConcertTable.COLUMN_CONCERT_IMAGE_URL, concert.getBackdropImage());
        mConcertUri = getContentResolver().insert(CurrentConcertTable.CONTENT_URI,concertValues);
        //Update Songs
        getContentResolver().delete(PlaylistTable.CONTENT_URI, null, null);
        ArrayList<Parcelable> parcelables = intent.getParcelableArrayListExtra("songs");
        iSize = parcelables.size();
        for (int i = 0; i < iSize; i++) {
            Song song = Parcels.unwrap(parcelables.get(i));
            ContentValues songValues = new ContentValues();
            songValues.put(PlaylistTable.COLUMN_SPOTIFY_ID, song.getSpotifyID());
            songValues.put(PlaylistTable.COLUMN_SONG_NAME, song.getName());
            songValues.put(PlaylistTable.COLUMN_SONG_ARTIST, song.getArtists().get(0));
            songValues.put(PlaylistTable.COLUMN_SONG_PREVIEW_URL, song.getPreviewUrl());
            songValues.put(PlaylistTable.COLUMN_ALBUM_ART_URL, song.getAlbumArtUrl());
            getContentResolver().insert(PlaylistTable.CONTENT_URI, songValues);
        }
        //Initialize Cursor
        mCursor = getContentResolver().query(PlaylistTable.CONTENT_URI, mSongProjection, null, null, null);
        assert mCursor != null;
        mCursor.moveToFirst();
        //Initialize Media player
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();

        //set first song
        try {
            mMediaPlayer.setDataSource(mCursor.getString(mCursor.getColumnIndex(PlaylistTable.COLUMN_SONG_PREVIEW_URL)));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getContentResolver().delete(CurrentSongTable.CONTENT_URI, null, null);
        ContentValues currentValues = new ContentValues();
        Log.i("position", String.valueOf(mCursor.getPosition()));
        currentValues.put(CurrentSongTable.COLUMN_CURRENT_SONG_ID, mCursor.getInt(mCursor.getColumnIndex(PlaylistTable._ID)));
        currentValues.put(CurrentSongTable.COLUMN_IS_PLAYING, 0);
        currentValues.put(CurrentSongTable.COLUNM_CURRENT_PROGRESS, 0);
        getContentResolver().insert(CurrentSongTable.CONTENT_URI, currentValues);
        return super.onStartCommand(intent, flags, startId);
    }



    /**
     * play/pause song
     */
    public void playPauseSong() {
        ContentValues playValue = new ContentValues();
        if (mMediaPlayer.isPlaying()) {
            playValue.put(CurrentSongTable.COLUMN_IS_PLAYING, 0);
            mMediaPlayer.pause();
        }
        else {
            playValue.put(CurrentSongTable.COLUMN_IS_PLAYING, 1);
            mMediaPlayer.start();
        }
        getContentResolver().update(CurrentSongTable.CONTENT_URI, playValue, null, null);

    }

    public void skipNext() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        if (mCursor.isLast()) {
            mCursor.moveToFirst();
        }
        else {
            mCursor.moveToNext();
        }
        try {
            mMediaPlayer.setDataSource(mCursor.getString(mCursor.getColumnIndex(PlaylistTable.COLUMN_SONG_PREVIEW_URL)));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("music player", "unknown error");
        }
    }

    public void skipPrev() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        if (mCursor.isFirst()) {
            mCursor.moveToLast();
        }
        else {
            mCursor.moveToLast();
        }
        try {
            mMediaPlayer.setDataSource(mCursor.getString(mCursor.getColumnIndex(PlaylistTable.COLUMN_SONG_PREVIEW_URL)));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("music player", "unknown error");
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
