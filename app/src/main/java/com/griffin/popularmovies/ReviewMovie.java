package com.griffin.popularmovies;

/**
 * Created by griffin on 26/07/16.
 */
public class ReviewMovie {

    public String author;
    public String review;

    public ReviewMovie(){

    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthor(){
        return author;
    }

    public void setReview(String review){
        this.review = review;
    }
    public String getReview(){
        return review;
    }
}
