package com.griffin.popularmovies.detail_movie;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Pojo.Movie;

public class DetailFavoriteActivity extends AppCompatActivity implements DetailFavoriteFragment
.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailMovie);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFavoriteFragment.DETAIL_URI, getIntent().getData());

            DetailFavoriteFragment fragment = new DetailFavoriteFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, fragment)
                    .commit();
        }
  }

    @Override
    public void onFavoriteMovieClick(Movie movie, Context context) {
        onBackPressed();
    }
}
