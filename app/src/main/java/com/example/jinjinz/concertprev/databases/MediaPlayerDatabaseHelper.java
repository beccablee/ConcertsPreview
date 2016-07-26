package com.example.jinjinz.concertprev.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jinjinz on 7/25/16.
 */
public class MediaPlayerDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mediaInfo.db";

    public MediaPlayerDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CurrentConcertTable.onCreate(db);
        CurrentSongTable.onCreate(db);
        PlaylistTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CurrentConcertTable.onUpgrade(db, oldVersion, newVersion);
        CurrentSongTable.onUpgrade(db, oldVersion, newVersion);
        PlaylistTable.onUpgrade(db, oldVersion, newVersion);
    }
}
