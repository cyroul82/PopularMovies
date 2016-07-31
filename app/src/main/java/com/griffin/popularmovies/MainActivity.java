package com.griffin.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.griffin.popularmovies.detail_movie.DetailFavoriteActivity;
import com.griffin.popularmovies.detail_movie.DetailFavoriteFragment;
import com.griffin.popularmovies.detail_movie.DetailActivity;
import com.griffin.popularmovies.detail_movie.DetailFragment;
import com.griffin.popularmovies.movie_list.FavoriteListFragment;
import com.griffin.popularmovies.movie_list.Movie;
import com.griffin.popularmovies.movie_list.MovieListFragment;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements FavoriteListFragment.Callback, MovieListFragment.Callback, DetailFavoriteFragment
        .Callback,AdapterView.OnItemSelectedListener {

    private boolean mTwoPane;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private static final String DETAIL_FAVORITE_FRAGMENT_TAG="DFFT";

    private static final String MOVIE_LIST_FRAGMENT_TAG = "MLFTAG";
    private static final String FAVORITE_MOVIE_LIST_FRAGMENT_TAG = "FMFTAG";

    private static final String BLANK_FRAGMENT_TAG = "BFTAG";

    private Spinner spinner;
    public static final int POPULAR_CHOICE = 1;
    public static final int TOP_RATED_CHOICE = 2;
    public static final int UPCOMING_CHOICE = 3;
    public static final int NOW_PLAYING_CHOICE = 4;
    public static final int FAVORITE_CHOICE = 5;
    public static final String USER_CHOICE= "user_choice";

    public static final String TITLE_BLANK_FRAGMENT_KEY = "title";
    private String mTitleBlankFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinner = (Spinner) findViewById(R.id.sort_order_spinner);
        spinner.setOnItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int spinnerValue = sharedPrefs.getInt(USER_CHOICE, POPULAR_CHOICE);

        if (findViewById(R.id.detail_movie_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null && spinnerValue != FAVORITE_CHOICE) {
                setBlankFragment();


            }
            if(savedInstanceState == null && spinnerValue == FAVORITE_CHOICE) {
                setBlankFragment();

            }
        }
        else {
            mTwoPane = false;
        }

        setListFragmentOnSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* FavoriteListFragment favoriteListFragment = (FavoriteListFragment) getSupportFragmentManager().findFragmentById(R.id
                .fragment_container);*/

       // BlankFragment blankFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(BLANK_FRAGMENT_TAG);

      // DetailFavoriteFragment detailFavoriteFragment = (DetailFavoriteFragment) getSupportFragmentManager().findFragmentByTag
             //   (DETAIL_FAVORITE_FRAGMENT_TAG);

       /* MovieListFragment movieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);*/
       //DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

    }

    private void setListFragmentOnSharedPreferences(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int spinnerValue = sharedPrefs.getInt(USER_CHOICE, -1);
        String sortOrder = Utilities.getOrder(this, spinnerValue);
        if(sortOrder.equals(getString(R.string.pref_movies_favorite))) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FavoriteListFragment(), FAVORITE_MOVIE_LIST_FRAGMENT_TAG)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MovieListFragment(), MOVIE_LIST_FRAGMENT_TAG)
                    .commit();
        }
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
            args.putParcelable(DetailFavoriteFragment.DETAIL_URI, movieUri);
            DetailFavoriteFragment detailFavoriteFragment = new DetailFavoriteFragment();
            detailFavoriteFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailFavoriteFragment, FAVORITE_MOVIE_LIST_FRAGMENT_TAG)
                    .commit();
        }
        else {
           Intent intent = new Intent(this, DetailFavoriteActivity.class).setData(movieUri);
           startActivity(intent);

        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(getString(R.string.key_movies_list), movie);
            startActivity(intent);
        }
    }

    //sets the shared preference on the spinner choice TODO : arrange this !!!
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();


        String selected = (String) parent.getItemAtPosition(position);
        if (selected.equals(getString(R.string.key_movies_popular))) {
            editor.putInt(USER_CHOICE,POPULAR_CHOICE);
            editor.commit();
        }

        if (selected.equals(getString(R.string.key_movies_upcoming))) {
            editor.putInt(USER_CHOICE,UPCOMING_CHOICE);
            editor.commit();
        }
        if (selected.equals(getString(R.string.key_movies_top_rated))) {
            editor.putInt(USER_CHOICE, TOP_RATED_CHOICE);
            editor.commit();
        }

        if (selected.equals(getString(R.string.key_movies_now_playing))) {
            editor.putInt(USER_CHOICE, NOW_PLAYING_CHOICE);
            editor.commit();
        }

        if (selected.equals(getString(R.string.key_movies_favorite))) {
            editor.putInt(USER_CHOICE, FAVORITE_CHOICE);
            editor.commit();
        }

        setListFragmentOnSharedPreferences();
        setBlankFragment();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Remove the movie from the favorite list
    @Override
    public void onFavoriteMovieClick(Movie movie, Context context) {
        Utilities.removeMovie(movie, context);
    }

    private void setBlankFragment(){
        Bundle b = new Bundle();
        b.putString(TITLE_BLANK_FRAGMENT_KEY, getString(R.string.pref_movies_favorite));
        BlankFragment bf= new BlankFragment();
        bf.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_movie_container, bf, BLANK_FRAGMENT_TAG)
                .commit();
    }

}
