package com.griffin.popularmovies.Service;

import com.griffin.popularmovies.Pojo.CastFilmography;
import com.griffin.popularmovies.Pojo.Collection;
import com.griffin.popularmovies.Pojo.Credits;
import com.griffin.popularmovies.Pojo.MovieDetail;
import com.griffin.popularmovies.Pojo.MovieImages;
import com.griffin.popularmovies.Pojo.MoviePage;
import com.griffin.popularmovies.Pojo.Part;
import com.griffin.popularmovies.Pojo.Person;
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
