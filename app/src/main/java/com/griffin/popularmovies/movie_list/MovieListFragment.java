package com.griffin.popularmovies.movie_list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>{

    private PopularMoviesAdapter mMoviesAdapter = null;

    private ArrayList<Movie> mMoviesList = null;
    //Loader ID
    private static final int MOVIE_LOADER = 0;

    private static final String SELECTED_KEY = "position_key";
    private static final String PAGE_KEY = "page_key";

    private GridView mGridView;
    private int mPage = 1;
    private int mPosition;

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


        setHasOptionsMenu(true);

        // Creates a new List of movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.key_movies_list))){
            mMoviesList = new ArrayList<>();
        }
        //restore the previous state
        else {
            mMoviesList = savedInstanceState.getParcelableArrayList(getString(R.string.key_movies_list));
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            mPage = savedInstanceState.getInt(PAGE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_list_fragment, container, false);

        mGridView = (GridView)rootView.findViewById(R.id.gridview_moviesList);
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

        mGridView.setOnScrollListener(new EndlessScrolling(4) {
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
        //mPosition = mGridView.getFirstVisiblePosition();
        outState.putInt(SELECTED_KEY, mGridView.getFirstVisiblePosition());
        outState.putInt(PAGE_KEY, mPage);
        //we put the mMoviesList into the bundle to avoid querying again while rebuilding
        outState.putParcelableArrayList(getString(R.string.key_movies_list), mMoviesList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();
        Utilities.checkConnectionStatus(getContext());
    }


    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTask(getContext(), mPage);
    }


    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
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
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMoviesAdapter.clear();
    }

}
