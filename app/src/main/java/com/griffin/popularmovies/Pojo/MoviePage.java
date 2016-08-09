
package com.griffin.popularmovies.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class MoviePage implements Parcelable{

    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("results")
    @Expose
    private List<Movie> results = new ArrayList<Movie>();
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("total_results")
    @Expose
    private int totalResults;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MoviePage() {
    }

    /**
     * 
     * @param results
     * @param totalResults
     * @param page
     * @param totalPages
     */
    public MoviePage(int page, List<Movie> results, int totalPages, int totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    protected MoviePage(Parcel in) {
        page = in.readInt();
        results = in.createTypedArrayList(Movie.CREATOR);
        totalPages = in.readInt();
        totalResults = in.readInt();
    }

    public static final Creator<MoviePage> CREATOR = new Creator<MoviePage>() {
        @Override
        public MoviePage createFromParcel(Parcel in) {
            return new MoviePage(in);
        }

        @Override
        public MoviePage[] newArray(int size) {
            return new MoviePage[size];
        }
    };

    /**
     * 
     * @return
     *     The page
     */
    public int getPage() {
        return page;
    }

    /**
     * 
     * @param page
     *     The page
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 
     * @return
     *     The results
     */
    public List<Movie> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<Movie> results) {
        this.results = results;
    }

    /**
     * 
     * @return
     *     The totalPages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * 
     * @param totalPages
     *     The total_pages
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * 
     * @return
     *     The totalResults
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * 
     * @param totalResults
     *     The total_results
     */
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeTypedList(results);
        dest.writeInt(totalPages);
        dest.writeInt(totalResults);
    }
}
