package com.griffin.popularmovies.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.Pojo.MoviePage;
import com.griffin.popularmovies.Service.MovieService;

import java.io.IOException;
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

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private List<Movie> mMovieList;
    private int mPage;

    private String mChoice = null;
    private String mSearch = null;

    private CallbackFetchMoviesTask callbackFetchMoviesTask;


    public FetchMoviesTask(Context context, int page, String choice) {
        super(context);
        mPage = page;
        mChoice = choice;
    }

    public FetchMoviesTask(Context context, String search) {
        super(context);
        mSearch = search;
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

        if (takeContentChanged() || mMovieList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<Movie> list) {
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

    @Override
    public List<Movie> loadInBackground() {

        List<Movie> movieList = null;
        String BASE_URL = "https://api.themoviedb.org/3/";

        if (mChoice != null) {
            try {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieService movieService = retrofit.create(MovieService.class);
                Call<MoviePage> callMoviePojo = movieService.getMoviesPage(mChoice, BuildConfig.MOVIE_DB_API_KEY, Locale
                        .getDefault().getLanguage(), mPage);
                Response response = callMoviePojo.execute();
                MoviePage moviePage = (MoviePage) response.body();
                if (moviePage != null) {
                    movieList = moviePage.getResults();
                }
                return movieList;
            } catch (IOException e) {
                callbackFetchMoviesTask.onExceptionLoadInBackground(e.getMessage());
            }
        }

        if (mSearch != null) {
            try {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieService movieService = retrofit.create(MovieService.class);
                Call<MoviePage> callMoviePojo = movieService.getSearchMovie(BuildConfig.MOVIE_DB_API_KEY, mSearch);
                Response response = callMoviePojo.execute();
                MoviePage moviePage = (MoviePage) response.body();
                if (moviePage != null) {
                    movieList = moviePage.getResults();
                }
                return movieList;
            } catch (IOException e) {
                callbackFetchMoviesTask.onExceptionLoadInBackground(e.getMessage());
            }
        }

        return null;
    }

    public void setCallback(CallbackFetchMoviesTask callbackFetchMoviesTask) {
        this.callbackFetchMoviesTask = callbackFetchMoviesTask;
    }

    public interface CallbackFetchMoviesTask {
        void onExceptionLoadInBackground(String exception);
    }

}
