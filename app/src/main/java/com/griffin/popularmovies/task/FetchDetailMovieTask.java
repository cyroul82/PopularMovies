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
import com.griffin.popularmovies.detail_movie.DetailMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
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

    private final static String CREDITS = "credits";
    private final static String TRAILER = "videos";
    private final static String REVIEWS = "reviews";
    private final static String LANGUAGE_SYSTEM = Locale.getDefault().getLanguage().toString();
    private final static String LANGUAGE_CALLBACK = "en";

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    private HttpURLConnection mUrlConnection = null;
    private BufferedReader mReader = null;

    // Will contain the raw JSON response as a string.
    private String mDetailMoviesJsonStr = null;

    public FetchDetailMovieTask (Context context, int idMovie){
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

        if (takeContentChanged() || detailMovie == null ) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            detailMovie = new DetailMovie();
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
    @Override public void onCanceled(DetailMovie movie) {
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
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */

    @Override
    public DetailMovie loadInBackground() {

        try {

            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";

            Retrofit retrofit = new Retrofit.Builder().baseUrl(MOVIE_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MovieService movieService = retrofit.create(MovieService.class);

            Call<Trailer> callTrailer = movieService.getTrailer(mIdMovie, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallTrailer = callTrailer.execute();
            Trailer trailer = (Trailer) responseCallTrailer.body();

            List<TrailerDetail> trailerDetails = null;
            if(trailer != null) {
                trailerDetails = trailer.getResults();
            }

            detailMovie.setTrailerDetails(trailerDetails);

            Call<MovieDetail> callMovieDetail = movieService.getMovieDetail(mIdMovie, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallMovieDetail = callMovieDetail.execute();
            MovieDetail movieDetail = (MovieDetail) responseCallMovieDetail.body();
            detailMovie.setMovieDetail(movieDetail);

            Call<Credits> callCredits = movieService.getCredits(mIdMovie, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallCredits = callCredits.execute();
            Credits credits = (Credits) responseCallCredits.body();


                for (Cast cast : credits.getCast()) {
                    Call<Person> callPerson = movieService.getPerson(cast.getId(), BuildConfig.MOVIE_DB_API_KEY);
                    Response responseCallPerson = callPerson.execute();
                    Person person = (Person) responseCallPerson.body();
                    cast.setPerson(person);
                }

            detailMovie.setCredits(credits);

            Call<ReviewPage> callReviewPage = movieService.getReviews(mIdMovie, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallReviewPage = callReviewPage.execute();
            ReviewPage reviewPage = (ReviewPage) responseCallReviewPage.body();
            List<Reviews> reviewsList = null;
            if(reviewPage != null) {
                 reviewsList = reviewPage.getResults();
            }
            detailMovie.setReviewsList(reviewsList);

            Object collectionObject = detailMovie.getMovieDetail().getBelongsToCollection();
            if(collectionObject != null){
                Map<String, ?> map = (Map<String, ?>) collectionObject;
                Object object =map.get("id");
                double idCollection = Double.parseDouble(object.toString());
                Call<Collection> callCollection = movieService.getCollection(idCollection, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
                Response responseCallCollection = callCollection.execute();
                Collection collection = (Collection) responseCallCollection.body();

                detailMovie.setCollection(collection);
            }


        }
        catch (IOException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return detailMovie;

    }

}
