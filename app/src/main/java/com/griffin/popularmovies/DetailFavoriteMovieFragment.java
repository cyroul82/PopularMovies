package com.griffin.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.griffin.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by griffin on 22/07/16.
 */
public class DetailFavoriteMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String RATING_OUT_OF_TEN = "/10";
    static final String DETAIL_URI = "FAVORITEMOVIE";
    private Uri mUriMovie;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.FavoriteMoviesEntry.TABLE_NAME + "." + MovieContract.FavoriteMoviesEntry._ID,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_PICTURE,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE,
            MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_FAVORITE_MOVIE_INDEX = 0;
    public static final int COL_FAVORITE_MOVIE_ID = 1;
    public static final int COL_FAVORITE_MOVIE_TITLE = 2;
    public static final int COL_FAVORITE_MOVIE_PICTURE = 3;
    public static final int COL_FAVORITE_MOVIE_ORIGINAL_TITLE = 4;
    public static final int COL_FAVORITE_MOVIE_OVERVIEW = 5;
    public static final int COL_FAVORITE_MOVIE_DATE = 6;
    public static final int COL_FAVORITE_MOVIE_RATING = 7;


    private TextView titleTextView;
    private ImageView imageViewMoviePicture;
    private TextView textViewMovieYear;
    private TextView textViewOriginalTitle;
    private TextView textViewOverview;
    private TextView textViewMovieRating;

    public DetailFavoriteMovieFragment() {
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUriMovie = arguments.getParcelable(DetailFavoriteMovieFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

        titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        imageViewMoviePicture = (ImageView) rootView.findViewById(R.id.moviePictureImageView);
        textViewMovieYear = (TextView) rootView.findViewById(R.id.movieYearTextView);
        textViewOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        textViewOverview = (TextView) rootView.findViewById(R.id.overviewMovieTextView);
        textViewMovieRating = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        Button buttonFavorite = (Button) rootView.findViewById(R.id.markAsFavoriteButton);
        buttonFavorite.setVisibility(View.INVISIBLE);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUriMovie ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.

            CursorLoader cl = new CursorLoader(getActivity(), mUriMovie, DETAIL_COLUMNS, null, null, null);
            return cl;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieCursor) {
        if (movieCursor != null && movieCursor.moveToFirst()) {
            String urlPicture = movieCursor.getString(COL_FAVORITE_MOVIE_PICTURE);
            Picasso.with(getActivity())
                    .load(urlPicture)
                    .into(imageViewMoviePicture);

            String date = movieCursor.getString(COL_FAVORITE_MOVIE_DATE);
            textViewMovieYear.setText(date);

            String title = movieCursor.getString(COL_FAVORITE_MOVIE_TITLE);
            titleTextView.setText(title);

            String originalTitle = movieCursor.getString(COL_FAVORITE_MOVIE_ORIGINAL_TITLE);
            textViewOriginalTitle.setText(originalTitle);

            String overview = movieCursor.getString(COL_FAVORITE_MOVIE_OVERVIEW);
            textViewOverview.setText(overview);

            String rating = movieCursor.getString(COL_FAVORITE_MOVIE_RATING);
            textViewMovieRating.setText(rating + RATING_OUT_OF_TEN);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
