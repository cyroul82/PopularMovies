package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class DetailMovie implements Parcelable {

    private String[] mGenre;
    private List<CastingMovie> mCasting;
    private List<TrailerMovie> mTrailerList;
    private List<ReviewMovie> mReviewMovieList;
    private String mRuntime;

    public DetailMovie(){

    }

    protected DetailMovie(Parcel in) {
        mGenre = in.createStringArray();
        mCasting = in.createTypedArrayList(CastingMovie.CREATOR);
        mTrailerList = in.createTypedArrayList(TrailerMovie.CREATOR);
        mReviewMovieList = in.createTypedArrayList(ReviewMovie.CREATOR);
        mRuntime = in.readString();
    }

    public static final Creator<DetailMovie> CREATOR = new Creator<DetailMovie>() {
        @Override
        public DetailMovie createFromParcel(Parcel in) {
            return new DetailMovie(in);
        }

        @Override
        public DetailMovie[] newArray(int size) {
            return new DetailMovie[size];
        }
    };

    public void setGenre(String[] mGenre){
        this.mGenre = mGenre;
    }
    public String[] getGenre(){
        return mGenre;
    }

    public void setCasting(List<CastingMovie> mActors){
        this.mCasting = mActors;
    }
    public List<CastingMovie> getCasting(){
        return mCasting;
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

    public String getRuntime() {
        return mRuntime;
    }

    public void setRuntime(String mRuntime) {
        this.mRuntime = mRuntime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mGenre);
        dest.writeTypedList(mCasting);
        dest.writeTypedList(mTrailerList);
        dest.writeTypedList(mReviewMovieList);
        dest.writeString(mRuntime);
    }
}
