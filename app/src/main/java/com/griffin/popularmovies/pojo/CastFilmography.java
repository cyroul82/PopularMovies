
package com.griffin.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class CastFilmography implements Parcelable {

    public static final Creator<CastFilmography> CREATOR = new Creator<CastFilmography>() {
        @Override
        public CastFilmography createFromParcel(Parcel in) {
            return new CastFilmography(in);
        }

        @Override
        public CastFilmography[] newArray(int size) {
            return new CastFilmography[size];
        }
    };
    @SerializedName("cast")
    @Expose
    private List<CastFilmographyDetail> cast = new ArrayList<>();
    @SerializedName("crew")
    @Expose
    private List<Crew> crew = new ArrayList<>();
    @SerializedName("id")
    @Expose
    private int id;

    protected CastFilmography(Parcel in) {
        cast = in.createTypedArrayList(CastFilmographyDetail.CREATOR);
        crew = in.createTypedArrayList(Crew.CREATOR);
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cast);
        dest.writeTypedList(crew);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 
     * @return
     *     The cast
     */
    public List<CastFilmographyDetail> getCast() {
        return cast;
    }

    /**
     * 
     * @param cast
     *     The cast
     */
    public void setCast(List<CastFilmographyDetail> cast) {
        this.cast = cast;
    }

    /**
     * 
     * @return
     *     The crew
     */
    public List<Crew> getCrew() {
        return crew;
    }

    /**
     * 
     * @param crew
     *     The crew
     */
    public void setCrew(List<Crew> crew) {
        this.crew = crew;
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

}
