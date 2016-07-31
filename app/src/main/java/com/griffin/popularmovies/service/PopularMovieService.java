package com.griffin.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.movie_list.Movie;

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
 * Created by griffin on 31/07/16.
 */
public class PopularMovieService extends IntentService{

    private final String LOG_TAG = PopularMovieService.class.getSimpleName();
    public static final String MOVIE_QUERY ="mq";


    public PopularMovieService(String name) {
        super("PopularMovie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String query = intent.getStringExtra(MOVIE_QUERY);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try{

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            int spinnerValue = sharedPrefs.getInt(MainActivity.USER_CHOICE, MainActivity.POPULAR_CHOICE);
            String choice;
            switch(spinnerValue){
                case MainActivity.POPULAR_CHOICE: {
                    choice = this.getString(R.string.pref_movies_popular);
                    break;
                }
                case MainActivity.UPCOMING_CHOICE: {
                    choice = this.getString(R.string.pref_movies_upcoming);
                    break;
                }
                case MainActivity.TOP_RATED_CHOICE: {
                    choice = this.getString(R.string.pref_movies_top_rated);
                    break;
                }
                case MainActivity.NOW_PLAYING_CHOICE: {
                    choice = this.getString(R.string.pref_movies_now_playing);
                    break;
                }

                default :
                    choice = this.getString(R.string.pref_movies_popular);
            }


            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + choice + "?";
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
                //return void;
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
                //return void;
            }

            moviesJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the popular movies data, there's no point in attempting
            // to parse it.
            //return null;
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



        //return void;
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
            int isFavorite = Utilities.isMovieFavorite(idMovie, this);

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

}
