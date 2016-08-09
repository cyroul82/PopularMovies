package com.griffin.popularmovies.detail_movie;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.Pojo.Genre;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.Pojo.Movie;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by griffin on 22/07/16.
 */
public class DetailFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static String TAG = DetailFavoriteFragment.class.getSimpleName();

    public static final String DETAIL_URI = "FAVORITEMOVIE";
    private Uri mUriMovie;
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 1;

    private final int mNumberMaxDisplayedActors = 5;

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


    @BindView(R.id.textView_Title) TextView mTextViewTitle;
    @BindView(R.id.imageView_Picture) ImageView mImageViewMoviePicture;
    @BindView(R.id.textView_Year) TextView mTextViewMovieYear;
    @BindView(R.id.textView_Original_Title) TextView mTextViewOriginalTitle;
    @BindView(R.id.textView_Overview) TextView mTextViewOverview;
    @BindView(R.id.textView_Rating) TextView mTextViewMovieRating;
    @BindView(R.id.textView_Casting) TextView mTextViewCasting;
    @BindView(R.id.textView_Genre) TextView mTextViewGenre;
    @BindView(R.id.textView_Runtime) TextView mTextViewRuntime;
    @BindView(R.id.linearLayout_Trailer) LinearLayout mLinearLayoutTrailer;
    @BindView(R.id.linearLayout_Review) LinearLayout mLinearLayoutReview;
    @BindView(R.id.shineButton_favorite) ShineButton mFavoriteButton;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey(DETAIL_URI)){
            mUriMovie = null;
        }
        else {
            mUriMovie = savedInstanceState.getParcelable(DETAIL_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(DETAIL_URI, mUriMovie);
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

        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            //content://com.griffin.popularmovies/favorite/1  -> the number represents the selected movie in the the gridView
            mUriMovie = arguments.getParcelable(DetailFavoriteFragment.DETAIL_URI);
        }


        mFavoriteButton.init(getActivity());

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mFavoriteButton.isChecked()) {
                    Utilities.removeMovieFromFavorite(mMovie, getContext());
                    ((Callback)getActivity()).onFavoriteMovieClick(mMovie, getContext().getApplicationContext());
                }
            }
        });

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);

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

            mMovie = Utilities.getMovieFromCursor(movieCursor);

            mImageViewMoviePicture.setImageBitmap(Utilities.getPoster(mMovie.getPosterPath(), mMovie.getId()));

            mTextViewMovieYear.setText(mMovie.getReleaseDate());

            mFavoriteButton.setChecked(true);

            mTextViewTitle.setText(mMovie.getTitle());

            mTextViewOriginalTitle.setText(mMovie.getOriginalTitle());

            mTextViewOverview.setText(mMovie.getOverview());

            mTextViewMovieRating.setText(String.format(getString(R.string.rating), Double.toString(mMovie.getVoteAverage())));

            mTextViewRuntime.setText(String.format(getString(R.string.runtime), Integer.toString(mMovie.getDetailMovie().getMovieDetail()
                    .getRuntime())));


            List<Genre> genres = mMovie.getDetailMovie().getMovieDetail().getGenres();
            StringBuilder sb = new StringBuilder();
            for(Genre genre : genres){
                sb.append(genre.getName());
                sb.append(" / ");
            }
            //Set genre Object and update UI
            mTextViewGenre.setText(sb.toString());

            List<Cast>  castingList = mMovie.getDetailMovie().getCredits().getCast();

            int maxActors;

            if(castingList.size() > mNumberMaxDisplayedActors){
                maxActors = mNumberMaxDisplayedActors;
            }
            else maxActors = castingList.size();
            /*for (Cast cast : castingList) {
                sb.append(cast.getName() + "  (" + cast.getCharacter() + ")\n");
            }*/
            sb = new StringBuilder();
            int i=0;
            while(i < maxActors) {
                Cast cast = castingList.get(i);
                sb.append(cast.getName() + "  (" + cast.getCharacter() + ")\n");

                i++;
            }

            mTextViewCasting.setText(sb.toString());

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
