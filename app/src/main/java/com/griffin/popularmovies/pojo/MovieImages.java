
package com.griffin.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class MovieImages implements Parcelable {

    public static final Creator<MovieImages> CREATOR = new Creator<MovieImages>() {
        @Override
        public MovieImages createFromParcel(Parcel in) {
            return new MovieImages(in);
        }

        @Override
        public MovieImages[] newArray(int size) {
            return new MovieImages[size];
        }
    };
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("backdrops")
    @Expose
    private List<Backdrop> backdrops = new ArrayList<Backdrop>();
    @SerializedName("posters")
    @Expose
    private List<Poster> posters = new ArrayList<Poster>();

    protected MovieImages(Parcel in) {
        id = in.readInt();
        backdrops = in.createTypedArrayList(Backdrop.CREATOR);
        posters = in.createTypedArrayList(Poster.CREATOR);
    }

    public MovieImages() {
    }

    /**
     * 
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The backdrops
     */
    public List<Backdrop> getBackdrops() {
        return backdrops;
    }

    /**
     * 
     * @param backdrops
     *     The backdrops
     */
    public void setBackdrops(List<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }

    /**
     * 
     * @return
     *     The posters
     */
    public List<Poster> getPosters() {
        return posters;
    }

    /**
     * 
     * @param posters
     *     The posters
     */
    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeTypedList(backdrops);
        dest.writeTypedList(posters);
    }
}
