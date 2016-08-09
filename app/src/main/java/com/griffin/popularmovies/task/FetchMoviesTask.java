package com.griffin.popularmovies.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.Pojo.MoviePage;
import com.griffin.popularmovies.Service.MovieService;
import com.griffin.popularmovies.Utilities;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by griffin on 16/07/16.
 */
public class FetchMoviesTask extends AsyncTaskLoader<List<Movie>> {

    private List<Movie> mMovieList;

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


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

        List<Movie> movieList = null;
        try {
            final String BASE_URL = "https://api.themoviedb.org/3/movie/";

            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MovieService movieService = retrofit.create(MovieService.class);
            Call<MoviePage> callMoviePojo = movieService.getMoviesPage(Utilities.getChoice(getContext()), BuildConfig.MOVIE_DB_API_KEY, Locale
                    .getDefault().getLanguage().toString());
            Response response = callMoviePojo.execute();
            MoviePage moviePage = (MoviePage) response.body();
            movieList = moviePage.getResults();
        }
        catch (IOException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        finally {
            return movieList;
        }
    }
}
