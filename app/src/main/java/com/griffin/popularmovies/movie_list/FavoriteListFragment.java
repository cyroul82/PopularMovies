package com.griffin.popularmovies.movie_list;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.griffin.popularmovies.R;
import com.griffin.popularmovies.adapter.FavoriteMoviesAdapter;
import com.griffin.popularmovies.data.MovieContract;

import java.net.URI;

/**
 * Created by griffin on 18/07/16.
 */
public class FavoriteListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private FavoriteMoviesAdapter mFavoriteAdapter = null;
    private URI mUriMovie;

    //Loader ID
    private static final int FAVORITE_LOADER = 1;

    private String[] MOVIE_COLUMNS = {
            MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE,

    };

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    public static final int COLUMN_INDEX_MOVIE = 0;
    public static final int COLUMN_DETAIL_KEY = 1;
    public static final int COLUMN_MOVIE_ID = 2;
    public static final int COLUMN_MOVIE_PICTURE = 3;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri movieUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.favorite_movie_fragment, container, false);

        GridView gridViewFavorite = (GridView)rootView.findViewById(R.id.gridview_favoriteMoviesList);
        //set the cursor to null as default, as it will get swap onLoadFinished with the populated one.
        mFavoriteAdapter = new FavoriteMoviesAdapter(getActivity(), null, 0);
        gridViewFavorite.setAdapter(mFavoriteAdapter);

        gridViewFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the cursor back from the adapter
                Cursor movieCursor = (Cursor)mFavoriteAdapter.getItem(position);

                if (movieCursor != null) {
                    ((Callback) getActivity()).onItemSelected(MovieContract.FavoriteEntry.buildMovieUriFromDetailId(movieCursor.getInt
                            (COLUMN_DETAIL_KEY)));
                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
    }

    // onCreateLoader is derived from AsyncTask, therefore do the process in doInBackground
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MovieContract.FavoriteEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, null);
    }

    // this method is called when the onCreateLoader has finished and then swap the actual cursor (null by default) to the new cursor created in
    // onCreatedLoader
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //swap the cursor in the FavoriteMovieAdapter with a new loaded one
        mFavoriteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Release any resources that might be using
        mFavoriteAdapter.swapCursor(null);
    }
}
