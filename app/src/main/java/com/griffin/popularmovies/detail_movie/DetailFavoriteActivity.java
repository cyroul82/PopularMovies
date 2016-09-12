package com.griffin.popularmovies.detail_movie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFavoriteActivity extends AppCompatActivity implements DetailFavoriteFragment.CallbackDetailFavoriteFragment {

    private static final String LOG_TAG = DetailFavoriteActivity.class.getSimpleName();
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar_detail_movie)
    Toolbar mToolbar;
    @BindView(R.id.floatingButton_favorite)
    FloatingActionButton mFavoriteButton;
    private DetailFavoriteFragment mDetailFavoriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        ButterKnife.bind(this);

        if(mFavoriteButton != null){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_unfavorite_black_24dp);
            mFavoriteButton.setImageBitmap(bitmap);
        }

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
            arguments.putBoolean(DetailFavoriteFragment.IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY, true);

            mDetailFavoriteFragment = new DetailFavoriteFragment();
            mDetailFavoriteFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, mDetailFavoriteFragment, MainActivity.DETAIL_FAVORITE_FRAGMENT_TAG)
                    .commit();
        }

        if (savedInstanceState != null) {
            mDetailFavoriteFragment = (DetailFavoriteFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, MainActivity.DETAIL_FAVORITE_FRAGMENT_TAG);
        }

        mFavoriteButton.setOnClickListener(mDetailFavoriteFragment);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager().putFragment(outState, MainActivity.DETAIL_FAVORITE_FRAGMENT_TAG, mDetailFavoriteFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDetailFavoriteFragment = (DetailFavoriteFragment) getSupportFragmentManager().getFragment(savedInstanceState, MainActivity
                .DETAIL_FAVORITE_FRAGMENT_TAG);
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

        ImageView imageView = (ImageView) findViewById(R.id.toolbar_image_detail_movie);

        try {
            if (imageView != null) {
                imageView.setImageBitmap(Utilities.getPoster(posterPath, idMovie));
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
