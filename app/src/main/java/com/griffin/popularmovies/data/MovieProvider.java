package com.griffin.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by griffin on 16/07/16.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int DETAIL = 200;


    private static final SQLiteQueryBuilder sMovieByDetailQueryBuilder;

    static{
        sMovieByDetailQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //favorite_detail INNER JOIN favorite ON favorite_detail.movie_id = favorite.movie_id
        sMovieByDetailQueryBuilder.setTables(
                MovieContract.FavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.DetailEntry.TABLE_NAME +
                        " ON " + MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY +
                        " = " + MovieContract.DetailEntry.TABLE_NAME +
                        "." + MovieContract.DetailEntry._ID);
    }

    static UriMatcher buildUriMatcher (){
        //Create the default matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //get the authority from the contract
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //build Uri Matcher
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_DETAIL, DETAIL);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        //initialize the MovieDbHelper
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    //detail.id = ?
    private static final String sDetailSelection = MovieContract.FavoriteEntry.TABLE_NAME +
            "." + MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY + " = ? ";

    private Cursor getMovieByDetail(Uri uri, String[] projection, String sortOrder) {
        long detailId = MovieContract.FavoriteEntry.getDetailIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sDetailSelection;
        selectionArgs = new String[]{Long.toString(detailId)};


        return sMovieByDetailQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_WITH_ID:
            {
                retCursor = getMovieByDetail(uri, projection, sortOrder);
                break;
            }


            case MOVIE: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case DETAIL:
            {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.DetailEntry.TABLE_NAME,
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
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case DETAIL:
                return MovieContract.DetailEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri );
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){

            case MOVIE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            }
            case DETAIL: {
                long _id = db.insert(MovieContract.DetailEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.DetailEntry.buildMovieDetailUri(_id);
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
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection + " = ?", selectionArgs);
                break;

            }
            case DETAIL: {
                rowsDeleted = db.delete(MovieContract.DetailEntry.TABLE_NAME, selection + " = ?", selectionArgs);
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
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            }
            case DETAIL: {
                rowsUpdated = db.update(MovieContract.DetailEntry.TABLE_NAME, values, selection, selectionArgs);
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
                        long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, value);
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
            case (DETAIL): {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.DetailEntry.TABLE_NAME, null, value);
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
