package com.griffin.popularmovies;

import java.util.List;

/**
 * Created by griffin on 21/07/16.
 */
public class DetailMovie {

    private String[] genre = null;
    private List<CreditsDetail> actors = null;

    public void setGenre(String[] genre){
        this.genre = genre;
    }
    public String[] getGenre(){
        return genre;
    }

    public void setActors(List<CreditsDetail> actors){
        this.actors = actors;
    }
    public List<CreditsDetail> getActors(){
        return actors;
    }
}
