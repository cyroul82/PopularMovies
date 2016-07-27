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

import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.movie_list.Movie;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by griffin on 22/07/16.
 */
public class DetailFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static String TAG = DetailFavoriteFragment.class.getSimpleName();

    private final String RATING_OUT_OF_TEN = "/10";
    public static final String DETAIL_URI = "FAVORITEMOVIE";
    public static final String EXTRA_DETAIL_MOVIE="EXTRAMOVIE";
    private Uri mUriMovie;
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;


    private static final int DETAIL_LOADER = 1;

    private final int mNumberMaxDisplayedActors = 3;

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
            MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS

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


    private TextView mTextViewTitle;
    private ImageView mImageViewMoviePicture;
    private TextView mTextViewMovieYear;
    private TextView mTextViewOriginalTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewMovieRating;
    private TextView mTextViewCasting;
    private TextView mTextViewGenre;
    private TextView mTextViewRuntime;
    private LinearLayout mLinearLayoutTrailer;
    private LinearLayout mLinearLayoutReview;
    private ShineButton mFavoriteButton;


    private ExtraDetailMovie mExtraDetailMovie;


    public DetailFavoriteFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
            //content://com.griffin.popularmovies/favorite/1  -> the number represents the selected movie in the the gridView
            mUriMovie = arguments.getParcelable(DetailFavoriteFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

        mTextViewTitle = (TextView) rootView.findViewById(R.id.titleTextView);
        mImageViewMoviePicture = (ImageView) rootView.findViewById(R.id.moviePictureImageView);
        mTextViewMovieYear = (TextView) rootView.findViewById(R.id.movieYearTextView);
        mTextViewOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        mTextViewOverview = (TextView) rootView.findViewById(R.id.overviewMovieTextView);
        mTextViewMovieRating = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        mTextViewCasting = (TextView) rootView.findViewById(R.id.textView_actor);
        mTextViewGenre = (TextView) rootView.findViewById(R.id.textView_genre);
        mLinearLayoutTrailer = (LinearLayout) rootView.findViewById(R.id.linearLayout_trailer);
        mLinearLayoutReview = (LinearLayout) rootView.findViewById(R.id.linearLayout_Review);
        mFavoriteButton = (ShineButton) rootView.findViewById(R.id.shineButton_favorite);
        mTextViewRuntime = (TextView) rootView.findViewById(R.id.textView_runtime);

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
            mTextViewTitle.setText(title);

            String originalTitle = movieCursor.getString(COL_FAVORITE_MOVIE_ORIGINAL_TITLE);
            mTextViewOriginalTitle.setText(originalTitle);

            String overview = movieCursor.getString(COL_FAVORITE_MOVIE_OVERVIEW);
            mTextViewOverview.setText(overview);

            String rating = movieCursor.getString(COL_FAVORITE_MOVIE_RATING);
            mTextViewMovieRating.setText(rating + RATING_OUT_OF_TEN);

            String runtime = movieCursor.getString(COL_FAVORITE_MOVIE_DETAIL_RUNTIME);
            mTextViewRuntime.setText(runtime);

            String[] genres = Utilities.convertStringToArray(movieCursor.getString(COL_FAVORITE_MOVIE_DETAIL_GENRE));
            //Set genre Object and update UI
            for (int i = 0 ; i < genres.length ; i++){
                StringBuilder sb = new StringBuilder();
                mTextViewGenre.append(genres[i]);
                if(i != genres.length-1){
                    mTextViewGenre.append(" / ");
                }
            }
            String genre = movieCursor.getString(COL_FAVORITE_MOVIE_DETAIL_GENRE);
            mTextViewGenre.setText(genre);

            List<CastingMovie> castingList = Utilities.convertStringToList(movieCursor.getString(COL_FAVORITE_MOVIE_DETAIL_CASTING));
            int maxActors;
            int mNumberMaxDisplayedActors = 5;
            if(castingList.size() > mNumberMaxDisplayedActors){
                maxActors = mNumberMaxDisplayedActors;
            }
            else maxActors = castingList.size();

            for (int i=0 ; i < maxActors  ; i++){
                CastingMovie castingMovie = castingList.get(i);
                mTextViewCasting.append(castingMovie.getName() + "  (" + castingMovie.getCharacter() + ")\n");

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setExtraDetail(){
        mMovie.setActors(mExtraDetailMovie.getCasting());
        int maxActors;
        if(mExtraDetailMovie.getCasting().size() > mNumberMaxDisplayedActors ){
            maxActors = mNumberMaxDisplayedActors;
        }
        else maxActors = mExtraDetailMovie.getCasting().size();

        for (int i=0 ; i < maxActors  ; i++){
            StringBuilder sb = new StringBuilder();
            sb.append(mMovie.getCasting().get(i).getName()).append("  (").append(mMovie.getCasting().get(i).getCharacter()).append(")\n");
            mTextViewCasting.append(sb.toString());
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
