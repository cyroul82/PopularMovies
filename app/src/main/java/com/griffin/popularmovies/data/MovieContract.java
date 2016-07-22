package com.griffin.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.griffin.popularmovies.Movie;

/**
 * Created by griffin on 14/07/16.
 */
public class MovieContract {

    //define the content_authority
    public static final String CONTENT_AUTHORITY = "com.griffin.popularmovies";

    //define the base content URI
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://" + CONTENT_AUTHORITY);

    //define the path to match the table
    public static final String PATH_MOVIE = "favorite_movies";


    //Database table column Names
    public static final class FavoriteMoviesEntry implements BaseColumns {

        //build uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table Name
        public static final String TABLE_NAME = "favorite";

        //Table's Column names
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_PICTURE = "movie_picture";
        public static final String COLUMN_MOVIE_ORIGINAL_TITLE = "movie_original_title";
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        public static final String COLUMN_MOVIE_DATE = "movie_date";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";


        //build the Uri upon the id
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }

        public static Uri buildMovieUriFromIdMovie(int id){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_MOVIE_ID, Integer.toString(id)).build();
        }



        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }



}
