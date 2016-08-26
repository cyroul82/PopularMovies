package com.griffin.popularmovies.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.Pojo.Collection;
import com.griffin.popularmovies.Pojo.Credits;
import com.griffin.popularmovies.Pojo.MovieDetail;
import com.griffin.popularmovies.Pojo.Part;
import com.griffin.popularmovies.Pojo.Person;
import com.griffin.popularmovies.Pojo.ReviewPage;
import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.Pojo.Trailer;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.Service.MovieService;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.adapter.CastingAdapter;
import com.griffin.popularmovies.detail_movie.DetailMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by griffin on 21/07/16.
 */
public class FetchDetailMovieTask extends AsyncTaskLoader<DetailMovie> {

    private int mIdMovie;
    private DetailMovie detailMovie;

    private final String LOG_TAG = FetchDetailMovieTask.class.getSimpleName();

    private final static String LANGUAGE_SYSTEM = Locale.getDefault().getLanguage().toString();

    public FetchDetailMovieTask(Context context, int idMovie) {
        super(context);
        mIdMovie = idMovie;

    }

    @Override
    public void deliverResult(DetailMovie detailMovie) {
        super.deliverResult(detailMovie);
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (detailMovie != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(detailMovie);
        }

        if (takeContentChanged() || detailMovie == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            detailMovie = new DetailMovie();
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
    public void onCanceled(DetailMovie movie) {
        super.onCanceled(movie);
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (detailMovie != null) {
            releaseResources(detailMovie);
            detailMovie = null;
        }

    }

    private void releaseResources(DetailMovie movie) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /**
     * Take the String representing the complete detail Movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */

    @Override
    public DetailMovie loadInBackground() {

       return Utilities.getMovieDetail(mIdMovie);

    }

}
