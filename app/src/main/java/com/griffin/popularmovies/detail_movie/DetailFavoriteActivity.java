package com.griffin.popularmovies.detail_movie;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.Utilities;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFavoriteActivity extends AppCompatActivity {

    public static final String MOVIE_KEY = "movie";

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_detail_movie)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        ButterKnife.bind(this);

        //Get back the movie from the intent
        //Movie movie = getIntent().getParcelableExtra(MOVIE_KEY);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null && collapsingToolbarLayout != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFavoriteFragment.FAVORITE_MOVIE, getIntent().getData());

            DetailFavoriteFragment fragment = new DetailFavoriteFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, fragment, MainActivity.DETAIL_FAVORITE_FRAGMENT_TAG)
                    .commit();
        }

        //run the loadToolbarImage method to display the view, outside the main thread
        //loadToolbarImage(movie.getPosterPath());
    }

    //load the image using Glide library
    private void loadToolbarImage(Movie movie) {
        ImageView imageView = (ImageView) findViewById(R.id.toolbar_image_detail_movie);

        if (imageView != null) {
            imageView.setImageBitmap(Utilities.getPoster(movie.getPosterPath(), movie.getId()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
