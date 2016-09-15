package com.griffin.popularmovies.detail_movie;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utility;
import com.griffin.popularmovies.adapter.FilmographyAdapter;
import com.griffin.popularmovies.pojo.Cast;
import com.griffin.popularmovies.pojo.CastFilmography;
import com.griffin.popularmovies.pojo.CastFilmographyDetail;
import com.griffin.popularmovies.task.FetchFilmographyTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CastFilmography> {

    public static final String CAST_KEY = "cast_key";
    private static final String CAST = "cast";
    private static final String MOVIE_LIST = "movie_list";

    private static final int LOADER = 0;
    @BindView(R.id.cast_birthday)
    TextView mBirthday;
    @BindView(R.id.cast_deathday)
    TextView mDeathday;
    @BindView(R.id.cast_biography)
    TextView mBiography;
    @BindView(R.id.cast_home_page)
    TextView mHomePage;
    @BindView(R.id.cast_imageView)
    ImageView mImageView;
    @BindView(R.id.toolbar_cast)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView_filmography)
    RecyclerView mRecyclerViewFilmography;
    private FilmographyAdapter mFilmographyAdapter;
    private ArrayList<CastFilmographyDetail> mCastMovieList = new ArrayList<>();
    private Cast mCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cast);

        ButterKnife.bind(this);


        if (savedInstanceState == null) {
            mCast = getIntent().getParcelableExtra(CAST_KEY);

            getSupportLoaderManager().initLoader(LOADER, null, this).forceLoad();

        } else {
            mCast = savedInstanceState.getParcelable(CAST);
            mCastMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
        }

        mRecyclerViewFilmography.setHasFixedSize(true);

        // use a grid layout manager , create the *** REVIEWS *** adapter and set it up to the recycler View
        GridLayoutManager gridLayoutManagerFilmography = new GridLayoutManager(this, Utility.calculateNoOfColumns(getApplicationContext()));
        gridLayoutManagerFilmography.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerViewFilmography.setLayoutManager(gridLayoutManagerFilmography);

        mFilmographyAdapter = new FilmographyAdapter(mCastMovieList, this);
        mRecyclerViewFilmography.setAdapter(mFilmographyAdapter);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mCast.getName());
        }

        mBirthday.setText(mCast.getPerson().getBirthday());
        mDeathday.setText(mCast.getPerson().getDeathday());
        mBiography.setText(mCast.getPerson().getBiography());
        mHomePage.setText(mCast.getPerson().getHomepage());
        //load the picture using picasso library
        Picasso.with(this)
                .load(getString(R.string.IMAGE_BASE_URL) + mCast.getPerson().getProfilePath())
                .placeholder(R.drawable.ic_wallpaper_black_48dp)
                .error(R.drawable.ic_wallpaper_black_48dp)
                .fit()
                .centerInside()
                .into(mImageView);

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CAST, mCast);
        outState.putParcelableArrayList(MOVIE_LIST, mCastMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<CastFilmography> onCreateLoader(int id, Bundle args) {
        return new FetchFilmographyTask(getApplicationContext(), mCast.getPerson().getId());
    }

    @Override
    public void onLoadFinished(Loader<CastFilmography> loader, CastFilmography data) {
        if (data != null) {
            if (data.getCast() != null) {
                mCastMovieList.clear();
                mCastMovieList.addAll(data.getCast());
                mFilmographyAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CastFilmography> loader) {

    }

}
