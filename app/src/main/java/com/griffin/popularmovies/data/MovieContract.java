package com.griffin.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by griffin on 14/07/16.
 */
public class MovieContract {

    //define the content_authority
    public static final String CONTENT_AUTHORITY = "com.griffin.popularmovies";

    //define the base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //define the path to match the table
    public static final String PATH_MOVIE = "favorite";
    public static final String PATH_DETAIL = "detail";


    //Database table column Names
    public static final class FavoriteEntry implements BaseColumns {

        //build uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table Name
        public static final String TABLE_NAME = "favorite";

        //Table's Column names
        public static final String COLUMN_DETAIL_KEY = "detail_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_PICTURE = "movie_picture";

        //build the Uri upon the id
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }

        public static Uri buildMovieUriFromDetailId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static Long getDetailIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    //Database table column Names
    public static final class DetailEntry implements BaseColumns {
        //build uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DETAIL;

        //Table Name
        public static final String TABLE_NAME = "favorite_detail";

        //Table's Column names
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_ORIGINAL_TITLE = "movie_original_title";
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        public static final String COLUMN_MOVIE_DATE = "movie_date";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_GENRE = "movie_genre";
        public static final String COLUMN_MOVIE_RUNTIME = "movie_runtime";
        public static final String COLUMN_MOVIE_CASTING = "movie_casting";
        public static final String COLUMN_MOVIE_TRAILER = "movie_videos";
        public static final String COLUMN_MOVIE_REVIEWS = "movie_reviews";
        public static final String COLUMN_MOVIE_TAGLINE = "movie_tagline";

        //build the Uri upon the id
        public static Uri buildMovieDetailUri(long id) {
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }
    }


}
