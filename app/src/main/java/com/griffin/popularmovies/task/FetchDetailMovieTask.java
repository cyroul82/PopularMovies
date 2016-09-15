package com.griffin.popularmovies.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.Utility;
import com.griffin.popularmovies.detail_movie.DetailMovie;

import java.io.IOException;

/**
 * Created by griffin on 21/07/16.
 */
public class FetchDetailMovieTask extends AsyncTaskLoader<DetailMovie> {

    private final String LOG_TAG = FetchDetailMovieTask.class.getSimpleName();
    private int mIdMovie;
    private DetailMovie mDetailMovie;

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
        if (mDetailMovie != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mDetailMovie);
        }

        if (takeContentChanged() || mDetailMovie == null) {
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
    public void onCanceled(DetailMovie movie) {
        super.onCanceled(movie);
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mDetailMovie != null) {
            releaseResources(mDetailMovie);
            mDetailMovie = null;
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
        try {
            return Utility.getMovieDetail(mIdMovie, getContext());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

}
