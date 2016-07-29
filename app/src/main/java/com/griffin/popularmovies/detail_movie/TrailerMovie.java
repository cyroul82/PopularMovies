package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by griffin on 24/07/16.
 */
public class TrailerMovie implements Parcelable {

    private String keyTrailer;
    private String nameTrailer;
    private String siteTrailer;
    private String type;

    public TrailerMovie(){

    }


    protected TrailerMovie(Parcel in) {
        keyTrailer = in.readString();
        nameTrailer = in.readString();
        siteTrailer = in.readString();
        type = in.readString();
    }

    public static final Creator<TrailerMovie> CREATOR = new Creator<TrailerMovie>() {
        @Override
        public TrailerMovie createFromParcel(Parcel in) {
            return new TrailerMovie(in);
        }

        @Override
        public TrailerMovie[] newArray(int size) {
            return new TrailerMovie[size];
        }
    };

    public String getKeyTrailer() {
        return keyTrailer;
    }

    public void setKeyTrailer(String keyTrailer) {
        this.keyTrailer = keyTrailer;
    }

    public String getNameTrailer() {
        return nameTrailer;
    }

    public void setNameTrailer(String nameTrailer) {
        this.nameTrailer = nameTrailer;
    }

    public String getSiteTrailer() {
        return siteTrailer;
    }

    public void setSiteTrailer(String siteTrailer) {
        this.siteTrailer = siteTrailer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyTrailer);
        dest.writeString(nameTrailer);
        dest.writeString(siteTrailer);
        dest.writeString(type);
    }
}
