package com.griffin.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


public class MainActivity extends AppCompatActivity implements FavoriteListFragment.Callback, MovieListFragment.Callback, DetailFavoriteFragment
        .Callback  {

    private boolean mTwoPane;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private static final String DETAIL_FAVORITE_FRAGMENT_TAG="DFFT";

    private static final String MOVIE_LIST_FRAGMENT_TAG = "MLFTAG";
    private static final String FAVORITE_MOVIE_LIST_FRAGMENT_TAG = "FMFTAG";

    private static final String BLANK_FRAGMENT_TAG = "BFTAG";

    private Spinner spinner;
    private int mSpinnerPosition;

    public static final String USER_CHOICE= "user_choice";

    public static final String TITLE_BLANK_FRAGMENT_KEY = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.sort_order_spinner);
        spinner.setSelection(Utilities.getSelectedChoiceNumber(this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected  = (String)parent.getSelectedItem();

                if(!selected.equals(Utilities.getSelectedChoice(getApplicationContext()))){
                    Utilities.setChoice(getApplicationContext(), selected);
                    if(mTwoPane) setBlankFragment();
                }
                setListFragmentOnSharedPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
            if (savedInstanceState == null) {
               setBlankFragment();
            }

            setListFragmentOnSharedPreferences();
        }
        else {
            mTwoPane = false;

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTwoPane) {

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detail_movie_container);

            if (fragment instanceof DetailFavoriteFragment) {
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

            if (fragment instanceof DetailFragment) {
                DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

                if (detailFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, detailFragment,
                            DETAIL_FRAGMENT_TAG).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                            .commit();
                }
            }

            if (fragment instanceof BlankFragment) {
                BlankFragment blankFragment = (BlankFragment) getSupportFragmentManager().findFragmentByTag(BLANK_FRAGMENT_TAG);

                if (blankFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, blankFragment,
                            BLANK_FRAGMENT_TAG).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_movie_container, new BlankFragment(),
                            BLANK_FRAGMENT_TAG).commit();
                }
            }
        }
        else {
            spinner.setSelection(Utilities.getSelectedChoiceNumber(this));
        }

    }



    private void setListFragmentOnSharedPreferences(){


        String choice = Utilities.getChoice(this);
        if(choice.equals(getString(R.string.pref_movies_favorite))) {
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


    //Remove the movie from the favorite list
    @Override
    public void onFavoriteMovieClick(Movie movie, Context context) {
        setBlankFragment();
    }

    private void setBlankFragment(){
        //frag = 0;
        Bundle b = new Bundle();
        b.putString(TITLE_BLANK_FRAGMENT_KEY, Utilities.getSelectedChoice(this));
        BlankFragment bf= new BlankFragment();
        bf.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_movie_container, bf, BLANK_FRAGMENT_TAG)
                .commit();
    }

}
