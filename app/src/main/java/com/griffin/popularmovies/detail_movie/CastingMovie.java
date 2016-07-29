package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class CastingMovie implements Parcelable{

    public String name;
    public String character;

    protected CastingMovie(Parcel in) {
        name = in.readString();
        character = in.readString();
    }

    public CastingMovie(){
    }

    public static final Creator<CastingMovie> CREATOR = new Creator<CastingMovie>() {
        @Override
        public CastingMovie createFromParcel(Parcel in) {
            return new CastingMovie(in);
        }

        @Override
        public CastingMovie[] newArray(int size) {
            return new CastingMovie[size];
        }
    };

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setCharacter (String character){
        this.character = character;
    }
    public String getCharacter(){
        return character;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(character);
    }
}
