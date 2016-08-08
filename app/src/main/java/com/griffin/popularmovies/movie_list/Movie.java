package com.griffin.popularmovies.movie_list;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.griffin.popularmovies.detail_movie.DetailMovie;

/**
 * Created by griffin on 08/07/16.
 */
public class Movie implements Parcelable {

    private int id = 0;
    private String title = null;
    private String overview = null;
    private String picture_url = null;
    private String originalTitle = null;
    private String movieDate = null;
    private String movieRating = null;
    private int isFavorite ;
    private DetailMovie detailMovie;

    public Movie(){
        detailMovie = new DetailMovie();

    }

    public Movie(int id, String picture_url){
        this.id = id;
        this.picture_url = picture_url;
    }

    public Movie(int id, String title, String overview, String picture_url, String originalTitle, String movieDate, String movieRating, int
            isFavorite) {
        this.id = id;
        this.title =title;
        this.overview = overview;
        this.picture_url = picture_url;
        this.originalTitle = originalTitle;
        this.movieDate = movieDate;
        this.movieRating = movieRating;
        this.isFavorite = isFavorite;
    }


    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        picture_url = in.readString();
        originalTitle = in.readString();
        movieDate = in.readString();
        movieRating = in.readString();
        isFavorite = in.readInt();
        detailMovie = in.readParcelable(DetailMovie.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public String getDate() {
        return movieDate;
    }

    public void setRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getRating() {
        return movieRating;
    }




    public int getFavorite() {
        return isFavorite;
    }

    public void setFavorite(int favorite) {
        isFavorite = favorite;
    }

    public DetailMovie getDetailMovie() {
        return detailMovie;
    }

    public void setDetail(DetailMovie detailMovie) {
        this.detailMovie = detailMovie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(picture_url);
        dest.writeString(originalTitle);
        dest.writeString(movieDate);
        dest.writeString(movieRating);
        dest.writeInt(isFavorite);
        dest.writeParcelable(detailMovie, flags);
    }
}



