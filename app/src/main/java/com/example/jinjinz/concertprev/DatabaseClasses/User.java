package com.example.jinjinz.concertprev.DatabaseClasses;

import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import java.util.ArrayList;

/**
 * Created by noradiegwu on 7/15/16.
 */
public class User { // from what I'm reading SQLite can only hold "TEXT (similar to String in Java),
    // INTEGER (similar to long in Java) and REAL (similar to double in Java)" no other objs
    // so instead of passing in a User, we will need to pass in identifying data of that user
    // and use that to populate our views and id our user and their preferences

    // favorited songs list
    ArrayList<Song> faveSongs;
    // saved concerts list
    ArrayList<Concert> savedConcerts;
    // tickets
    // maybe a seperate obj that contains the scannable barcode/qrcode and the concert info

    public ArrayList<Song> getFaveSongs() {
        return faveSongs;
    }

    public ArrayList<Concert> getSavedConcerts() {
        return savedConcerts;
    }


}
