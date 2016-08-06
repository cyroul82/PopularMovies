package com.griffin.popularmovies.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.movie_list.Movie;
import com.griffin.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by griffin on 16/07/16.
 */
public class FetchMoviesTask extends AsyncTaskLoader<List<Movie>> {

    private List<Movie> mMovieList;

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    private String moviesJsonStr = null;

    public FetchMoviesTask (Context context){
        super(context);
    }

    @Override
    public void deliverResult(List<Movie> data) {
        super.deliverResult(data);
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mMovieList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mMovieList);
        }

        if (takeContentChanged() || mMovieList == null ) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<Movie> list) {
        super.onCanceled(list);
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mMovieList != null) {
            releaseResources(mMovieList);
            mMovieList = null;
        }

    }

    private void releaseResources(List<Movie> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /**
     * Take the String representing the complete Popular Movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private List<Movie> getPopularMoviesDataFromJson(String popularMoviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JSON_RESULTS = "results";
        final String JSON_OVERVIEW = "overview";
        final String JSON_TITLE = "title";
        final String JSON_ID = "id";
        final String JSON_IMAGE = "poster_path";
        final String JSON_MOVIE_YEAR="release_date";
        final String JSON_ORIGINAL_TITLE = "original_title";
        final String JSON_RATING = "vote_average";

        final String IMAGE_BASE_URL="http://image.tmdb.org/t/p/w185";


        JSONObject popularMoviesJson = new JSONObject(popularMoviesJsonStr);
        JSONArray popularMoviesArray = popularMoviesJson.getJSONArray(JSON_RESULTS);

        // Initialize the list containing all the popular movies
        List<Movie> moviesList = new ArrayList<>();

        for(int i = 0; i < popularMoviesArray.length(); i++) {





            // Get the JSON object representing a popular movie
            JSONObject popularMovie = popularMoviesArray.getJSONObject(i);

            int idMovie = Integer.parseInt(popularMovie.getString(JSON_ID));
            //Check from SQLite DB if already favorite
            int isFavorite = Utilities.isMovieFavorite(idMovie, getContext());

            // Creates the movie
            Movie movie = new Movie(
                    idMovie,
                    popularMovie.getString(JSON_TITLE),
                    popularMovie.getString(JSON_OVERVIEW),
                    IMAGE_BASE_URL + popularMovie.getString(JSON_IMAGE),
                    popularMovie.getString(JSON_ORIGINAL_TITLE),
                    getYear(popularMovie.getString(JSON_MOVIE_YEAR)),
                    popularMovie.getString(JSON_RATING),
                    isFavorite
            );
            //Add the movie to the list
            moviesList.add(movie);
        }
        return moviesList;
    }

    private String getYear(String date) {
        // Creates the format style to match the json format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
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
            year = month_name + " " + Integer.toString(cal.get(Calendar.YEAR)) ;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e(LOG_TAG, e.getMessage(), e);

        }
        return year;
    }



    @Override
    public List<Movie> loadInBackground() {
        try{




            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + Utilities.getChoice(getContext()) + "?";
            final String APPID_PARAM = "api_key";
            final String LANGUAGE = "language";

            final 

            // Build the Uri
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANGUAGE, Locale.getDefault().getLanguage())
                    .build();

            // Create the Url to open the connection later
            URL url = new URL(builtUri.toString());


            // Create the request to TheMovieDB and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            moviesJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the popular movies data, there's no point in attempting
            // to parse it.
            return null;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getPopularMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }
}
