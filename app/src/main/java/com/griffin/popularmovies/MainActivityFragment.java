package com.griffin.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private PopularMoviesAdapter mPopularMoviesAdapter = null;
    private ArrayList<Movie> moviesList = null;

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // Creates a new List of movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.key_movies_list))){
            moviesList = new ArrayList<Movie>();
        }
        //restore the previous state
        else {
            moviesList = savedInstanceState.getParcelableArrayList(getString(R.string.key_movies_list));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mPopularMoviesAdapter = new PopularMoviesAdapter(getActivity(), R.layout.movie_item_picture, R.id.movieItemPictureImageView, moviesList);

        GridView gridViewPopularMovies = (GridView)rootView.findViewById(R.id.gridview_moviesList);
        gridViewPopularMovies.setAdapter(mPopularMoviesAdapter);

        //set up the OnClick Listener to gridViewPopularMovies
        gridViewPopularMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the back from the adapter
                Movie movie = mPopularMoviesAdapter.getItem(position);


                //Create an Intent and start the activity
                Intent intent = new Intent(getActivity(), DetailMovieActivity.class);
                intent.putExtra(getString(R.string.key_movies_list), movie);

                startActivity(intent);
            }
        });

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //we put the moviesList into the bundle to avoid querying again while rebuilding
        outState.putParcelableArrayList(getString(R.string.key_movies_list), moviesList);
        super.onSaveInstanceState(outState);
    }

    public void checkConnectionStatus(){
        final ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(!networkInfoWifi.isAvailable() && !networkInfoMobile.isAvailable() ){
            Toast.makeText(getActivity(), R.string.connectivity, Toast.LENGTH_LONG).show();

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        checkConnectionStatus();
        updateMovies();
    }

    public void updateMovies(){

        // Gets the back the choice selected by the user to sort the movies
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = sharedPrefs.getString(getString(R.string.pref_movies_sorting_key), getString(R.string.pref_movies_popular));

        // Creates the long task and executes it
        FetchMoviesTask mFetchMoviesTask = new FetchMoviesTask();
        mFetchMoviesTask.execute(sort_by);
    }


    /*
    ** This class is used to fech the movies data from themoviedb.org
    ** We use AsyncTask to execute lhe long task outside the UI Thread
    */
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

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


            JSONObject forecastJson = new JSONObject(popularMoviesJsonStr);
            JSONArray popularMoviesArray = forecastJson.getJSONArray(JSON_RESULTS);

            // Initialize the list containing all the popular movies
            List<Movie> moviesList = new ArrayList<>();

            for(int i = 0; i < popularMoviesArray.length(); i++) {

                // Get the JSON object representing a popular movie
                JSONObject popularMovie = popularMoviesArray.getJSONObject(i);

                // Creates the movie
                Movie movie = new Movie(
                        Integer.parseInt(popularMovie.getString(JSON_ID)),
                        popularMovie.getString(JSON_TITLE),
                        popularMovie.getString(JSON_OVERVIEW),
                        IMAGE_BASE_URL + popularMovie.getString(JSON_IMAGE),
                        popularMovie.getString(JSON_ORIGINAL_TITLE),
                        getYear(popularMovie.getString(JSON_MOVIE_YEAR)),
                        popularMovie.getString(JSON_RATING)
                );


                //Add the movie to the list
                moviesList.add(movie);

            }

            return moviesList;

        }

        private String getYear(String date) {
            // Creates the format style to match the json format
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String year = null;
            try {
                // Creates the date with the format previously created
                Date d = format.parse(date);
                // Instancie le calendrier
                Calendar cal = Calendar.getInstance();
                // Sets up the calendar
                cal.setTime(d);
                // gets back the year out of the date and cast it into a string
                year = Integer.toString(cal.get(Calendar.YEAR));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Log.e(LOG_TAG, e.getMessage(), e);

            }
            return year;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            try{

                final String FORECAST_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "?";
                final String APPID_PARAM = "api_key";

                // Build the Uri
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
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
                // If the code didn't successfully get the popular movies data, there's no point in attemping
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

            /* TODO: Delete this comment later
             *
              *         https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=a66e3b345254d37fa295de9ec4e3c56b
              * */
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(movies != null){
                mPopularMoviesAdapter.clear();
                for (Movie movie : movies) {
                    mPopularMoviesAdapter.add(movie);
                }
            }
        }
    }
}
