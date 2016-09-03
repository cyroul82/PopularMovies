package com.griffin.popularmovies.movie_list;

import android.os.Bundle;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.adapter.CollectionAdapter;
import com.griffin.popularmovies.adapter.PopularMoviesAdapter;
import com.griffin.popularmovies.task.FetchMoviesTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private PopularMoviesAdapter mMoviesAdapter = null;

    private List<Movie> mMoviesList = null;

    private static final int MOVIE_LOADER = 0;

    private static final String PAGE_KEY = "page_key";

    private static final String MOVIE_LIST_KEY = "movie_list_key";

    public static final String CHOICE = "choice";
    public static final String SEARCH = "search";

    private String mChoice = null;
    private String mSearch = null;

    private int mPage = 1;

    @BindView(R.id.gridview_moviesList)
    GridView mGridView;

    public MovieListFragment() {

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface CallbackMovieListFragment {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(int idMovie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creates a new List of movies if no previous state
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
            mMoviesList = new ArrayList<>();
        }
        //restore the previous state
        if (savedInstanceState != null) {
            mMoviesList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            if(savedInstanceState.containsKey(SEARCH)){
                mSearch = savedInstanceState.getString(SEARCH);
            }
            if(savedInstanceState.containsKey(CHOICE)){
                mChoice = savedInstanceState.getString(CHOICE);
                mPage = savedInstanceState.getInt(PAGE_KEY);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_list_fragment, container, false);

        ButterKnife.bind(this, rootView);

        if(savedInstanceState == null) {
            //Get back the arguments
            Bundle args = getArguments();
            if (args != null) {
                if (args.containsKey(CHOICE)) {
                    //Set up the variable mChoice
                    mChoice = args.getString(CHOICE);
                }
                if (args.containsKey(SEARCH)) {
                    mSearch = args.getString(SEARCH);
                }
            }
        }



        mMoviesAdapter = new PopularMoviesAdapter(getActivity(), R.layout.movie_item_picture, R.id.movieItemPictureImageView, mMoviesList);
        mGridView.setAdapter(mMoviesAdapter);

        //set up the OnClick Listener to gridViewMovies
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the movie back from the adapter
                Movie movie = mMoviesAdapter.getItem(position);
                ((CallbackMovieListFragment) getActivity()).onItemSelected(movie.getId());

            }
        });

        if(mChoice != null) {
            mGridView.setOnScrollListener(new EndlessScrolling(12) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    mPage++;
                    getLoaderManager().restartLoader(MOVIE_LOADER, null, MovieListFragment.this);

                }
            });
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        if (savedInstanceState == null ) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mChoice != null) {
            //save the page loaded so far
            outState.putInt(PAGE_KEY, mPage);
            outState.putString(CHOICE, mChoice);
        }

        if(mSearch != null){
            outState.putString(SEARCH, mSearch);
        }
        //put the mMoviesList into the bundle to avoid querying again while rebuilding
        outState.putParcelableArrayList(MOVIE_LIST_KEY, new ArrayList<Parcelable>(mMoviesList));

        super.onSaveInstanceState(outState);
    }


    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        if(mChoice != null) {
            return new FetchMoviesTask(getContext(), mPage, mChoice);
        }
        if(mSearch != null){
            return new FetchMoviesTask(getContext(), mSearch);
        }
        else return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {

        if (data != null) {
            //mMoviesAdapter.clear();

            for (Movie movie : data) {
                mMoviesAdapter.add(movie);

            }
        }
        mMoviesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        mMoviesAdapter.clear();
    }


}
