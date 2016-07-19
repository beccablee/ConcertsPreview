package com.example.jinjinz.concertprev.DatabaseClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by noradiegwu on 7/15/16.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {
    // we can use ORMs (object relational mappers) to persist models (the user!) to a db table

    private static final String DB_NAME = "concertAppDatabase.db";
    private static final int DB_VERSION = 0;



    public UserDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    // These is where we need to write create table statements.
    // This is called when database is created
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // SQL for creating the tables
/*        SongsTable.onCreate(sqLiteDatabase);
        ConcertsTable.onCreate(sqLiteDatabase);*/
    }


    // This method is called when database is upgraded like
    // modifying the table structure,
    // adding constraints to database,
    // changeing the version number, etc
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // SQL for updating the tables
/*        SongsTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        ConcertsTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);*/
    }
}
