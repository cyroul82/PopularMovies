package com.griffin.popularmovies.Service;

import com.griffin.popularmovies.Pojo.Credits;
import com.griffin.popularmovies.Pojo.MovieDetail;
import com.griffin.popularmovies.Pojo.MoviePage;
import com.griffin.popularmovies.Pojo.ReviewPage;
import com.griffin.popularmovies.Pojo.Trailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by griffin on 08/08/16.
 */

public interface MovieService {

    //Get movies list by choice
    @GET("{choice}")
    Call<MoviePage> getMoviesPage(@Path("choice") String choice, @Query("api_key") String apiKey, @Query("language") String language);

    //Get the videos of a movies by Id
    @GET("{id}/videos")
    Call<Trailer> getTrailer(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //get all the detail of a movie by Id
    @GET("{id}")
    Call<MovieDetail> getMovieDetail(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //get casting by id
    @GET("{id}/credits")
    Call<Credits> getCredits(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //get reviews by id
    @GET("{id}/reviews")
    Call<ReviewPage> getReviews(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

}
