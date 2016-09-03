package com.griffin.popularmovies.detail_movie;

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

public class DetailFavoriteActivity extends AppCompatActivity implements DetailFavoriteFragment.CallbackDetailFavoriteFragment {

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar_detail_movie)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        ButterKnife.bind(this);

        //Get back the movie from the intent
        //Movie movie = getIntent().getParcelableExtra(MOVIE_KEY);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null && mCollapsingToolbarLayout != null) {
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

    @Override
    public void setTitleAndPosterOnActivity(String title, String posterPath, int idMovie) {

        mCollapsingToolbarLayout.setTitle(title);

        //ImageView imageView = (ImageView) findViewById(R.id.toolbar_image_detail_movie);
        //imageView.setImageBitmap(Utilities.getPoster(posterPath, idMovie));

    }
}
