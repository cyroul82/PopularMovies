package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

import com.griffin.popularmovies.ReviewMovie;

import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class ExtraDetailMovie implements Parcelable{


    private String[] mGenre;
    private List<CastingMovie> mActors;
    private List<TrailerMovie> mTrailerList;
    private List<ReviewMovie> mReviewMovieList;
    private String mRuntime;

    public ExtraDetailMovie(){

    }

    protected ExtraDetailMovie(Parcel in) {
        mGenre = in.createStringArray();
        /*mActors = new ArrayList<>();
        in.readList(mActors,null);
        mTrailerList = new ArrayList<>();
        in.readList(mTrailerList,null);*/
    }

    public static final Creator<ExtraDetailMovie> CREATOR = new Creator<ExtraDetailMovie>() {
        @Override
        public ExtraDetailMovie createFromParcel(Parcel in) {
            return new ExtraDetailMovie(in);
        }

        @Override
        public ExtraDetailMovie[] newArray(int size) {
            return new ExtraDetailMovie[size];
        }
    };

    public void setGenre(String[] mGenre){
        this.mGenre = mGenre;
    }
    public String[] getGenre(){
        return mGenre;
    }

    public void setActors(List<CastingMovie> mActors){
        this.mActors = mActors;
    }
    public List<CastingMovie> getCasting(){
        return mActors;
    }

    public void setTrailers(List<TrailerMovie> trailerList){
        this.mTrailerList = trailerList;
    }
    public List<TrailerMovie> getTrailers(){
        return mTrailerList;
    }

    public List<ReviewMovie> getReviews() {
        return mReviewMovieList;
    }

    public void setReviewMovieList(List<ReviewMovie> mReviewMovieList) {
        this.mReviewMovieList = mReviewMovieList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mGenre);
        //dest.writeList(mActors);
        //dest.writeList(mTrailerList);
    }


    public String getRuntime() {
        return mRuntime;
    }

    public void setRuntime(String mRuntime) {
        this.mRuntime = mRuntime;
    }
}
