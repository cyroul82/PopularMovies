package com.griffin.popularmovies.movie_list;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.R;
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

    private int mPosition;
    private String SELECTED_KEY = "positionkey";

    private GridView mGridView;
    private ProgressDialog mProgressDialog;

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

                    mPosition = mGridView.getFirstVisiblePosition();
                }
            });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            mGridView.smoothScrollToPosition(mPosition);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_KEY, mPosition);
        //we put the mMoviesList into the bundle to avoid querying again while rebuilding
        outState.putParcelableArrayList(getString(R.string.key_movies_list), mMoviesList);
        super.onSaveInstanceState(outState);
    }



    public void checkConnectionStatus(){
        final ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(!networkInfoWifi.isAvailable() && !networkInfoMobile.isAvailable() ){
            Toast.makeText(getActivity(), R.string.connectivity, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkConnectionStatus();
    }



    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTask(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null) {
            mMoviesAdapter.clear();
            for (Movie movie : data) {
                mMoviesAdapter.add(movie);
            }
        }
        if (mPosition != mGridView.getFirstVisiblePosition()){
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMoviesAdapter.clear();
    }

}
