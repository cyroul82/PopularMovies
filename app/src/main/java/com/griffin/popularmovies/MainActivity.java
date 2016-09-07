package com.griffin.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.griffin.popularmovies.adapter.CollectionAdapter;
import com.griffin.popularmovies.detail_movie.DetailActivity;
import com.griffin.popularmovies.detail_movie.DetailFavoriteActivity;
import com.griffin.popularmovies.detail_movie.DetailFavoriteFragment;
import com.griffin.popularmovies.detail_movie.DetailFragment;
import com.griffin.popularmovies.movie_list.BlankFragment;
import com.griffin.popularmovies.movie_list.FavoriteListFragment;
import com.griffin.popularmovies.movie_list.MovieListFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements FavoriteListFragment.Callback,
        MovieListFragment.CallbackMovieListFragment,
        CollectionAdapter.CallbackCollectionAdapter,
        DetailFavoriteFragment.CallbackFavorite {

    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    public static final String DETAIL_FAVORITE_FRAGMENT_TAG = "DFFT";
    public static final String MOVIE_LIST_FRAGMENT_TAG = "MLFTAG";
    public static final String FAVORITE_MOVIE_LIST_FRAGMENT_TAG = "FMFTAG";
    public static final String TITLE_BLANK_FRAGMENT_KEY = "title";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BLANK_FRAGMENT_TAG = "BFTAG";
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private boolean mTwoPane;
    private FavoriteListFragment favoriteListFragment;
    private MovieListFragment movieListFragment;
    private boolean isFavoriteFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //get back the choice
        int idItem = Utilities.getIdItem(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    int itemId = menuItem.getItemId();
                    switch (itemId) {
                        case R.id.drawer_popular: {
                            //Save the choice to the preferences
                            Utilities.setIdItem(getApplicationContext(), R.id.drawer_popular);
                            setMovieListFragment(itemId);

                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            onStart();
                            return true;
                        }
                        case R.id.drawer_top_rated: {
                            //Save the choice to the preferences
                            Utilities.setIdItem(getApplicationContext(), R.id.drawer_top_rated);
                            setMovieListFragment(itemId);

                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            onStart();
                            return true;
                        }
                        case R.id.drawer_upcoming: {
                            //Save the choice to the preferences
                            Utilities.setIdItem(getApplicationContext(), R.id.drawer_upcoming);
                            setMovieListFragment(itemId);

                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            onStart();
                            return true;
                        }
                        case R.id.drawer_this_week: {
                            //Save the choice to the preferences
                            Utilities.setIdItem(getApplicationContext(), R.id.drawer_this_week);
                            setMovieListFragment(itemId);

                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            onStart();
                            return true;
                        }
                        case R.id.drawer_favorite: {
                            //Save the choice to the preferences

                            Utilities.setIdItem(getApplicationContext(), R.id.drawer_favorite);

                            setFavoriteMovieListFragment(itemId);
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            onStart();
                            return true;
                        }
                        default: {
                            mDrawerLayout.closeDrawers();
                            return false;
                        }
                    }

                }
            });


            //set the navigation view to the choice saved in Preferences
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                MenuItem menuItem = navigationView.getMenu().getItem(i);

                if (idItem == menuItem.getItemId()) {
                    menuItem.setChecked(true);
                }

            }

        }

        // setup if using a tablet
        if (findViewById(R.id.detail_movie_container) != null) {
            // The detail container navigationView will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this navigationView is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail navigationView in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                setBlankFragment(Utilities.getIdItem(this));
            }
        } else {
            mTwoPane = false;
        }


        if (savedInstanceState == null) {

            //if choice equals favorite
            if (idItem == R.id.drawer_favorite) {
                //Create a new FavoriteListFragment Object
                setFavoriteMovieListFragment(idItem);
                isFavoriteFragment = true;
            }

            //then if choice is not equals favorite
            else {
                setMovieListFragment(idItem);
                isFavoriteFragment = false;
            }

        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_LIST_FRAGMENT_TAG)) {
                isFavoriteFragment = false;
                getMovieListFragment(savedInstanceState);
            }

            if (savedInstanceState.containsKey(FAVORITE_MOVIE_LIST_FRAGMENT_TAG)) {
                isFavoriteFragment = true;
                getFavoriteMovieListFragment(savedInstanceState);
            }
        }
    }

    //Set the movie List fragment with the choice from the navigation
    private void setMovieListFragment(int idItem) {
        //Create a new Object MovieListFragment
        movieListFragment = new MovieListFragment();
        String choice = Utilities.getChoice(idItem, this);
        Bundle args = new Bundle();
        args.putString(MovieListFragment.CHOICE, choice);
        //Set the arguments with the previous Bundle
        movieListFragment.setArguments(args);
        isFavoriteFragment = false;
        if (mTwoPane) {
            setBlankFragment(idItem);
        }
    }

    //Set the movie List fragment with the choice from the navigation
    private void setFavoriteMovieListFragment(int idItem) {
        //Create a new Object MovieListFragment
        favoriteListFragment = new FavoriteListFragment();

        isFavoriteFragment = true;
        if (mTwoPane) {
            setBlankFragment(idItem);
        }
    }

    //get back the movie list fragment from manager
    private void getMovieListFragment(Bundle savedInstanceState) {
        movieListFragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, MOVIE_LIST_FRAGMENT_TAG);
    }

    //get back the favorite list fragment from manager
    private void getFavoriteMovieListFragment(Bundle savedInstanceState) {
        favoriteListFragment = (FavoriteListFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                FAVORITE_MOVIE_LIST_FRAGMENT_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Fragment fragment_container = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment_container instanceof MovieListFragment) {
            getSupportFragmentManager().putFragment(outState, MOVIE_LIST_FRAGMENT_TAG, movieListFragment);
        }

        if (fragment_container instanceof FavoriteListFragment) {
            getSupportFragmentManager().putFragment(outState, FAVORITE_MOVIE_LIST_FRAGMENT_TAG, favoriteListFragment);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(MOVIE_LIST_FRAGMENT_TAG)) {
            isFavoriteFragment = false;
            getMovieListFragment(savedInstanceState);
        }

        if (savedInstanceState.containsKey(FAVORITE_MOVIE_LIST_FRAGMENT_TAG)) {
            isFavoriteFragment = true;
            getFavoriteMovieListFragment(savedInstanceState);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isFavoriteFragment) {
            //Get the fragmentManager and replace the movieListFragment to the fragment container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, movieListFragment, MOVIE_LIST_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();

        }
        if (isFavoriteFragment) {
            //Get the fragmentManager and replace the FavoriteListFragment to the fragment container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, favoriteListFragment, FAVORITE_MOVIE_LIST_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Create a new Object MovieListFragment
                movieListFragment = new MovieListFragment();
                String encodedString = null;
                try {
                    encodedString = URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                Bundle args = new Bundle();
                args.putString(MovieListFragment.SEARCH, encodedString);
                //Set the arguments with the previous Bundle
                movieListFragment.setArguments(args);
                isFavoriteFragment = false;

                onStart();

                if (mTwoPane) {
                    setBlankFragment(R.string.search_title);
                }
                //Collapse the search View
                invalidateOptionsMenu();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
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
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFavoriteFragment.FAVORITE_MOVIE, movieUri);
            args.putBoolean(DetailFavoriteFragment.IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY, false);
            DetailFavoriteFragment detailFavoriteFragment = new DetailFavoriteFragment();
            detailFavoriteFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailFavoriteFragment, FAVORITE_MOVIE_LIST_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailFavoriteActivity.class).setData(movieUri);
            startActivity(intent);

        }
    }

    @Override
    public void onItemSelected(int idMovie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putInt(DetailFragment.DETAIL_MOVIE, idMovie);
            args.putBoolean(DetailFragment.IS_DETAIL_FRAGMENT_FROM_ACTIVITY, false);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .addToBackStack(Integer.toString(idMovie))
                    .commit();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.MOVIE_KEY, idMovie);
            startActivity(intent);
        }
    }

    private void setBlankFragment(int idItem) {
        String choice = Utilities.getChoice(idItem, this);
        Bundle b = new Bundle();
        b.putString(TITLE_BLANK_FRAGMENT_KEY, choice);
        BlankFragment blankFragment = new BlankFragment();
        blankFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_movie_container, blankFragment, BLANK_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCollectionMovieClicked(int idMovie) {
        onItemSelected(idMovie);
    }

    @Override
    public void onClickFavoriteMovie(int idMovie) {
        Utilities.removeMovieFromFavorite(idMovie, getApplicationContext());
        if (mTwoPane) {
            setBlankFragment(R.id.drawer_favorite);
        }
    }
}
