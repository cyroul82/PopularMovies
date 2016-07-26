package com.griffin.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by griffin on 16/07/16.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    static UriMatcher buildUriMatcher (){
        //Create the default matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //get the authority from the contract
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //build Uri Matcher
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        //initialize the MovieDbHelper
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    //movie.id = ?
    private static final String sFavoriteMovieSelection = MovieContract.FavoriteMoviesEntry.TABLE_NAME +
            "." + MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ? ";
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_WITH_ID:
            {
                long movieId = MovieContract.FavoriteMoviesEntry.getMovieIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        sFavoriteMovieSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }


            case MOVIE: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //set notification to the content Resolver, to notify the change and update the UI
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE_WITH_ID :
                return MovieContract.FavoriteMoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.FavoriteMoviesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri );
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match){

            case MOVIE: {
                _id = db.insert(MovieContract.FavoriteMoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteMoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            }



            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //notify the change of the table
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted ??? TODO: Didn't understand
        if(selection == null) selection="1";

        switch (match){

            case MOVIE: {
                rowsDeleted = db.delete(MovieContract.FavoriteMoviesEntry.TABLE_NAME, selection + " = ?", selectionArgs);
                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        // this makes delete all rows return the number of rows deleted
        if(selection == null) selection="1";

        switch (match){

            case MOVIE: {
                rowsUpdated = db.update(MovieContract.FavoriteMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case (MOVIE): {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.FavoriteMoviesEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                }
                finally {
                    db.endTransaction();
                }
                //notify the content resolver of the change
                getContext().getContentResolver().notifyChange(uri, null);
                //Return the number of rows inserted
                return returnCount;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


}
