package com.griffin.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by griffin on 14/07/16.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createFavoriteMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, "12546");
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, "Get your hands dirty and program hard !");
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_PICTURE, "movie picutre");
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE, "Lève toi et bat toi !");
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, "Le Travail c'est la santé !");
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE, "2016");
        testValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING, "8");

        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);

            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

}
