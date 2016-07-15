package com.example.jinjinz.concertprev;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerActivity extends AppCompatActivity {
    Concert concert;
    ArrayList<Song> songs;
    ImageView albumImg; //done -sh
    TextView concertTitle; //done
    Button backBtn; //done
    TextView songTitle; //done -sh
    TextView artistTitle;
    Button playBtn;
    MediaPlayer mediaPlayer;
    private int songNum;
    private boolean play;
    View view;
    Button prevBtn;
    Button nextBtn;
    ProgressBar progressBar;
    int total = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Optional
        //have progress bar on button
        //Change media player into fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //create dummy songs and concerts
        Concert dummy_c = new Concert();
        Song dummy_ss = new Song();
        dummy_c.setEventName("TESTING");
        dummy_ss.setAlbumArtUrl("https://i.scdn.co/image/6324fe377dcedf110025527873dafc9b7ee0bb34");
        ArrayList<String> artist = new ArrayList<>();
        artist.add("Elvis Presley");
        dummy_ss.setArtists(artist);
        dummy_ss.setName("Suspicious Minds");
        dummy_ss.setPreviewUrl("https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1");
        ArrayList<Song> dummy_s = new ArrayList<>();
        dummy_s.add(dummy_ss);

        //initialize values
        concert = dummy_c;
        songs = dummy_s;
        Collections.shuffle(songs);
        albumImg = (ImageView) findViewById(R.id.albumImg);
        concertTitle = (TextView) findViewById(R.id.concertTitle);
        backBtn = (Button) findViewById(R.id.backBtn);
        songTitle = (TextView) findViewById(R.id.songTitle);
        playBtn = (Button) findViewById(R.id.playBtn);
        songNum = 0;
        play = true;
        artistTitle = (TextView) findViewById(R.id.artistTitle);
        //view = findViewById(R.id.background);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //set back button listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlayerActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });

        //set concert title

        concertTitle.setText(concert.getEventName());
        //tell rebecca about this feature
        /**concertTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlayerActivity.this, ConcertActivity.class);
                i.putExtra("concert", concert);
                startActivity(i);
            }
        }); */
        //initialize media player
        updateInterface(songs.get(songNum));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //on prepared listener --> what happens when it is ready to play
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                updateInterface(songs.get(songNum));
                songNum++;
                updateProgressBar();
                mediaPlayer.start();
            }
        });
        //switch between songs
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                if(songNum == songs.size()) {
                    songNum = 0;
                }
                try {
                    mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(PlayerActivity.this, "No music playing", Toast.LENGTH_SHORT);
                return false;
            }
        });

        //start with first song
        try {
            mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play) {
                    if (mediaPlayer.isPlaying()) {
                        play = false;
                        mediaPlayer.pause();
                        //playBtn.setText("Play");
                        playBtn.setBackground(getDrawable(R.drawable.ic_play_circle_));
                    }
                    else {
                        Toast.makeText(PlayerActivity.this, "No songs playing", Toast.LENGTH_SHORT);
                    }
                }
                else {
                    mediaPlayer.start();
                    play = true;
                    playBtn.setBackground(getDrawable(R.drawable.ic_pause_circle));
                }
            }
        });

        //previous skip
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                if (songNum >= 2) {
                    songNum = songNum - 2;
                }
                else if (songNum == 1) {
                    songNum = songs.size() - 1;
                }
                try {
                    mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //forward skip
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                progressBar.setProgress(0);
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
        });
    }

    private void updateProgressBar() {
        //attempt at progressbar
        new Thread(new Runnable() {
            public void run() {
                int currentPosition = 0;
                progressBar.setMax(total);
                while (mediaPlayer != null && currentPosition < total) {
                    try {
                        Thread.sleep(100);
                        currentPosition = mediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                    progressBar.setProgress(currentPosition);
                }
            }
        }).start();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        //create dummy songs and concerts
        Concert dummy_c = new Concert();
        Song dummy_ss = new Song();
        dummy_c.setEventName("TESTING");
        dummy_ss.setAlbumArtUrl("https://i.scdn.co/image/6324fe377dcedf110025527873dafc9b7ee0bb34");
        ArrayList<String> artist = new ArrayList<>();
        artist.add("Elvis Presley");
        dummy_ss.setArtists(artist);
        dummy_ss.setName("Suspicious Minds");
        dummy_ss.setPreviewUrl("https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1");
        ArrayList<Song> dummy_s = new ArrayList<>();
        dummy_s.add(dummy_ss);
        if (concert == null) {
            onCreate(null);
        }
        else if(!concert.getEventName().equals(dummy_c.getEventName())) {
            //initialize
            songNum = 0;
            play = true;
            concert = dummy_c;
            songs = dummy_s;
            mediaPlayer.stop();
            mediaPlayer.reset();
            //start with first song
            try {
                mediaPlayer.setDataSource(songs.get(songNum).getPreviewUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //update interface
    private void updateInterface(Song song) {
        //Picasso.with(this).load(song.getAlbumArtUrl()).fit().into(albumImg);
        songTitle.setText(song.getName());
        artistTitle.setText(song.getArtists().get(0));


        // Define a listener for image loading
        Target target = new Target() {
            // Fires when Picasso finishes loading the bitmap for the target
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                albumImg.setImageBitmap(bitmap);
                Palette backgrd = Palette.from(bitmap).generate();
                Palette.Swatch swatch = backgrd.getDarkMutedSwatch();
                if (swatch != null) {
                    //view.setBackgroundColor(swatch.getRgb());
                }
                else {
                    swatch = backgrd.getDarkVibrantSwatch();
                    if (swatch != null) {
                        //view.setBackgroundColor(swatch.getRgb());
                    }
                    else {
                        //view.setBackgroundColor(Color.parseColor("#404040"));
                    }
                }
                // Set the result as the background color for `R.id.vPalette` view containing the contact's name.
            }

            // Fires if bitmap fails to load
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        // TODO: Clear the bitmap and the background color in adapter
        albumImg.setBackgroundResource(0);
        view.setBackgroundResource(0);
        // Store the target into the tag for the profile to ensure target isn't garbage collected prematurely
        albumImg.setTag(target);
        // Instruct Picasso to load the bitmap into the target defined above
        Picasso.with(this).load(song.getAlbumArtUrl()).into(target);

    }

}
