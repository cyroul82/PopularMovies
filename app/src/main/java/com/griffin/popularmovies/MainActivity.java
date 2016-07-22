package com.griffin.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.griffin.popularmovies.task.FetchMoviesTask;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements FavoriteMovieFragment.Callback, MovieListFragment.Callback, AdapterView.OnItemSelectedListener {

    private boolean mTwoPane;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String MOVIELISTFRAGMENT_TAG = "MLFTAG";
    private static final String FAVORITEMOVIEFRAGMENT_TAG = "FMFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Spinner spinner = (Spinner) findViewById(R.id.sort_order_spinner);
        spinner.setOnItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);



        if (findViewById(R.id.detail_movie_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null && !Utilities.getSortBy(this).equals(R.string.pref_movies_sorting_favorite_key)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_container, new DetailMovieFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
            else if (savedInstanceState == null && Utilities.getSortBy(this).equals(R.string.pref_movies_sorting_favorite_key)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_container, new DetailFavoriteMovieFragment(), FAVORITEMOVIEFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        setFragmentOnSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFragmentOnSharedPreferences();
    }

    private void setFragmentOnSharedPreferences(){
        String sortOrder =  Utilities.getSortBy(this);
        if(sortOrder.equals(getString(R.string.key_movies_favorite))) {
            setFavoriteListFragment();
        }
        else {
            setMovieListFragment();
        }
    }

    private void setFavoriteListFragment(){
        getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FavoriteMovieFragment(), FAVORITEMOVIEFRAGMENT_TAG)
                    .commit();

    }

    private void setMovieListFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MovieListFragment(), MOVIELISTFRAGMENT_TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /*Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
       if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFavoriteMovieFragment.DETAIL_URI, movieUri);
            DetailFavoriteMovieFragment detailFavoriteMovieFragment = new DetailFavoriteMovieFragment();
            detailFavoriteMovieFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailFavoriteMovieFragment, FAVORITEMOVIEFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailFavoriteMovieActivity.class).setData(movieUri);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(Movie movie) {

        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailMovieFragment.DETAIL_MOVIE, movie);

            DetailMovieFragment detailMovieFragment = new DetailMovieFragment();
            detailMovieFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailMovieFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailMovieActivity.class);
            intent.putExtra(getString(R.string.key_movies_list), movie);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = (String) parent.getItemAtPosition(position);
        if (selected == getString(R.string.key_movies_popular)) {
            Utilities.setSortOrder(this, getString(R.string.pref_movies_popular));
            setMovieListFragment();
        }

        if (selected == getString(R.string.key_movies_upcoming)) {
            Utilities.setSortOrder(this, getString(R.string.pref_movies_upcoming));
            setMovieListFragment();
        }
        if (selected == getString(R.string.key_movies_top_rated)) {
            Utilities.setSortOrder(this, getString(R.string.pref_movies_top_rated));
            setMovieListFragment();
        }

        if (selected == getString(R.string.key_movies_now_playing)) {
            Utilities.setSortOrder(this, getString(R.string.pref_movies_now_playing));
            setMovieListFragment();
        }

        if (selected == getString(R.string.key_movies_favorite)) {
            Utilities.setSortOrder(this, getString(R.string.pref_movies_favorite));
            setFavoriteListFragment();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
