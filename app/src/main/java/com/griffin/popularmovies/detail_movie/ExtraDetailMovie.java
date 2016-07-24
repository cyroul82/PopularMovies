package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class ExtraDetailMovie implements Parcelable{


    private String[] genre;
    private List<ActorMovie> actors;
    private List<TrailerMovie> trailerList;

    public ExtraDetailMovie(){

    }

    protected ExtraDetailMovie(Parcel in) {
        genre = in.createStringArray();
        actors = new ArrayList<>();
        in.readList(actors,null);
        trailerList = new ArrayList<>();
        in.readList(trailerList,null);
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

    public void setTrailers(List<TrailerMovie> trailerList){
        this.trailerList = trailerList;
    }
    public List<TrailerMovie> getTrailers(){
        return trailerList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(genre);
        dest.writeList(actors);
        //dest.writeList(trailerList);
    }
}
