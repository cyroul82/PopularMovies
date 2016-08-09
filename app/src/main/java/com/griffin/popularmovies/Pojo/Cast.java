
package com.griffin.popularmovies.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class Cast implements Parcelable{

    @SerializedName("cast_id")
    @Expose
    private int castId;
    @SerializedName("character")
    @Expose
    private String character;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("order")
    @Expose
    private int order;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    public Cast(){

    }

    protected Cast(Parcel in) {
        castId = in.readInt();
        character = in.readString();
        creditId = in.readString();
        id = in.readInt();
        name = in.readString();
        order = in.readInt();
        profilePath = in.readString();
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    /**
     * 
     * @return
     *     The castId
     */
    public int getCastId() {
        return castId;
    }

    /**
     * 
     * @param castId
     *     The cast_id
     */
    public void setCastId(int castId) {
        this.castId = castId;
    }

    /**
     * 
     * @return
     *     The character
     */
    public String getCharacter() {
        return character;
    }

    /**
     * 
     * @param character
     *     The character
     */
    public void setCharacter(String character) {
        this.character = character;
    }

    /**
     * 
     * @return
     *     The creditId
     */
    public String getCreditId() {
        return creditId;
    }

    /**
     * 
     * @param creditId
     *     The credit_id
     */
    public void setCreditId(String creditId) {
        this.creditId = creditId;
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
     *     The order
     */
    public int getOrder() {
        return order;
    }

    /**
     * 
     * @param order
     *     The order
     */
    public void setOrder(int order) {
        this.order = order;
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
        dest.writeInt(castId);
        dest.writeString(character);
        dest.writeString(creditId);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeString(profilePath);
    }
}
