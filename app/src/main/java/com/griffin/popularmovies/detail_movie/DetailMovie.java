package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

import com.griffin.popularmovies.Pojo.Collection;
import com.griffin.popularmovies.Pojo.Credits;
import com.griffin.popularmovies.Pojo.MovieDetail;
import com.griffin.popularmovies.Pojo.Part;
import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class DetailMovie implements Parcelable {

    private MovieDetail movieDetail;
    private List<TrailerDetail> trailerDetails;
    private Credits credits;
    private List<Reviews> reviewsList;
    private Collection collection;

   public DetailMovie(){
       movieDetail = new MovieDetail();
       credits = new Credits();
       trailerDetails = new ArrayList<>();
       reviewsList = new ArrayList<>();
       collection = new Collection();
   }

    protected DetailMovie(Parcel in) {
        movieDetail = in.readParcelable(MovieDetail.class.getClassLoader());
        trailerDetails = in.createTypedArrayList(TrailerDetail.CREATOR);
        credits = in.readParcelable(Credits.class.getClassLoader());
        reviewsList = in.createTypedArrayList(Reviews.CREATOR);
        collection = in.readParcelable(Collection.class.getClassLoader());
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(movieDetail, flags);
        dest.writeTypedList(trailerDetails);
        dest.writeParcelable(credits, flags);
        dest.writeTypedList(reviewsList);
        dest.writeParcelable(collection, flags);
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

    public List<Reviews> getReviewsList() {
        return reviewsList;
    }

    public void setReviewsList(List<Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    public MovieDetail getMovieDetail() {
        return movieDetail;
    }

    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
    }

    public List<TrailerDetail> getTrailerDetails() {
        return trailerDetails;
    }

    public void setTrailerDetails(List<TrailerDetail> trailerDetails) {
        this.trailerDetails = trailerDetails;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
