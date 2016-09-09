
package com.griffin.popularmovies.pojo;

//import javax.annotation.Generated;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class ProductionCompany implements Parcelable{

    public static final Creator<ProductionCompany> CREATOR = new Creator<ProductionCompany>() {
        @Override
        public ProductionCompany createFromParcel(Parcel in) {
            return new ProductionCompany(in);
        }

        @Override
        public ProductionCompany[] newArray(int size) {
            return new ProductionCompany[size];
        }
    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private int id;

    protected ProductionCompany(Parcel in) {
        name = in.readString();
        id = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
    }
}
