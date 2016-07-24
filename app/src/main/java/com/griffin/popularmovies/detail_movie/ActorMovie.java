package com.griffin.popularmovies.detail_movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by griffin on 21/07/16.
 */
public class ActorMovie implements Parcelable{

    public String name;
    public String character;

    protected ActorMovie(Parcel in) {
        name = in.readString();
        character = in.readString();
    }

    public ActorMovie(){

    }

    public static final Creator<ActorMovie> CREATOR = new Creator<ActorMovie>() {
        @Override
        public ActorMovie createFromParcel(Parcel in) {
            return new ActorMovie(in);
        }

        @Override
        public ActorMovie[] newArray(int size) {
            return new ActorMovie[size];
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
