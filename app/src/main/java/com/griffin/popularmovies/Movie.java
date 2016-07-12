package com.griffin.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by griffin on 08/07/16.
 */
public class Movie implements Parcelable {

    private int id = 0;
    private String title = null;
    private String overview = null;
    private String url = null;
    private String originalTitle = null;
    private String movieDate = null;
    private String movieRating = null;

    public Movie(){

    }

    public Movie(int id, String title, String overview, String url, String originalTitle, String movieDate, String movieRating) {
        this.id = id;
        this.title =title;
        this.overview = overview;
        this.url = url;
        this.originalTitle = originalTitle;
        this.movieDate = movieDate;
        this.movieRating = movieRating;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        url = in.readString();
        originalTitle = in.readString();
        movieDate = in.readString();
        movieRating = in.readString();
    }

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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieRating() {
        return movieRating;
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
        dest.writeString(url);
        dest.writeString(originalTitle);
        dest.writeString(movieDate);
        dest.writeString(movieRating);
    }


    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }

    };
}



