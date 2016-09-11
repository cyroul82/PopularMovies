package com.griffin.popularmovies.service;

import com.griffin.popularmovies.pojo.CastFilmography;
import com.griffin.popularmovies.pojo.Collection;
import com.griffin.popularmovies.pojo.Credits;
import com.griffin.popularmovies.pojo.MovieDetail;
import com.griffin.popularmovies.pojo.MovieImages;
import com.griffin.popularmovies.pojo.MoviePage;
import com.griffin.popularmovies.pojo.Person;
import com.griffin.popularmovies.pojo.ReviewPage;
import com.griffin.popularmovies.pojo.Trailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by griffin on 08/08/16.
 */

public interface MovieService {

    //Get movies list by choice
    @GET("movie/{choice}")
    Call<MoviePage> getMoviesPage(@Path("choice") String choice, @Query("api_key") String apiKey, @Query("language") String language, @Query
            ("page") int page);

    //Get the videos of a movies by Id
    @GET("movie/{id}/videos")
    Call<Trailer> getTrailer(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //get all the detail of a movie by Id
    @GET("movie/{id}")
    Call<MovieDetail> getMovieDetail(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //get casting by id
    @GET("movie/{id}/credits")
    Call<Credits> getCredits(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //get reviews by id
    @GET("movie/{id}/reviews")
    Call<ReviewPage> getReviews(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //Get the actor profile picture
    @GET("person/{id}")
    Call<Person> getPerson(@Path("id") int id, @Query("api_key") String apiKey);

    //Get the actor profile picture
    @GET("collection/{id}")
    Call<Collection> getCollection(@Path("id") double id, @Query("api_key") String apiKey, @Query("language") String language);

    //Get Filmography
    @GET("person/{id}/movie_credits")
    Call<CastFilmography> getFilmography(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    //Get Images Movie
    @GET("movie/{id}/images")
    Call<MovieImages> getMovieImages(@Path("id") int id, @Query("api_key") String apiKey);

    //Search by movie title
    @GET("search/movie")
    Call<MoviePage> getSearchMovie(@Query("api_key") String apiKey, @Query("query") String querySearch);
}
