package com.griffin.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.detail_movie.CastingMovie;
import com.griffin.popularmovies.movie_list.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by griffin on 18/07/16.
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    private final static String STR_SEPARATOR = " / ";
    private final static String STR_SEPARATOR2 = "---";

    public static void addMovieToFavorite(Movie movie, Context context) {
        long movieRowId;

        // First, check if the mMovie with this id already exists in the db
        Cursor movieCursor = context.getContentResolver().query(
                //The URI content://com.griffin.popularmovies :
                MovieContract.FavoriteEntry.CONTENT_URI,
                //The list of which columns to return, in this case only the _ID column
                new String[]{MovieContract.FavoriteEntry._ID},
                //The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs))
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                //only one clause movie_id
                new String[]{Long.toString(movie.getId())},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.FavoriteEntry._ID);
            movieRowId = movieCursor.getLong(movieIdIndex);
        } else {

            ContentValues detail = new ContentValues();
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, movie.getTitle());


            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_CASTING, convertListToString(movie.getCasting()));
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_DATE, movie.getDate());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_GENRE, convertArrayToString(movie.getGenre()));
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RATING, movie.getRating());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS, movie.getReviews().toString());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME, movie.getRuntime());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER, movie.getTrailers().toString());

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            Uri insertedDetailUri = context.getContentResolver().insert(MovieContract.DetailEntry.CONTENT_URI, detail);
            long insertedRowId = ContentUris.parseId(insertedDetailUri);

            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues values = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            values.put(MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY,insertedRowId);
            values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE, movie.getUrl());

            // Finally, insert movie data into the database.
            Uri insertedUri = context.getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI,values);

        }

        movieCursor.close();

    }

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+ STR_SEPARATOR;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str){
        String[] arr = str.split(STR_SEPARATOR);
        return arr;
    }


    public static String convertListToString(List<CastingMovie> castingList) {
        StringBuffer stringBuffer = new StringBuffer();
        String str;
        for(CastingMovie castingMovie : castingList){
            String[] array = new String[2];
            array[0] = castingMovie.getName();
            array[1] = castingMovie.getCharacter();
            str = convertArrayToString(array);
            stringBuffer.append(str).append(STR_SEPARATOR2);
        }

         // Remove last separator
        int lastIndex = stringBuffer.lastIndexOf(STR_SEPARATOR2);
        stringBuffer.delete(lastIndex, lastIndex + STR_SEPARATOR2.length() + 1);

        return stringBuffer.toString();
    }

    public static List<CastingMovie> convertStringToList(String str) {
        List<String> list = Arrays.asList(str.split(STR_SEPARATOR2));
        List<CastingMovie> castingList = new ArrayList<>();
        for(String string : list){
            String[] strArray = convertStringToArray(string);
            CastingMovie castingMovie = new CastingMovie();
            castingMovie.setName(strArray[0]);
            castingMovie.setCharacter(strArray[1]);
            castingList.add(castingMovie);
        }
        return castingList;
    }


    public static void removeMovie(Movie movie, Context context){
        context.getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
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
