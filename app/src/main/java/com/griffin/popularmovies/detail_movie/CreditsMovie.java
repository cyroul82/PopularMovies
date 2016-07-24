package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class CreditsMovie implements Parcelable{


    private String[] genre;
    private List<ActorMovie> actors;

    public CreditsMovie(){
        actors = new ArrayList<>();

    }

    protected CreditsMovie(Parcel in) {
        genre = in.createStringArray();
        actors = new ArrayList<>();
        in.readList(actors,null);
    }

    public static final Creator<CreditsMovie> CREATOR = new Creator<CreditsMovie>() {
        @Override
        public CreditsMovie createFromParcel(Parcel in) {
            return new CreditsMovie(in);
        }

        @Override
        public CreditsMovie[] newArray(int size) {
            return new CreditsMovie[size];
        }
    };

    public void setGenre(String[] genre){
        this.genre = genre;
    }
    public String[] getGenre(){
        return genre;
    }

    public void setActors(List<ActorMovie> actors){
        this.actors = actors;
    }
    public List<ActorMovie> getActors(){
        return actors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(genre);
        dest.writeList(actors);
    }
}
