package com.griffin.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by griffin on 16/07/16.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final int MOVIE_ID = 2569;

    // content://com.griffin.popularmovies/movie
    private static final Uri TEST_MOVIE_DIR = MovieContract.FavoriteEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID = MovieContract.FavoriteEntry.buildMovieUriFromDetailId(MOVIE_ID);


    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID), MovieProvider.MOVIE_WITH_ID);

    }
}
