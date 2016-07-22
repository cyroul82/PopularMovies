package com.griffin.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

/**
 * Created by griffin on 18/07/16.
 */
public class Utilities {

    public static Movie getMovieFromCursor(Cursor cursor){
      Movie movie = new Movie();
/*
        movie.setId(Integer.parseInt(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_ID)));
        movie.setTitle(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_TITLE));
        movie.setUrl(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_PICTURE));
        movie.setOriginalTitle(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_ORIGINAL_TITLE));
        movie.setOverview(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_OVERVIEW));
        movie.setMovieDate(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_DATE));
        movie.setMovieRating(cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_RATING));
*/

        return movie;
    }

    public static String getSortBy(Context context){
        // Gets back the choice selected by the user to sort the movies
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort_by = sharedPrefs.getString(context.getString(R.string.pref_movies_sorting_key),
                context.getString(R.string.pref_movies_popular));
        return sort_by;
    }

    public static String getSortOrder(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort_by = sharedPrefs.getString(context.getString(R.string.pref_movies_sorting_key),
                context.getString(R.string.key_movies_popular));
        return sort_by;
    }

    public static void setSortOrder(Context context, String sortOrder){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.pref_movies_sorting_key), sortOrder);
        editor.commit();
    }


}
