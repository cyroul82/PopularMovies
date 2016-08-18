package com.griffin.popularmovies.detail_movie;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.R;

public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_KEY = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Movie movie = getIntent().getParcelableExtra(MOVIE_KEY);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null && collapsingToolbar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbar.setTitle(movie.getTitle());

        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            /*
                Create a bundle
                Get back the intent
                Get back the movie from the Intent

            */

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.MOVIE, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, fragment, MainActivity.DETAIL_FRAGMENT_TAG)
                    .commit();

            loadBackdrop(movie.getPosterPath());
        }


    }

    private void loadBackdrop(String posterPath) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        if(imageView != null) {
            Glide.with(this).load(getString(R.string.IMAGE_BASE_URL) + posterPath).centerCrop().into(imageView);
        }
    }


}
