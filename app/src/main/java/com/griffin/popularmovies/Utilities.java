package com.griffin.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.detail_movie.CastingMovie;
import com.griffin.popularmovies.detail_movie.DetailFavoriteFragment;
import com.griffin.popularmovies.detail_movie.DetailMovie;
import com.griffin.popularmovies.movie_list.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by griffin on 18/07/16.
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();
    private static final String CHOICE = "choice";
    private static final String SELECTED_CHOICE = "selected_choice";
    private static final int POPULAR_CHOICE = 0;
    private static final int TOP_RATED_CHOICE = 1;
    private static final int UPCOMIG_CHOICE = 2;
    private static final int NOw_PLAYING_CHOICE = 3;
    private static final int FAVORITE_CHOICE = 4;


    public static Movie getMovieFromCursor(Cursor movieCursor){
        Movie movie = new Movie();
        movie.setId(movieCursor.getInt(DetailFavoriteFragment.COL_FAVORITE_MOVIE_ID));
        movie.setPicture_url(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_PICTURE));
        movie.setDate(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DATE));
        movie.setTitle(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_TITLE));
        movie.setOriginalTitle(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_ORIGINAL_TITLE));
        movie.setOverview(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_OVERVIEW));
        movie.setRating(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_RATING));
        movie.getDetailMovie().setRuntime(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_RUNTIME));

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Type type = new TypeToken<String[]>() {}.getType();
        String genreJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_GENRE);
        String[] genres = gson.fromJson(genreJSON, type);
        movie.getDetailMovie().setGenre(genres);

        type = new TypeToken<List<CastingMovie>>() {}.getType();
        String castingJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_CASTING);
        List<CastingMovie>  castingList = gson.fromJson(castingJSON, type);
        movie.getDetailMovie().setCasting(castingList);

        return movie;
    }



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

            Gson gson = new GsonBuilder().create();
            String casting = gson.toJson(movie.getDetailMovie().getCasting());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_CASTING,casting);

            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_DATE, movie.getDate());

            String genre = gson.toJson(movie.getDetailMovie().getGenre());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_GENRE, genre);

            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RATING, movie.getRating());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS, movie.getDetailMovie().getReviews().toString());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME, movie.getDetailMovie().getRuntime());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER, movie.getDetailMovie().getTrailers().toString());

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


            values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE, movie.getPicture_url());

            // Finally, insert movie data into the database.
            Uri insertedUri = context.getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI,values);

        }

        movieCursor.close();

    }


    public static void removeMovieFromFavorite(Movie movie, Context context){
        context.getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                new String[]{Integer.toString(movie.getId())});
    }


    public static void setChoice (Context context, String choice){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        if(choice.equals(context.getString(R.string.key_movies_popular))){
            editor.putString(CHOICE, context.getString(R.string.pref_movies_popular));
            editor.putString(SELECTED_CHOICE, choice);
            editor.commit();
        }
        if(choice.equals(context.getString(R.string.key_movies_top_rated))){
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_top_rated));
            editor.commit();
        }
        if(choice.equals(context.getString(R.string.key_movies_upcoming))){
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_upcoming));
            editor.commit();
        }
        if(choice.equals(context.getString(R.string.key_movies_now_playing))){
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_now_playing));
            editor.commit();
        }
        if(choice.equals(context.getString(R.string.key_movies_favorite))){
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_favorite));
            editor.commit();
        }

    }

    public static String getChoice(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(CHOICE, context.getString(R.string.pref_movies_popular));
    }

    public static int getSelectedChoiceNumber(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String choice =  sharedPreferences.getString(SELECTED_CHOICE, context.getString(R.string.key_movies_popular));
        if(choice.equals(context.getString(R.string.key_movies_popular))){
            return POPULAR_CHOICE;
        }
        if(choice.equals(context.getString(R.string.key_movies_top_rated))){
            return TOP_RATED_CHOICE;
        }
        if(choice.equals(context.getString(R.string.key_movies_upcoming))){
            return UPCOMIG_CHOICE;
        }
        if(choice.equals(context.getString(R.string.key_movies_now_playing))){
            return NOw_PLAYING_CHOICE;
        }
        if(choice.equals(context.getString(R.string.key_movies_favorite))){
            return FAVORITE_CHOICE;
        }
        else return POPULAR_CHOICE;
    }

    public static String getSelectedChoice(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SELECTED_CHOICE, context.getString(R.string.key_movies_popular));
    }


    public static int isMovieFavorite(int idMovie, Context context){
        int isFavorite = 0;
        Cursor cursor = context.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI, null, MovieContract.FavoriteEntry
                .COLUMN_MOVIE_ID + " = ?", new String[]{Integer.toString(idMovie)}, null);
        if(cursor.moveToFirst()){
            isFavorite = 1;
        }
        cursor.close();
        return isFavorite;
    }

    public static String savePoster(Bitmap bitmapImage, int idMovie, Context context){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File bitmap = new File(directory, Integer.toString(idMovie));

        FileOutputStream fos = null;
        try {
            //fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos = new FileOutputStream(bitmap);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    public static Bitmap getPoster(String path, int idMovie) {
        Bitmap bitmap = null;
        try {
            File file=new File(path, Integer.toString(idMovie));
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;

    }




}
