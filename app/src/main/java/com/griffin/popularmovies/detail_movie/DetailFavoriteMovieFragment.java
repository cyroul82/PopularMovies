package com.griffin.popularmovies.detail_movie;

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

import com.griffin.popularmovies.R;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.movie_list.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by griffin on 22/07/16.
 */
public class DetailFavoriteMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String RATING_OUT_OF_TEN = "/10";
    public static final String DETAIL_URI = "FAVORITEMOVIE";
    public static final String EXTRA_DETAIL_MOVIE="EXTRAMOVIE";
    private Uri mUriMovie;
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;


    private static final int DETAIL_LOADER = 1;

    private final int mNumberMaxDisplayedActors = 3;

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


    private TextView mTitleTextView;
    private ImageView mImageViewMoviePicture;
    private TextView mTextViewMovieYear;
    private TextView mTextViewOriginalTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewMovieRating;
    private TextView mTextViewActor;
    private TextView mTextViewGenre;

    private ExtraDetailMovie mExtraDetailMovie;


    public DetailFavoriteMovieFragment() {
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creates a new List of movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(EXTRA_DETAIL_MOVIE)){
            mMovie = null;
        }
        //restore the previous state
        else {
            mExtraDetailMovie = (ExtraDetailMovie)savedInstanceState.getParcelable(EXTRA_DETAIL_MOVIE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_DETAIL_MOVIE, mExtraDetailMovie);
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

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUriMovie = arguments.getParcelable(DetailFavoriteMovieFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

        mTitleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        mImageViewMoviePicture = (ImageView) rootView.findViewById(R.id.moviePictureImageView);
        mTextViewMovieYear = (TextView) rootView.findViewById(R.id.movieYearTextView);
        mTextViewOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        mTextViewOverview = (TextView) rootView.findViewById(R.id.overviewMovieTextView);
        mTextViewMovieRating = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        mTextViewActor = (TextView) rootView.findViewById(R.id.textView_actor);
        mTextViewGenre = (TextView) rootView.findViewById(R.id.textView_genre);

        Button buttonFavorite = (Button) rootView.findViewById(R.id.markAsFavoriteButton);
        buttonFavorite.setVisibility(View.GONE);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUriMovie != null) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.

            return new CursorLoader(getActivity(),
                    mUriMovie,
                    DETAIL_COLUMNS,
                    null,
                    null, null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieCursor) {
        if (movieCursor != null && movieCursor.moveToFirst()) {
            String urlPicture = movieCursor.getString(COL_FAVORITE_MOVIE_PICTURE);
            Picasso.with(getActivity())
                    .load(urlPicture)
                    .into(mImageViewMoviePicture);

            String date = movieCursor.getString(COL_FAVORITE_MOVIE_DATE);
            mTextViewMovieYear.setText(date);

            String title = movieCursor.getString(COL_FAVORITE_MOVIE_TITLE);
            mTitleTextView.setText(title);

            String originalTitle = movieCursor.getString(COL_FAVORITE_MOVIE_ORIGINAL_TITLE);
            mTextViewOriginalTitle.setText(originalTitle);

            String overview = movieCursor.getString(COL_FAVORITE_MOVIE_OVERVIEW);
            mTextViewOverview.setText(overview);

            String rating = movieCursor.getString(COL_FAVORITE_MOVIE_RATING);
            mTextViewMovieRating.setText(rating + RATING_OUT_OF_TEN);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setExtraDetail(){
        mMovie.setActors(mExtraDetailMovie.getActors());
        int maxActors;
        if(mExtraDetailMovie.getActors().size() > mNumberMaxDisplayedActors ){
            maxActors = mNumberMaxDisplayedActors;
        }
        else maxActors = mExtraDetailMovie.getActors().size();

        for (int i=0 ; i < maxActors  ; i++){
            StringBuilder sb = new StringBuilder();
            sb.append(mMovie.getActors().get(i).getName()).append("  (").append(mMovie.getActors().get(i).getCharacter()).append(")\n");
            mTextViewActor.append(sb.toString());
        }

        mMovie.setGenre(mExtraDetailMovie.getGenre());
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
