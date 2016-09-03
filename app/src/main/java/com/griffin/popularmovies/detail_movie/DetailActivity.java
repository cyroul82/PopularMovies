package com.griffin.popularmovies.detail_movie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.Pojo.Poster;
import com.griffin.popularmovies.R;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements DetailFragment.CallbackDetailFragment {

    public static final String MOVIE_KEY = "movie";
    public static final String DETAIL_FRAGMENT_TAG = "dft";


    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar_detail_movie)
    Toolbar mToolbar;
    @BindView(R.id.floatingButton_favorite)
    FloatingActionButton mFavoriteButton;
    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        ButterKnife.bind(this);

        //Get back the movie from the intent
        int idMovie = getIntent().getIntExtra(MOVIE_KEY, 0);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null && mCollapsingToolbarLayout != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            arguments.putInt(DetailFragment.DETAIL_MOVIE, idMovie);
            arguments.putBoolean(DetailFragment.IS_DETAIL_FRAGMENT_FROM_ACTIVITY, true);

            mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, mDetailFragment, MainActivity.DETAIL_FRAGMENT_TAG)
                    .addToBackStack(Integer.toString(idMovie))
                    .commit();

        }

        if (savedInstanceState != null) {
            mDetailFragment = (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, DETAIL_FRAGMENT_TAG);
        }

        mFavoriteButton.setOnClickListener(mDetailFragment);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_TAG, mDetailFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDetailFragment = (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, DETAIL_FRAGMENT_TAG);
    }


    //load the image using Glide library
    private void loadToolbarImage(String posterPath) {
        ImageView imageView = (ImageView) findViewById(R.id.toolbar_image_detail_movie);

        if(imageView != null) {
            Picasso.with(this).load(getString(R.string.IMAGE_BASE_URL) + posterPath).fit().centerInside().into(imageView);
        }
    }

    //load the image using Picasso library
    private void loadToolbarCarousel(final List<Poster> posterList) {
        /*final CarouselView mCarouselView = (CarouselView) findViewById(R.id.carouselView);
        if(mCarouselView != null) {
            mCarouselView.setPageCount(posterList.size());


            mCarouselView.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(int position) {

                    View customView = getLayoutInflater().inflate(R.layout.custom_images_movie_view, null);

                    ImageView fruitImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

                    Picasso.with(getApplicationContext())
                            .load(getString(R.string.IMAGE_BASE_URL) + posterList.get(position).getFilePath())
                            .fit()
                            .centerCrop().into
                            (fruitImageView);

                    mCarouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

                    return customView;


                }
            });

            mCarouselView.setSlideInterval(4000);

        }*/

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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitleAndPosterOnActivity(String title, String posterPath, List<Poster> posterList) {

        mCollapsingToolbarLayout.setTitle(title);
        //run the loadToolbarImage method to display the view, outside the main thread
        loadToolbarImage(posterList.get(0).getFilePath());
        //loadToolbarCarousel(posterList);
    }

}
