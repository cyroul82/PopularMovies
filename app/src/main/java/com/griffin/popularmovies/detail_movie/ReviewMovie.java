package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by griffin on 26/07/16.
 */
public class ReviewMovie implements Parcelable {

    public String author;
    public String review;

    public ReviewMovie(){

    }

    protected ReviewMovie(Parcel in) {
        author = in.readString();
        review = in.readString();
    }

    public static final Creator<ReviewMovie> CREATOR = new Creator<ReviewMovie>() {
        @Override
        public ReviewMovie createFromParcel(Parcel in) {
            return new ReviewMovie(in);
        }

        @Override
        public ReviewMovie[] newArray(int size) {
            return new ReviewMovie[size];
        }
    };

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthor(){
        return author;
    }

    public void setReview(String review){
        this.review = review;
    }
    public String getReview(){
        return review;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(review);
    }
}
