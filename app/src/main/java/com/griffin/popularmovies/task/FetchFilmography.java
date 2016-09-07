package com.griffin.popularmovies.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.griffin.popularmovies.BuildConfig;
import com.griffin.popularmovies.Pojo.CastFilmography;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Service.MovieService;
import com.griffin.popularmovies.Utilities;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by griffin on 30/08/16.
 */
public class FetchFilmography extends AsyncTaskLoader<CastFilmography> {

    private static final String LOG_TAG = FetchFilmography.class.getSimpleName();
    private int mIdCast;

    public FetchFilmography(Context context, int idCast) {
        super(context);
        mIdCast = idCast;
    }

    @Override
    public CastFilmography loadInBackground() {

        CastFilmography castFilmography = null;

        try {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(getContext().getResources().getString(R.string.BASE_URL))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieService movieService = retrofit.create(MovieService.class);

            Call<CastFilmography> callCastFilmography = movieService.getFilmography(mIdCast, BuildConfig.MOVIE_DB_API_KEY, Utilities.LANGUAGE_SYSTEM);
            Response responseCallCastFilmography = callCastFilmography.execute();
            castFilmography = (CastFilmography) responseCallCastFilmography.body();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }


        return castFilmography;
    }
}
