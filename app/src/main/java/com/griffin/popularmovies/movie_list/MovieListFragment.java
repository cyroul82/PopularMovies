package com.griffin.popularmovies.movie_list;

import android.content.Loader;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.adapter.PopularMoviesAdapter;
import com.griffin.popularmovies.task.FetchMoviesTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>{

    private PopularMoviesAdapter mMoviesAdapter = null;

    private ArrayList<Movie> mMoviesList = null;

    private static final int MOVIE_LOADER = 0;

    private static final String PAGE_KEY = "page_key";

    public static final String CHOICE = "choice";

    private String mChoice;

    private int mPage = 1;

    @BindView(R.id.gridview_moviesList) GridView mGridView;

    public MovieListFragment(){

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Movie movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creates a new List of movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.key_movies_list))){
            mMoviesList = new ArrayList<>();
        }
        //restore the previous state
        else {
            mMoviesList = savedInstanceState.getParcelableArrayList(getString(R.string.key_movies_list));
            mPage = savedInstanceState.getInt(PAGE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_list_fragment, container, false);

        ButterKnife.bind(this, rootView);

        //Get back the arguments
        Bundle args = getArguments();
        //Set up the variable mChoice
        mChoice = args.getString(CHOICE);

        mMoviesAdapter = new PopularMoviesAdapter(getActivity(), R.layout.movie_item_picture, R.id.movieItemPictureImageView, mMoviesList);
        mGridView.setAdapter(mMoviesAdapter);

        //set up the OnClick Listener to gridViewMovies
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //get the movie back from the adapter
                    Movie movie = mMoviesAdapter.getItem(position);
                    ((Callback)getActivity()).onItemSelected(movie);

                }
            });

        mGridView.setOnScrollListener(new EndlessScrolling(8, mPage) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                    mPage = page;
                    getLoaderManager().restartLoader(MOVIE_LOADER, null, MovieListFragment.this);

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        if(savedInstanceState == null) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the page loaded so far
        outState.putInt(PAGE_KEY, mPage);
        //put the mMoviesList into the bundle to avoid querying again while rebuilding
        outState.putParcelableArrayList(getString(R.string.key_movies_list), mMoviesList);
    }


    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTask(getContext(), mPage, mChoice);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null) {
            //mMoviesAdapter.clear();
            for (Movie movie : data) {
                if (Utilities.isMovieFavorite(movie.getId(), getContext()) == 1){
                    movie.setFavorite(1);
                }
                mMoviesAdapter.add(movie);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        mMoviesAdapter.clear();
    }




}
