package com.griffin.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
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
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie> {

    private final String RATING_OUT_OF_TEN = "/10";
    static final String DETAIL_MOVIE = "MOVIE";
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 0;

    private TextView titleTextView;
    private ImageView imageViewMoviePicture;
    private TextView textViewMovieYear;
    private TextView textViewOriginalTitle;
    private TextView textViewOverview;
    private TextView textViewMovieRating;

    public DetailMovieFragment() {
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
            mMovie = arguments.getParcelable(DetailMovieFragment.DETAIL_MOVIE);
        }


        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

        titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        imageViewMoviePicture = (ImageView) rootView.findViewById(R.id.moviePictureImageView);
        textViewMovieYear = (TextView) rootView.findViewById(R.id.movieYearTextView);
        textViewOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        textViewOverview = (TextView) rootView.findViewById(R.id.overviewMovieTextView);
        textViewMovieRating = (TextView) rootView.findViewById(R.id.movieRatingTextView);

        Button buttonMarkAsFavorite = (Button) rootView.findViewById(R.id.markAsFavoriteButton);
        buttonMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie(mMovie);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void addMovie(Movie movie) {
        long movieRowId;

        // First, check if the mMovie with this id already exists in the db
        Cursor movieCursor = getContext().getContentResolver().query(
                //The URI content://com.griffin.popularmovies :
                MovieContract.FavoriteMoviesEntry.CONTENT_URI,
                //The list of which columns to return, in this case only the _ID column
                new String[]{MovieContract.FavoriteMoviesEntry._ID},
                //The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs))
                MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                //only one clause movie_id
                new String[]{Long.toString(movie.getId())},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.FavoriteMoviesEntry._ID);
            movieRowId = movieCursor.getLong(movieIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues values = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_PICTURE, movie.getUrl());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE, movie.getMovieDate());
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING, movie.getMovieRating());

            // Finally, insert location data into the database.
            Uri insertedUri = getContext().getContentResolver().insert(MovieContract.FavoriteMoviesEntry.CONTENT_URI,values);

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            movieRowId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();

    }
    //TODO
    public void removeMovie(Movie movie){
        getContext().getContentResolver().delete(MovieContract.FavoriteMoviesEntry.CONTENT_URI, MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID,
                new String[]{Integer.toString(movie.getId())});
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        if ( null != mMovie ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new FetchDetailMovieTask(getActivity(), mMovie);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        if (movie != null) {

            String urlPicture = movie.getUrl();
            Picasso.with(getActivity())
                    .load(urlPicture)
                    .into(imageViewMoviePicture);

            String date = movie.getMovieDate();
            textViewMovieYear.setText(date);

            String title = movie.getTitle();
            titleTextView.setText(title);

            String originalTitle = movie.getOriginalTitle();
            textViewOriginalTitle.setText(originalTitle);

            String overview = movie.getOverview();
            textViewOverview.setText(overview);

            String rating = movie.getMovieRating();
            textViewMovieRating.setText(rating + RATING_OUT_OF_TEN);

        }
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }
}
