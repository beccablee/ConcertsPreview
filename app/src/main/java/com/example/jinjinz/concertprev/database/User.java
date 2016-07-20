package com.example.jinjinz.concertprev.database;

import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Song;

import java.util.ArrayList;

public class User { // this model will be built up from the db on load and used to populate the user profile

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
