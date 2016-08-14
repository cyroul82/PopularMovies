package com.griffin.popularmovies.detail_movie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.R;

public class DetailActivity extends AppCompatActivity {

    private final static String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            /*
                Create a bundle
                Get back the intent
                Get back the movie from the Intent

            */

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.MOVIE, getIntent().getParcelableExtra(getString(R.string.key_movies_list)));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, fragment, MainActivity.DETAIL_FRAGMENT_TAG)
                    .commit();
        }


    }


}
