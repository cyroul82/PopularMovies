package com.griffin.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by griffin on 18/07/16.
 */
public class Utilities {

    public static Movie getMovieFromCursor(Cursor cursor){
      Movie movie = new Movie();

        movie.setId(Integer.parseInt(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_ID)));
        movie.setTitle(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_TITLE));
        movie.setUrl(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_PICTURE));
        movie.setOriginalTitle(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_ORIGINAL_TITLE));
        movie.setOverview(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_OVERVIEW));
        movie.setMovieDate(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_DATE));
        movie.setMovieRating(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_RATING));

        return movie;
    }





    public static String getOrder(Context context, int spinnerChoice){

        switch (spinnerChoice){
            case 0: {
                return context.getString(R.string.pref_movies_popular);

            }
            case 1 : {
                return context.getString(R.string.pref_movies_top_rated);
            }
            case 2 : {
                return context.getString(R.string.pref_movies_upcoming);

            }
            case 3 : {
                return context.getString(R.string.pref_movies_now_playing);

            }
            case 4 : {
                return context.getString(R.string.pref_movies_favorite);
            }
            default:
                return context.getString(R.string.pref_movies_popular);
        }

    }

}
