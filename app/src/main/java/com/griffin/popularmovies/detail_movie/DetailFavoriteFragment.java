package com.griffin.popularmovies.detail_movie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.Genre;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.adapter.TrailerMovieAdapter;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.Pojo.Movie;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by griffin on 22/07/16.
 */
public class DetailFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static String LOG_TAG = DetailFavoriteFragment.class.getSimpleName();

    public static final String FAVORITE_MOVIE = "FAVORITE_MOVIE";
    private Uri mUriMovie;
    private DetailMovie mDetailMovie;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.DetailEntry.TABLE_NAME + "." + MovieContract.DetailEntry._ID,

            MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE,

            MovieContract.DetailEntry.COLUMN_MOVIE_TITLE,
            MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE,
            MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.DetailEntry.COLUMN_MOVIE_DATE,
            MovieContract.DetailEntry.COLUMN_MOVIE_RATING,
            MovieContract.DetailEntry.COLUMN_MOVIE_GENRE,
            MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME,
            MovieContract.DetailEntry.COLUMN_MOVIE_CASTING,
            MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER,
            MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS,
            MovieContract.DetailEntry.COLUMN_MOVIE_TAGLINE
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_FAVORITE_DETAIL_KEY =1;
    public static final int COL_FAVORITE_MOVIE_ID = 2;
    public static final int COL_FAVORITE_MOVIE_PICTURE = 3;
    public static final int COL_FAVORITE_MOVIE_TITLE = 4;

    public static final int COL_FAVORITE_MOVIE_ORIGINAL_TITLE = 5;
    public static final int COL_FAVORITE_MOVIE_OVERVIEW = 6;
    public static final int COL_FAVORITE_MOVIE_DATE = 7;
    public static final int COL_FAVORITE_MOVIE_RATING = 8;
    public static final int COL_FAVORITE_MOVIE_DETAIL_GENRE = 9;
    public static final int COL_FAVORITE_MOVIE_DETAIL_RUNTIME = 10;
    public static final int COL_FAVORITE_MOVIE_DETAIL_CASTING = 11;
    public static final int COL_FAVORITE_MOVIE_DETAIL_VIDEOS = 12;
    public static final int COL_FAVORITE_MOVIE_DETAIL_REVIEWS = 13;
    public static final int COL_FAVORITE_MOVIE_TAGLINE = 14;

    @Nullable
    @BindView(R.id.textView_movieTitle) TextView mTextViewMovieTitle;
    @BindView(R.id.textView_tagline) TextView mTextViewTagLine;
    @BindView(R.id.imageView_Picture) ImageView mImageViewMoviePicture;
    @BindView(R.id.textView_Year) TextView mTextViewMovieYear;
    @BindView(R.id.textView_Original_Title) TextView mTextViewOriginalTitle;
    @BindView(R.id.textView_Overview) TextView mTextViewOverview;
    @BindView(R.id.textView_Rating) TextView mTextViewMovieRating;
    @BindView(R.id.textView_Genre) TextView mTextViewGenre;
    @BindView(R.id.textView_Runtime) TextView mTextViewRuntime;
    @BindView(R.id.linearLayout_Trailer) LinearLayout mLinearLayoutTrailer;
    @BindView(R.id.recyclerView_trailer) RecyclerView mRecyclerViewTrailer;
    @BindView(R.id.cardViewTrailer) CardView mCardViewTrailer;

    private List<TrailerDetail> mTrailerDetailList = new ArrayList<>();

    private TrailerMovieAdapter mTrailerMovieAdapter;

    private boolean mIsDetailFavoriteFragmentFromActivity;
    public static final String IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY = "idfffa";

    private static final String SHORTBRAIN_SHARE_HASHTAG = " #Shortbrain";
    private String mShareMovie;


    public DetailFavoriteFragment() {
        setHasOptionsMenu(true);
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
        void onFavoriteMovieClick(Movie movie, Context context);
    }

    public interface CallbackDetailFavoriteFragment{
        void setTitleAndPosterOnActivity(String title, String posterPath, int idMovie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(FAVORITE_MOVIE) && savedInstanceState.containsKey
                (IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY)){
            mIsDetailFavoriteFragmentFromActivity = savedInstanceState.getBoolean(IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY);
            mDetailMovie = savedInstanceState.getParcelable(FAVORITE_MOVIE);
            if (mDetailMovie != null){
                mTrailerDetailList = mDetailMovie.getTrailerDetails();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(FAVORITE_MOVIE, mDetailMovie);
        outState.putBoolean(IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY, mIsDetailFavoriteFragmentFromActivity);
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

        if (mShareMovie != null) {
            mShareActionProvider.setShareIntent(createShareMovie());
        }
    }

    private Intent createShareMovie(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareMovie + "\n" + SHORTBRAIN_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            //content://com.griffin.popularmovies/favorite/1  -> the number represents the selected movie in the the gridView
            mUriMovie = arguments.getParcelable(FAVORITE_MOVIE);
            mIsDetailFavoriteFragmentFromActivity = arguments.getBoolean(IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY);
        }
        if(savedInstanceState != null){
            setUI();
        }

        mRecyclerViewTrailer.setHasFixedSize(true);
        // use a linear layout manager , create the *** TRAILER *** adapter and set it up to the recycler View
        LinearLayoutManager linearLayoutManagerTrailer = new LinearLayoutManager(getContext());
        linearLayoutManagerTrailer.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewTrailer.setLayoutManager(linearLayoutManagerTrailer);

        mTrailerMovieAdapter = new TrailerMovieAdapter(mTrailerDetailList, getContext());
        mRecyclerViewTrailer.setAdapter(mTrailerMovieAdapter);

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

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
            mDetailMovie = Utilities.getDetailMovieFromCursor(movieCursor);

            //clear the trailer List and add the new one to the adapter
            if(mDetailMovie.getTrailerDetails() != null){
                mTrailerDetailList.clear();
                mTrailerDetailList.addAll(mDetailMovie.getTrailerDetails());

                mTrailerMovieAdapter.notifyDataSetChanged();
            }
            else {
                mCardViewTrailer.setVisibility(View.GONE);
            }
            setUI();

            mShareMovie = String.format("%s \n%s", mDetailMovie.getMovieDetail().getTitle(), mDetailMovie.getMovieDetail().getOverview());

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovie());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setUI(){

        //set the title text, only on tablet display !
        if (mTextViewMovieTitle != null) {
            mTextViewMovieTitle.setText(mDetailMovie.getMovieDetail().getTitle());
        }


        mTextViewTagLine.setText(mDetailMovie.getMovieDetail().getTagline());

        try {

            mImageViewMoviePicture.setImageBitmap(Utilities.getPoster(mDetailMovie.getMovieDetail().getPosterPath(), mDetailMovie.getMovieDetail().getId()));
        }
        catch(FileNotFoundException e){
            Log.d(LOG_TAG, e.getMessage());
        }

        mTextViewMovieYear.setText(Utilities.getMonthAndYear(mDetailMovie.getMovieDetail().getReleaseDate()));

       //mFavoriteButton.setChecked(true);

        mTextViewOriginalTitle.setText(mDetailMovie.getMovieDetail().getOriginalTitle());

        mTextViewOverview.setText(mDetailMovie.getMovieDetail().getOverview());

        mTextViewMovieRating.setText(String.format(getString(R.string.rating), Double.toString(mDetailMovie.getMovieDetail().getVoteAverage())));

        mTextViewRuntime.setText(String.format(getString(R.string.runtime), Integer.toString(mDetailMovie.getMovieDetail().getRuntime())));


        List<Genre> genres = mDetailMovie.getMovieDetail().getGenres();
        StringBuilder sb = new StringBuilder();
        for(Genre genre : genres){
            sb.append(genre.getName());

            sb.append(" / ");
        }
        //Set genre Object and update UI
        mTextViewGenre.setText(sb.toString());

        if(mIsDetailFavoriteFragmentFromActivity) {
            ((CallbackDetailFavoriteFragment) getActivity()).setTitleAndPosterOnActivity(mDetailMovie.getMovieDetail().getTitle(),
                    mDetailMovie.getMovieDetail().getPosterPath(), mDetailMovie.getMovieDetail().getId());
        }
    }
}
