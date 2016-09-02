package com.griffin.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.Pojo.Collection;
import com.griffin.popularmovies.Pojo.Credits;
import com.griffin.popularmovies.Pojo.Genre;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.Pojo.MovieDetail;
import com.griffin.popularmovies.Pojo.Person;
import com.griffin.popularmovies.Pojo.ReviewPage;
import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.Pojo.Trailer;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.Service.MovieService;
import com.griffin.popularmovies.adapter.CastingAdapter;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.detail_movie.DetailFavoriteFragment;
import com.griffin.popularmovies.detail_movie.DetailMovie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by griffin on 18/07/16.
 */
public class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

    private static final String TAG = Utilities.class.getSimpleName();
    private static final String CHOICE = "choice";
    private static final String SELECTED_CHOICE = "selected_choice";
    private static final int POPULAR_CHOICE = 0;
    private static final int TOP_RATED_CHOICE = 1;
    private static final int UPCOMIG_CHOICE = 2;
    private static final int NOw_PLAYING_CHOICE = 3;
    private static final int FAVORITE_CHOICE = 4;

    public final static String LANGUAGE_SYSTEM = Locale.getDefault().getLanguage();


    public static DetailMovie getDetailMovieFromCursor(Cursor movieCursor) {
        DetailMovie detailMovie = new DetailMovie();

        detailMovie.getMovieDetail().setId(movieCursor.getInt(DetailFavoriteFragment.COL_FAVORITE_MOVIE_ID));
        detailMovie.getMovieDetail().setPosterPath(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_PICTURE));
        detailMovie.getMovieDetail().setReleaseDate(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DATE));
        detailMovie.getMovieDetail().setTitle(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_TITLE));
        detailMovie.getMovieDetail().setOriginalTitle(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_ORIGINAL_TITLE));
        detailMovie.getMovieDetail().setOverview(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_OVERVIEW));
        detailMovie.getMovieDetail().setVoteAverage(movieCursor.getDouble(DetailFavoriteFragment.COL_FAVORITE_MOVIE_RATING));

        detailMovie.getMovieDetail().setRuntime(movieCursor.getInt(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_RUNTIME));

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();


        Type type = new TypeToken<List<Genre>>() {
        }.getType();
        String genreJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_GENRE);
        System.out.println(genreJSON);
        List<Genre> genres = gson.fromJson(genreJSON, type);
        detailMovie.getMovieDetail().setGenres(genres);


        type = new TypeToken<List<Cast>>() {
        }.getType();
        String castingJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_CASTING);
        List<Cast> castList = gson.fromJson(castingJSON, type);
        detailMovie.getCredits().setCast(castList);

        detailMovie.getMovieDetail().setTagline(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_TAGLINE));

        return detailMovie;
    }


    public static void addMovieToFavorite(DetailMovie detailMovie, Context context) {

        //long movieRowId;

        // First, check if the mMovie with this id already exists in the db
        Cursor movieCursor = context.getContentResolver().query(
                //The URI content://com.griffin.popularmovies :
                MovieContract.FavoriteEntry.CONTENT_URI,
                //The list of which columns to return, in this case only the _ID column
                new String[]{MovieContract.FavoriteEntry._ID},
                /* The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs)) */
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                //only one clause movie_id
                new String[]{Long.toString(detailMovie.getMovieDetail().getId())},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.FavoriteEntry._ID);
            //movieRowId = movieCursor.getLong(movieIdIndex);
        } else {

            ContentValues detail = new ContentValues();
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, detailMovie.getMovieDetail().getTitle());

            Gson gson = new GsonBuilder().create();
            String casting = gson.toJson(detailMovie.getCredits().getCast());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_CASTING, casting);

            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_DATE, detailMovie.getMovieDetail().getReleaseDate());

            String genre = gson.toJson(detailMovie.getMovieDetail().getGenres());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_GENRE, genre);

            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE, detailMovie.getMovieDetail().getOriginalTitle());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW, detailMovie.getMovieDetail().getOverview());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RATING, Double.toString(detailMovie.getMovieDetail().getVoteAverage()));
            if (detailMovie.getReviewsList() != null) {
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS, detailMovie.getReviewsList().toString());
            }
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME, detailMovie.getMovieDetail().getRuntime());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER, detailMovie.getTrailerDetails().toString());
            detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TAGLINE, detailMovie.getMovieDetail().getTagline());

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            Uri insertedDetailUri = context.getContentResolver().insert(MovieContract.DetailEntry.CONTENT_URI, detail);
            long insertedRowId = ContentUris.parseId(insertedDetailUri);

            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues values = new ContentValues();


            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            values.put(MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY, insertedRowId);
            values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, detailMovie.getMovieDetail().getId());


            values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE, detailMovie.getMovieDetail().getPosterPath());

            // Finally, insert movie data into the database.
            //Uri insertedUri =
            context.getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);

        }
        movieCursor.close();
    }


    public static void removeMovieFromFavorite(Movie movie, Context context) {
        context.getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                new String[]{Integer.toString(movie.getId())});
    }

    /*  Set 2 parameters
    *   1 - CHOICE representing the key used in the query to www.themoviedb.org
    *   2 - SELECTED_CHOICE the String of the mSpinner selected */
    public static void setChoice(Context context, String choice) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (choice.equals(context.getString(R.string.key_movies_popular))) {
            editor.putString(CHOICE, context.getString(R.string.pref_movies_popular));
            editor.putString(SELECTED_CHOICE, choice);
            editor.commit();
        }
        if (choice.equals(context.getString(R.string.key_movies_top_rated))) {
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_top_rated));
            editor.commit();
        }
        if (choice.equals(context.getString(R.string.key_movies_upcoming))) {
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_upcoming));
            editor.commit();
        }
        if (choice.equals(context.getString(R.string.key_movies_now_playing))) {
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_now_playing));
            editor.commit();
        }
        if (choice.equals(context.getString(R.string.key_movies_favorite))) {
            editor.putString(SELECTED_CHOICE, choice);
            editor.putString(CHOICE, context.getString(R.string.pref_movies_favorite));
            editor.commit();
        }

    }


    //Get back the String choice from the mSpinner
    public static String getChoice(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(CHOICE, context.getString(R.string.pref_movies_popular));
    }

    //This method is to ease the mSpinner.setSelection(int position), return the right position
    public static int getSelectedChoiceNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String choice = sharedPreferences.getString(SELECTED_CHOICE, context.getString(R.string.key_movies_popular));
        if (choice.equals(context.getString(R.string.key_movies_popular))) {
            return POPULAR_CHOICE;
        }
        if (choice.equals(context.getString(R.string.key_movies_top_rated))) {
            return TOP_RATED_CHOICE;
        }
        if (choice.equals(context.getString(R.string.key_movies_upcoming))) {
            return UPCOMIG_CHOICE;
        }
        if (choice.equals(context.getString(R.string.key_movies_now_playing))) {
            return NOw_PLAYING_CHOICE;
        }
        if (choice.equals(context.getString(R.string.key_movies_favorite))) {
            return FAVORITE_CHOICE;
        } else return POPULAR_CHOICE;
    }

    public static String getSelectedChoice(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SELECTED_CHOICE, context.getString(R.string.key_movies_popular));
    }


    public static int isMovieFavorite(int idMovie, Context context) {
        int isFavorite = 0;
        Cursor cursor = context.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI, null, MovieContract.FavoriteEntry
                .COLUMN_MOVIE_ID + " = ?", new String[]{Integer.toString(idMovie)}, null);
        if (cursor.moveToFirst()) {
            isFavorite = 1;
        }
        cursor.close();
        return isFavorite;
    }

    //Save picture into the mobile within the app folder (MODE PRIVATE)
    public static String savePoster(Bitmap bitmapImage, int idMovie, Context context) {
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
            File file = new File(path, Integer.toString(idMovie));
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public static String getMonthAndYear(String date) {
        // Creates the format style to match the json format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
        String year = null;
        try {
            // Creates the date with the format previously created
            Date d = format.parse(date);
            // Instancie le calendrier
            Calendar cal = Calendar.getInstance();
            String month_name = month_date.format(cal.getTime());
            // Sets up the calendar
            cal.setTime(d);
            // gets back the year out of the date and cast it into a string
            year = month_name + " " + Integer.toString(cal.get(Calendar.YEAR));
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);

        }
        return year;
    }

    public static String getYear(String date) {
        // Creates the format style to match the json format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
        String year = null;
        try {
            // Creates the date with the format previously created
            Date d = format.parse(date);
            // Instancie le calendrier
            Calendar cal = Calendar.getInstance();

            //String month_name = month_date.format(cal.getTime());
            // Sets up the calendar
            cal.setTime(d);
            // gets back the year out of the date and cast it into a string
            year = Integer.toString(cal.get(Calendar.YEAR));
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);

        }
        return year;
    }

    public static void checkConnectionStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //Connected with wifi
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Connected with mobile
            }
        } else {
            //Not connected
            Toast.makeText(context, R.string.connectivity, Toast.LENGTH_LONG).show();
        }
    }


    public static DetailMovie getMovieDetail(int movieId, DetailMovie detailMovie, Context context) {

        try {

            String MOVIE_BASE_URL = context.getResources().getString(R.string.BASE_URL);

            Retrofit retrofit = new Retrofit.Builder().baseUrl(MOVIE_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MovieService movieService = retrofit.create(MovieService.class);

            Call<Trailer> callTrailer = movieService.getTrailer(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallTrailer = callTrailer.execute();
            Trailer trailer = (Trailer) responseCallTrailer.body();

            List<TrailerDetail> trailerDetails = null;
            if (trailer != null) {
                trailerDetails = trailer.getResults();
            }
            detailMovie.setTrailerDetails(trailerDetails);


            Call<MovieDetail> callMovieDetail = movieService.getMovieDetail(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallMovieDetail = callMovieDetail.execute();
            MovieDetail movieDetail = (MovieDetail) responseCallMovieDetail.body();
            detailMovie.setMovieDetail(movieDetail);


            Call<Credits> callCredits = movieService.getCredits(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallCredits = callCredits.execute();
            Credits credits = (Credits) responseCallCredits.body();


            if (credits != null) {
                int castingMax;

                if (credits.getCast().size() < CastingAdapter.MAX_CASTING_TO_DISPLAY) {
                    castingMax = credits.getCast().size() - 1;
                } else {
                    castingMax = CastingAdapter.MAX_CASTING_TO_DISPLAY;
                }

                for (int i = 0; i < castingMax; i++) {
                    Cast cast = credits.getCast().get(i);
                    Call<Person> callPerson = movieService.getPerson(cast.getId(), BuildConfig.MOVIE_DB_API_KEY);
                    Response responseCallPerson = callPerson.execute();
                    Person person = (Person) responseCallPerson.body();
                    cast.setPerson(person);
                }
            }

            detailMovie.setCredits(credits);

            Call<ReviewPage> callReviewPage = movieService.getReviews(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallReviewPage = callReviewPage.execute();
            ReviewPage reviewPage = (ReviewPage) responseCallReviewPage.body();
            List<Reviews> reviewsList = null;
            if (reviewPage != null) {
                reviewsList = reviewPage.getResults();
            }

            detailMovie.setReviewsList(reviewsList);


            Object collectionObject = detailMovie.getMovieDetail().getBelongsToCollection();
            if (collectionObject != null) {
                Map<String, ?> map = (Map<String, ?>) collectionObject;
                Object object = map.get("id");
                double idCollection = Double.parseDouble(object.toString());
                Call<Collection> callCollection = movieService.getCollection(idCollection, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
                Response responseCallCollection = callCollection.execute();
                Collection collection = (Collection) responseCallCollection.body();

                detailMovie.setCollection(collection);

            }


        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return detailMovie;
    }

    //http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns  RITEN
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }


}
