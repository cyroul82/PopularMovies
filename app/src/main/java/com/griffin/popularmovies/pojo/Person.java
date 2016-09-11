
package com.griffin.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class Person implements Parcelable {

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("also_known_as")
    @Expose
    private List<Object> alsoKnownAs = new ArrayList<Object>();
    @SerializedName("biography")
    @Expose
    private String biography;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("deathday")
    @Expose
    private String deathday;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place_of_birth")
    @Expose
    private String placeOfBirth;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    protected Person(Parcel in) {
        adult = in.readByte() != 0;
        biography = in.readString();
        birthday = in.readString();
        deathday = in.readString();
        homepage = in.readString();
        id = in.readInt();
        name = in.readString();
        placeOfBirth = in.readString();
        profilePath = in.readString();
    }

    /**
     * 
     * @return
     *     The adult
     */
    public boolean isAdult() {
        return adult;
    }

    /**
     * 
     * @param adult
     *     The adult
     */
    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    /**
     * 
     * @return
     *     The alsoKnownAs
     */
    public List<Object> getAlsoKnownAs() {
        return alsoKnownAs;
    }

    /**
     * 
     * @param alsoKnownAs
     *     The also_known_as
     */
    public void setAlsoKnownAs(List<Object> alsoKnownAs) {
        this.alsoKnownAs = alsoKnownAs;
    }

    /**
     * 
     * @return
     *     The biography
     */
    public String getBiography() {
        return biography;
    }

    /**
     * 
     * @param biography
     *     The biography
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * 
     * @return
     *     The birthday
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * 
     * @param birthday
     *     The birthday
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * 
     * @return
     *     The deathday
     */
    public String getDeathday() {
        return deathday;
    }

    /**
     * 
     * @param deathday
     *     The deathday
     */
    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    /**
     * 
     * @return
     *     The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * 
     * @param homepage
     *     The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
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
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The placeOfBirth
     */
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    /**
     * 
     * @param placeOfBirth
     *     The place_of_birth
     */
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    /**
     * 
     * @return
     *     The profilePath
     */
    public String getProfilePath() {
        return profilePath;
    }

    /**
     * 
     * @param profilePath
     *     The profile_path
     */
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(biography);
        dest.writeString(birthday);
        dest.writeString(deathday);
        dest.writeString(homepage);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(placeOfBirth);
        dest.writeString(profilePath);
    }
}
