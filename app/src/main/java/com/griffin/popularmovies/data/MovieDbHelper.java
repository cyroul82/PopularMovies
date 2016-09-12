package com.griffin.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by griffin on 14/07/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    //Define the database name
    static final String DATABASE_NAME = "favorite_movie.db";
    //Define the version of the database
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creates the table favorite
        /*final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE + " TEXT NOT NULL, " +

                // Set up the detail column as a foreign key to detail table.
                " FOREIGN KEY (" + MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY + ") REFERENCES " +
                MovieContract.DetailEntry.TABLE_NAME + " (" + MovieContract.DetailEntry._ID + "), " +

                // Make sure that only one movie and not duplicate it
                " UNIQUE (" + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";*/


        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + MovieContract.DetailEntry.TABLE_NAME + " (" +
                MovieContract.DetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_PICTURE + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_GENRE + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME + " TEXT NOT NULL, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_CASTING + " TEXT, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER + " TEXT, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS + " TEXT, " +
                MovieContract.DetailEntry.COLUMN_MOVIE_TAGLINE + " TEXT, " +

                // Make sure that only one movie and not duplicate it
                " UNIQUE (" + MovieContract.DetailEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        //db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_DETAIL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this method is called when the db has been already created but the version has changed
        //Drops the table it exists and then call onCreate() method to recreate the DB
        //db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.DetailEntry.TABLE_NAME);
        onCreate(db);

    }
}
