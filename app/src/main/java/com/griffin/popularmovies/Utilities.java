package com.griffin.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.movie_list.FavoriteMovieFragment;
import com.griffin.popularmovies.movie_list.Movie;

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

    public static void addMovieToFavorite(Movie movie, Context context) {
        //long movieRowId;

        // First, check if the mMovie with this id already exists in the db
        Cursor movieCursor = context.getContentResolver().query(
                //The URI content://com.griffin.popularmovies :
                MovieContract.FavoriteMoviesEntry.CONTENT_URI,
                //The list of which columns to return, in this case only the _ID column
                new String[]{MovieContract.FavoriteMoviesEntry._ID},
                //The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs))
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                //only one clause movie_id
                new String[]{Long.toString(movie.getId())},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.FavoriteMoviesEntry._ID);
            //movieRowId = movieCursor.getLong(movieIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues values = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_PICTURE, movie.getUrl());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE, movie.getMovieDate());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING, movie.getMovieRating());

            // Finally, insert location data into the database.
            Uri insertedUri = context.getContentResolver().insert(MovieContract.FavoriteMoviesEntry.CONTENT_URI,values);

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            // movieRowId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();

    }

    public static void removeMovie(Movie movie, Context context){
        context.getContentResolver().delete(MovieContract.FavoriteMoviesEntry.CONTENT_URI,
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID,
                new String[]{Integer.toString(movie.getId())});
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
