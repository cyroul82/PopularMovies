package com.griffin.popularmovies.task;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.detail_movie.ActorMovie;
import com.griffin.popularmovies.detail_movie.ExtraDetailMovie;
import com.griffin.popularmovies.detail_movie.TrailerMovie;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by griffin on 21/07/16.
 */
public class FetchDetailMovieTask extends AsyncTaskLoader<ExtraDetailMovie> {

    private Movie mMovie;
    private ExtraDetailMovie extraDetailMovie;

    private final String LOG_TAG = FetchDetailMovieTask.class.getSimpleName();

    private final static String CREDITS = "credits";
    private final static String TRAILER = "videos";

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    private HttpURLConnection mUrlConnection = null;
    private BufferedReader mReader = null;

    // Will contain the raw JSON response as a string.
    private String mDetailMoviesJsonStr = null;

    public FetchDetailMovieTask (Context context, Movie movie){
        super(context);
        mMovie = movie;

    }

    @Override
    public void deliverResult(ExtraDetailMovie extraDetailMovie) {
        super.deliverResult(extraDetailMovie);
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (extraDetailMovie != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(extraDetailMovie);
        }

        if (takeContentChanged() || extraDetailMovie == null ) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            extraDetailMovie = new ExtraDetailMovie();
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
    @Override public void onCanceled(ExtraDetailMovie movie) {
        super.onCanceled(movie);
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (extraDetailMovie != null) {
            releaseResources(extraDetailMovie);
            extraDetailMovie = null;
        }

    }

    private void releaseResources(ExtraDetailMovie movie) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /**
     * Take the String representing the complete detail Movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private List<ActorMovie> getActorFromJson(String creditsMoviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JSON_RESULTS = "cast";
        final String JSON_NAME = "name";
        final String JSON_CHARACTER = "character";



        JSONObject creditsMovieJson = new JSONObject(creditsMoviesJsonStr);
        JSONArray creditsMovieArray = creditsMovieJson.getJSONArray(JSON_RESULTS);

        List<ActorMovie> actorMovieList = new ArrayList<>();

        for(int i = 0; i < creditsMovieArray.length(); i++) {

            // Get the JSON object representing an actor
            JSONObject creditMovie = creditsMovieArray.getJSONObject(i);

            // udpate the movie detail
            ActorMovie creditsDetail = new ActorMovie();
            creditsDetail.setCharacter(creditMovie.getString(JSON_CHARACTER));
            creditsDetail.setName(creditMovie.getString(JSON_NAME));

            //Add the movie to the list
            actorMovieList.add(creditsDetail);

        }

        return actorMovieList;

    }

    private String[] getGenreFromJson(String genreMoviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JSON_RESULTS = "genres";
        final String JSON_NAME = "name";

        JSONObject genreMovieJson = new JSONObject(genreMoviesJsonStr);
        JSONArray genreMovieArray = genreMovieJson.getJSONArray(JSON_RESULTS);

        String[] genreList = new String[genreMovieArray.length()];

        for(int i = 0; i < genreMovieArray.length(); i++) {

            // Get the JSON object representing a genre
            JSONObject genreMovie = genreMovieArray.getJSONObject(i);

            // add genre to the array
            genreList[i] = genreMovie.getString(JSON_NAME);

        }

        return genreList;

    }

    private List<TrailerMovie> getTrailFromJson(String trailerJsonStr)
            throws JSONException{
        // These are the names of the JSON objects that need to be extracted.
        final String JSON_RESULTS = "results";
        final String JSON_KEY = "key";
        final String JSON_NAME = "name";
        final String JSON_SITE = "site";
        final String JSON_TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(JSON_RESULTS);

        List<TrailerMovie> trailerList = new ArrayList<>();

        for (int i=0 ; i < trailerArray.length() ; i++) {
            // Get the JSON object representing an trailer
            JSONObject trailer = trailerArray.getJSONObject(i);

            // udpate the trailer detail
            TrailerMovie trailerMovie = new TrailerMovie();
            trailerMovie.setKeyTrailer(trailer.getString(JSON_KEY));
            trailerMovie.setNameTrailer(trailer.getString(JSON_NAME));
            trailerMovie.setSiteTrailer(trailer.getString(JSON_SITE));
            trailerMovie.setType(trailer.getString(JSON_TYPE));

            //Add the trailer to the list
            trailerList.add(trailerMovie);
        }
        return trailerList;
    }

    @Override
    public ExtraDetailMovie loadInBackground() {
        try {
            extraDetailMovie.setActors(getActorFromJson(getDataFromTheMovieDB(mMovie.getId(), CREDITS)));
            extraDetailMovie.setGenre(getGenreFromJson(getDataFromTheMovieDB(mMovie.getId(), null)));
            extraDetailMovie.setTrailers(getTrailFromJson(getDataFromTheMovieDB(mMovie.getId(), TRAILER)));
            return extraDetailMovie;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    private String getDataFromTheMovieDB(int id, String param){
        String parsedParam;
        if (param == null){
            parsedParam = "";
        }
        else {
            parsedParam = "/" + param;
        }

        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + Integer.toString(id) + parsedParam + "?";
        final String APPID_PARAM = "api_key";
        final String LANGUAGE = "language";

        try{

            // Build the Uri
            final Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANGUAGE, Locale.getDefault().getLanguage())
                    .build();

            // Create the Url to open the connection later
            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDB and open the connection
            mUrlConnection = (HttpURLConnection) url.openConnection();
            mUrlConnection.setRequestMethod("GET");
            mUrlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = mUrlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            mReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = mReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            mDetailMoviesJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the popular movies data, there's no point in attempting
            // to parse it.
            return null;
        }
        finally {
            if (mUrlConnection != null) {
                mUrlConnection.disconnect();
            }
            if (mReader != null) {
                try {
                    mReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return mDetailMoviesJsonStr;

    }
}
