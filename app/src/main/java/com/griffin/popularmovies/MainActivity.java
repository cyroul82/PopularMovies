package com.griffin.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.griffin.popularmovies.movie_list.BlankFragment;
import com.griffin.popularmovies.movie_list.FavoriteListFragment;
import com.griffin.popularmovies.movie_list.MovieListFragment;
import com.griffin.popularmovies.Pojo.Movie;

import java.util.List;


public class MainActivity extends AppCompatActivity implements FavoriteListFragment.Callback, MovieListFragment.Callback, DetailFavoriteFragment
        .Callback  {

    private boolean mTwoPane;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    public static final String DETAIL_FAVORITE_FRAGMENT_TAG="DFFT";

    public static final String MOVIE_LIST_FRAGMENT_TAG = "MLFTAG";
    public static final String FAVORITE_MOVIE_LIST_FRAGMENT_TAG = "FMFTAG";

    private static final String BLANK_FRAGMENT_TAG = "BFTAG";


    private FavoriteListFragment favoriteListFragment;
    private MovieListFragment movieListFragment;

    private Spinner spinner;
    private int mSpinnerPosition;

    public static final String TITLE_BLANK_FRAGMENT_KEY = "title";

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST_FRAGMENT_TAG)){
            movieListFragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, MOVIE_LIST_FRAGMENT_TAG);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(FAVORITE_MOVIE_LIST_FRAGMENT_TAG)){
            favoriteListFragment = (FavoriteListFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                    FAVORITE_MOVIE_LIST_FRAGMENT_TAG);
        }

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        spinner = (Spinner) findViewById(R.id.sort_order_spinner);
        spinner.setSelection(Utilities.getSelectedChoiceNumber(this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected  = (String)parent.getSelectedItem();

                if(!selected.equals(Utilities.getSelectedChoice(getApplicationContext()))){
                    Utilities.setChoice(getApplicationContext(), selected);
                    if(mTwoPane) setBlankFragment();
                    if(!selected.equals(getString(R.string.pref_movies_favorite))) {
                        movieListFragment = new MovieListFragment();
                    }

                    setListFragmentOnSharedPreferences();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        if (view != null) {
            view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                    Snackbar.make(mDrawerLayout, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });
        }


        if (findViewById(R.id.detail_movie_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                setBlankFragment();
            }
        }
        else {
            mTwoPane = false;
        }
        setListFragmentOnSharedPreferences();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment instanceof MovieListFragment){
                getSupportFragmentManager().putFragment(outState, MOVIE_LIST_FRAGMENT_TAG, movieListFragment);
            }
            if(fragment instanceof FavoriteListFragment){
                getSupportFragmentManager().putFragment(outState, FAVORITE_MOVIE_LIST_FRAGMENT_TAG, favoriteListFragment);
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for(Fragment fragment : fragments){
            if(fragment instanceof MovieListFragment){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, movieListFragment, MOVIE_LIST_FRAGMENT_TAG)
                        .commit();

            }
            if(fragment instanceof FavoriteListFragment){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, favoriteListFragment, FAVORITE_MOVIE_LIST_FRAGMENT_TAG)
                        .commit();
            }
        }

        if(mTwoPane) {
            Fragment detail_fragment = getSupportFragmentManager().findFragmentById(R.id.detail_movie_container);

            if (detail_fragment instanceof DetailFavoriteFragment) {
                DetailFavoriteFragment detailFavoriteFragment = (DetailFavoriteFragment) getSupportFragmentManager().findFragmentByTag
                        (DETAIL_FAVORITE_FRAGMENT_TAG);

                if (detailFavoriteFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, detailFavoriteFragment,
                            DETAIL_FAVORITE_FRAGMENT_TAG).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, new DetailFavoriteFragment(),
                            DETAIL_FAVORITE_FRAGMENT_TAG).commit();
                }
            }

            if (detail_fragment instanceof DetailFragment) {
                DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

                if (detailFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, detailFragment,
                            DETAIL_FRAGMENT_TAG).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                            .commit();
                }
            }

            if (detail_fragment instanceof BlankFragment) {
                BlankFragment blankFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(BLANK_FRAGMENT_TAG);

                if (blankFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, blankFragment,
                            BLANK_FRAGMENT_TAG).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, new BlankFragment(),
                            BLANK_FRAGMENT_TAG).commit();
                }
            }
            spinner.setSelection(Utilities.getSelectedChoiceNumber(this));
        }
    }



    private void setListFragmentOnSharedPreferences(){
            String choice = Utilities.getChoice(this);

            if (choice.equals(getString(R.string.pref_movies_favorite))) {
                if(favoriteListFragment == null) {
                    favoriteListFragment = new FavoriteListFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, favoriteListFragment, FAVORITE_MOVIE_LIST_FRAGMENT_TAG)
                        .commit();
            }
            else {
                if(movieListFragment == null){
                    movieListFragment = new MovieListFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, movieListFragment, MOVIE_LIST_FRAGMENT_TAG)
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

        switch (item.getItemId()) {
            case R.id.action_settings :
                /*Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
            return true;*/
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
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
            args.putParcelable(DetailFragment.MOVIE, movie);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(getString(R.string.key_movies_list), movie);
            startActivity(intent);
        }
    }


    //Remove the movie from the favorite list
    @Override
    public void onFavoriteMovieClick(Movie movie, Context context) {
        setBlankFragment();
    }

    private void setBlankFragment(){
        Bundle b = new Bundle();
        b.putString(TITLE_BLANK_FRAGMENT_KEY, Utilities.getSelectedChoice(this));
        BlankFragment bf= new BlankFragment();
        bf.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_movie_container, bf, BLANK_FRAGMENT_TAG)
                .commit();
    }

}
