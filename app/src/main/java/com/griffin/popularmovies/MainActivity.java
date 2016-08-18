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

    private boolean isFavoriteFragment = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        spinner = (Spinner) findViewById(R.id.sort_order_spinner);

        //Set the spinner with the preferences choice, saved from the previous use of the app or to  popular by default
        spinner.setSelection(Utilities.getSelectedChoiceNumber(this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected  = (String)parent.getSelectedItem();

                /*  if the selected choice is different from the previous one,
                     then we save the selected into preferences
                     set the "right panel" on tablet to a new new Blank Fragment

                */

                if(!selected.equals(Utilities.getSelectedChoice(getApplicationContext()))){
                    //Save the choice to the preferences
                    Utilities.setChoice(getApplicationContext(), selected);

                    //if selected choice equals Favorite
                    if(selected.equals(getResources().getString(R.string.key_movies_favorite))){
                        //Create a new Object FavoriteListFragment
                        favoriteListFragment = new FavoriteListFragment();
                        isFavoriteFragment = true;
                    }
                    //else if selected choice is different than Favorite
                    else{
                        //Create a new Object MovieListFragment
                        movieListFragment = new MovieListFragment();
                        Bundle args = new Bundle();
                        args.putString(MovieListFragment.CHOICE, Utilities.getChoice(getApplicationContext()));
                        //Set the arguments with the previous Bundle
                        movieListFragment.setArguments(args);
                        isFavoriteFragment = false;
                    }

                    //if on Tablet
                    if(mTwoPane) {
                        //set up a blank fragment on the right panel as presentation page !!!
                        setBlankFragment();
                    }

                    onResume();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailMovie);
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


        if(savedInstanceState == null){

            //get back the choice
            String choice = Utilities.getChoice(this);
            //if choice equals favorite
            if(choice.equals(getString(R.string.pref_movies_favorite))){
                //Create a new FavoriteListFragment Object
                favoriteListFragment = new FavoriteListFragment();
                isFavoriteFragment = true;
            }

            //then if choice is not equals favorite
            else {
                //create a bundle and add the choice
                Bundle args = new Bundle();
                args.putString(MovieListFragment.CHOICE, choice);

                //Create a new movieListFragment Object
                movieListFragment = new MovieListFragment();
                //Set the arguments with the previous Bundle
                movieListFragment.setArguments(args);
                isFavoriteFragment = false;
            }

        }

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(MOVIE_LIST_FRAGMENT_TAG)){
                isFavoriteFragment = false;
                movieListFragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, MOVIE_LIST_FRAGMENT_TAG);
            }

            if(savedInstanceState.containsKey(FAVORITE_MOVIE_LIST_FRAGMENT_TAG)){
                isFavoriteFragment = true;
                favoriteListFragment = (FavoriteListFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                        FAVORITE_MOVIE_LIST_FRAGMENT_TAG);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment fragment_container = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(fragment_container instanceof MovieListFragment){
            getSupportFragmentManager().putFragment(outState, MOVIE_LIST_FRAGMENT_TAG, movieListFragment);
        }

        if(fragment_container instanceof FavoriteListFragment){
            getSupportFragmentManager().putFragment(outState, FAVORITE_MOVIE_LIST_FRAGMENT_TAG, favoriteListFragment);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isFavoriteFragment){
            //Get the fragmentManager and replace the movieListFragment to the fragment container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, movieListFragment, MOVIE_LIST_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();

        }
        if(isFavoriteFragment){
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
                    .addToBackStack(null)
                    .commit();
        }
        else {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.MOVIE_KEY, movie);
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
