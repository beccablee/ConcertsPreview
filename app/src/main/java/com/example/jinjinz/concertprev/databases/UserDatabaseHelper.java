package com.example.jinjinz.concertprev.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UserDatabaseHelper extends SQLiteOpenHelper {
// responsible for creating the db
    private static final String DB_NAME = "concertAppDatabase.db";
    private static final int DB_VERSION = 1;



    public UserDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    /** Calls to table methods of creation
     * Called when database is being created
     * @param sqLiteDatabase the SQLite database being created */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // SQL for creating the tables
        SongsTable.onCreate(sqLiteDatabase);
        ConcertsTable.onCreate(sqLiteDatabase);
    }

    /** Calls to table methods of upgrading
     * Called when database is being upgraded
     * @param sqLiteDatabase the SQLite database created
     * @param oldVersion the version of the database before updating
     * @param newVersion the version of the database after updating */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // SQL for updating the tables
        SongsTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        ConcertsTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}
