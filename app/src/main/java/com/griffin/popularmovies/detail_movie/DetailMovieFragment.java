package com.griffin.popularmovies.detail_movie;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
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

import com.griffin.popularmovies.movie_list.Movie;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<CreditsMovie>{

    private final String RATING_OUT_OF_TEN = "/10";
    public static final String DETAIL_MOVIE = "MOVIE";
    public static final String EXTRA_DETAIL_MOVIE="EXTRAMOVIE";
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 0;

    private TextView mTitleTextView;
    private ImageView mImageViewMoviePicture;
    private TextView mTextViewMovieYear;
    private TextView mTextViewOriginalTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewMovieRating;
    private TextView mTextViewActor;
    private TextView mTextViewGenre;

    private CreditsMovie mCreditsMovie;

    private final int mNumberMaxDisplayedActors = 3;

    public DetailMovieFragment() {
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creates a new List of movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(EXTRA_DETAIL_MOVIE)){
            mCreditsMovie = null;
        }
        //restore the previous state
        else {
            mCreditsMovie = (CreditsMovie)savedInstanceState.getParcelable(DetailMovieFragment.EXTRA_DETAIL_MOVIE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_DETAIL_MOVIE, mCreditsMovie);
        super.onSaveInstanceState(outState);
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

        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

        mTitleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        mImageViewMoviePicture = (ImageView) rootView.findViewById(R.id.moviePictureImageView);
        mTextViewMovieYear = (TextView) rootView.findViewById(R.id.movieYearTextView);
        mTextViewOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        mTextViewOverview = (TextView) rootView.findViewById(R.id.overviewMovieTextView);
        mTextViewMovieRating = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        mTextViewActor = (TextView) rootView.findViewById(R.id.textView_actor);
        mTextViewGenre = (TextView) rootView.findViewById(R.id.textView_genre);

        Bundle arguments = getArguments();

        if (arguments != null) {

            mMovie = arguments.getParcelable(DetailMovieFragment.DETAIL_MOVIE);


            String urlPicture = mMovie.getUrl();
            Picasso.with(getActivity())
                    .load(urlPicture)
                    .into(mImageViewMoviePicture);

            String date = mMovie.getMovieDate();
            mTextViewMovieYear.setText(date);

            String title = mMovie.getTitle();
            mTitleTextView.setText(title);

            String originalTitle = mMovie.getOriginalTitle();
            mTextViewOriginalTitle.setText(originalTitle);

            String overview = mMovie.getOverview();
            mTextViewOverview.setText(overview);

            String rating = mMovie.getMovieRating();
            mTextViewMovieRating.setText(rating + RATING_OUT_OF_TEN);

            if (mCreditsMovie != null) {
                setExtraDetail();
            }

        }

        Button buttonMarkAsFavorite = (Button) rootView.findViewById(R.id.markAsFavoriteButton);
        buttonMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovieToFavorite(mMovie);
            }
        });

        return rootView;
    }

    private void addMovieToFavorite(Movie movie) {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        if(mMovie != null && mCreditsMovie == null) getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<CreditsMovie> onCreateLoader(int id, Bundle args) {
        return new FetchDetailMovieTask(getActivity(), mMovie);

    }

    @Override
    public void onLoadFinished(Loader<CreditsMovie> loader, CreditsMovie creditsMovie) {

    }

    @Override
    public void onLoaderReset(Loader<CreditsMovie> loader) {

    }


    private void setExtraDetail(){
        mMovie.setActors(mCreditsMovie.getActors());
        int maxActors;
        if(mCreditsMovie.getActors().size() > mNumberMaxDisplayedActors ){
            maxActors = mNumberMaxDisplayedActors;
        }
        else maxActors = mCreditsMovie.getActors().size();

        for (int i=0 ; i < maxActors  ; i++){
            StringBuilder sb = new StringBuilder();
            sb.append(mMovie.getActors().get(i).getName()).append("  (").append(mMovie.getActors().get(i).getCharacter()).append(")\n");
            mTextViewActor.append(sb.toString());
        }

        mMovie.setGenre(mCreditsMovie.getGenre());
        String[] genres = mMovie.getGenre();
        for (int i = 0 ; i < genres.length ; i++){
            StringBuilder sb = new StringBuilder();
            mTextViewGenre.append(genres[i]);
            if(i != genres.length-1){
                mTextViewGenre.append(" / ");
            }
        }
    }




}
