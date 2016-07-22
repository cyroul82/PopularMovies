package com.griffin.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by griffin on 14/07/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    //Define the version of the database
    private static final int DATABASE_VERSION = 1;

    //Define the database name
    static final String DATABASE_NAME = "favorite_movie.db";

    //Constructor
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creates the table popular_movies
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.FavoriteMoviesEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_PICTURE + " TEXT NOT NULL, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +


                // Make sure that only one movie and not duplicate it
                " UNIQUE (" + MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this method is called when the db has been already created but the version has changed
        //Drops the table it exists and then call onCreate() method to recreate the DB
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);


    }
}
