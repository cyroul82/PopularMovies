package com.griffin.popularmovies.detail_movie;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.griffin.popularmovies.pojo.Cast;
import com.griffin.popularmovies.pojo.Genre;
import com.griffin.popularmovies.pojo.Part;
import com.griffin.popularmovies.pojo.Reviews;
import com.griffin.popularmovies.pojo.TrailerDetail;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.adapter.CastingAdapter;
import com.griffin.popularmovies.adapter.CollectionAdapter;
import com.griffin.popularmovies.adapter.ReviewMovieAdapter;
import com.griffin.popularmovies.adapter.TrailerMovieAdapter;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.task.MovieToFavoriteTask;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by griffin on 22/07/16.
 */
public class DetailFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CollectionAdapter
        .CallbackCollectionAdapter, View.OnClickListener, MovieToFavoriteTask.OnQueryCompleteListener {

    public static final String IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY = "idfffa";
    public static final String FAVORITE_MOVIE = "FAVORITE_MOVIE";
    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_FAVORITE_MOVIE_ID = 1;
    public static final int COL_FAVORITE_MOVIE_PICTURE = 2;
    public static final int COL_FAVORITE_MOVIE_TITLE = 3;
    public static final int COL_FAVORITE_MOVIE_ORIGINAL_TITLE = 4;
    public static final int COL_FAVORITE_MOVIE_OVERVIEW = 5;
    public static final int COL_FAVORITE_MOVIE_DATE = 6;
    public static final int COL_FAVORITE_MOVIE_RATING = 7;
    public static final int COL_FAVORITE_MOVIE_DETAIL_GENRE = 8;
    public static final int COL_FAVORITE_MOVIE_DETAIL_RUNTIME = 9;
    public static final int COL_FAVORITE_MOVIE_DETAIL_CASTING = 10;
    public static final int COL_FAVORITE_MOVIE_DETAIL_VIDEOS = 11;
    public static final int COL_FAVORITE_MOVIE_DETAIL_REVIEWS = 12;
    public static final int COL_FAVORITE_MOVIE_TAGLINE = 13;
    private static final String LOG_TAG = DetailFavoriteFragment.class.getSimpleName();
    private static final String SHORTBRAIN_SHARE_HASHTAG = " #Shortbrain";
    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.DetailEntry.TABLE_NAME + "." + MovieContract.DetailEntry._ID,

            MovieContract.DetailEntry.COLUMN_MOVIE_ID,
            MovieContract.DetailEntry.COLUMN_MOVIE_PICTURE,
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
    @BindView(R.id.imageView_Picture)
    ImageView mImageViewMoviePicture;
    @BindView(R.id.textView_Year)
    TextView mTextViewMovieYear;
    @BindView(R.id.textView_Original_Title_Text)
    TextView mTextViewOriginalTitleText;
    @BindView(R.id.textView_Original_Title)
    TextView mTextViewOriginalTitle;
    @BindView(R.id.textView_Overview)
    TextView mTextViewOverview;
    @BindView(R.id.textView_Rating)
    TextView mTextViewMovieRating;
    @BindView(R.id.textView_Genre)
    TextView mTextViewGenre;
    @BindView(R.id.textView_Runtime)
    TextView mTextViewRuntime;
    @BindView(R.id.linearLayout_Trailer)
    LinearLayout mLinearLayoutTrailer;
    @BindView(R.id.linearLayout_Review)
    LinearLayout mLinearLayoutReview;
    @BindView(R.id.textView_tagline)
    TextView mTextViewTagLine;
    @BindView(R.id.textView_Collection_main_title)
    TextView mTextViewCollectionMainTitle;
    @BindView(R.id.cardViewReview)
    CardView mCardViewReview;
    @BindView(R.id.recyclerView_review)
    RecyclerView mRecyclerViewReview;
    @BindView(R.id.recyclerView_trailer)
    RecyclerView mRecyclerViewTrailer;
    @BindView(R.id.recyclerView_casting)
    RecyclerView mRecyclerViewCasting;
    @BindView(R.id.recyclerView_collection)
    RecyclerView mRecyclerViewCollection;
    @BindView(R.id.cardViewCollection)
    CardView mCardViewCollection;
    @BindView(R.id.cardViewCasting)
    CardView mCardViewCasting;
    @BindView(R.id.cardViewTrailer)
    CardView mCardViewTrailer;
    @Nullable
    @BindView(R.id.floatingButton_favorite_tablet)
    FloatingActionButton mFloatingButtonFavorite;
    @Nullable
    @BindView(R.id.textView_movieTitle)
    TextView mTextViewMovieTitle;
    private Uri mUriMovie;
    private DetailMovie mDetailMovie;
    private ShareActionProvider mShareActionProvider;
    private String mShareMovie;
    private boolean mIsDetailFavoriteFragmentFromActivity;
    private List<Reviews> mReviewsList = new ArrayList<>();
    private List<TrailerDetail> mTrailerDetailList = new ArrayList<>();
    private List<Cast> mCastList = new ArrayList<>();
    private List<Part> mPartList = new ArrayList<>();
    private ReviewMovieAdapter mReviewMovieAdapter;
    private TrailerMovieAdapter mTrailerMovieAdapter;
    private CastingAdapter mCastingAdapter;
    private CollectionAdapter mCollectionAdapter;

    public DetailFavoriteFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(FAVORITE_MOVIE) && savedInstanceState.containsKey
                (IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY)) {

            mIsDetailFavoriteFragmentFromActivity = savedInstanceState.getBoolean(IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY);
            mDetailMovie = savedInstanceState.getParcelable(FAVORITE_MOVIE);

            if (mDetailMovie != null) {
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

    private Intent createShareMovie() {
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

        if (mFloatingButtonFavorite != null) {
            mFloatingButtonFavorite.setOnClickListener(this);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_unfavorite_black_24dp);
            mFloatingButtonFavorite.setImageBitmap(bitmap);
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            //content://com.griffin.popularmovies/favorite/1  -> the number represents the selected movie in the the gridView
            mUriMovie = arguments.getParcelable(FAVORITE_MOVIE);
            mIsDetailFavoriteFragmentFromActivity = arguments.getBoolean(IS_DETAIL_FAVORITE_FRAGMENT_FROM_ACTIVITY);
        }
        if (savedInstanceState != null) {
            setUI();
        }

        //connect recyclerView to a layout manager, and attach an adapter for the data to be displayed
        mRecyclerViewReview.setHasFixedSize(true);
        mRecyclerViewTrailer.setHasFixedSize(true);
        mRecyclerViewCasting.setHasFixedSize(true);
        mRecyclerViewCollection.setHasFixedSize(true);

        // use a linear layout manager , create the *** REVIEWS *** adapter and set it up to the recycler View
        LinearLayoutManager linearLayoutManagerReview = new LinearLayoutManager(getContext());
        linearLayoutManagerReview.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewReview.setLayoutManager(linearLayoutManagerReview);

        mReviewMovieAdapter = new ReviewMovieAdapter(mReviewsList);
        mRecyclerViewReview.setAdapter(mReviewMovieAdapter);

        // use a linear layout manager , create the *** TRAILER *** adapter and set it up to the recycler View
        LinearLayoutManager linearLayoutManagerTrailer = new LinearLayoutManager(getContext());
        linearLayoutManagerTrailer.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewTrailer.setLayoutManager(linearLayoutManagerTrailer);

        mTrailerMovieAdapter = new TrailerMovieAdapter(mTrailerDetailList, getContext());

        mRecyclerViewTrailer.setAdapter(mTrailerMovieAdapter);
        // use a linear layout manager , create the *** CASTING *** adapter and set it up to the recycler View
        LinearLayoutManager linearLayoutManagerCasting = new LinearLayoutManager(getContext());
        linearLayoutManagerCasting.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewCasting.setLayoutManager(linearLayoutManagerCasting);

        mCastingAdapter = new CastingAdapter(mCastList, getContext());
        mRecyclerViewCasting.setAdapter(mCastingAdapter);

        // use a linear layout manager , create the *** COLLECTION *** adapter and set it up to the recycler View
        LinearLayoutManager linearLayoutManagerCollection = new LinearLayoutManager(getContext());
        linearLayoutManagerCollection.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewCollection.setLayoutManager(linearLayoutManagerCollection);

        mCollectionAdapter = new CollectionAdapter(mPartList, getContext());
        mCollectionAdapter.setCallback(this);
        mRecyclerViewCollection.setAdapter(mCollectionAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUriMovie != null) {
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
            if (mDetailMovie.getTrailerDetails() != null && !mDetailMovie.getTrailerDetails().isEmpty()) {
                mTrailerDetailList.clear();
                mTrailerDetailList.addAll(mDetailMovie.getTrailerDetails());

                mTrailerMovieAdapter.notifyDataSetChanged();
            } else {
                mCardViewTrailer.setVisibility(View.GONE);
            }

            if (mDetailMovie.getReviewsList() != null && !mDetailMovie.getReviewsList().isEmpty()) {
                mReviewsList.clear();
                mReviewsList.addAll(mDetailMovie.getReviewsList());

                mReviewMovieAdapter.notifyDataSetChanged();
            } else {
                mCardViewTrailer.setVisibility(View.GONE);
            }

            mCardViewCasting.setVisibility(View.GONE);
            mCardViewCollection.setVisibility(View.GONE);

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

    private void setUI() {

        //set the title text, only on tablet display !
        if (mTextViewMovieTitle != null) {
            mTextViewMovieTitle.setText(mDetailMovie.getMovieDetail().getTitle());
        }

        mTextViewTagLine.setText(mDetailMovie.getMovieDetail().getTagline());

        try {

            mImageViewMoviePicture.setImageBitmap(Utilities.getPoster(mDetailMovie.getMovieDetail().getPosterPath(), mDetailMovie.getMovieDetail().getId()));
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        try {
            mTextViewMovieYear.setText(Utilities.getMonthAndYear(mDetailMovie.getMovieDetail().getReleaseDate()));
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);

        }

        mTextViewOriginalTitle.setText(mDetailMovie.getMovieDetail().getOriginalTitle());

        mTextViewOverview.setText(mDetailMovie.getMovieDetail().getOverview());

        mTextViewMovieRating.setText(String.format(getString(R.string.rating), Double.toString(mDetailMovie.getMovieDetail().getVoteAverage())));

        mTextViewRuntime.setText(String.format(getString(R.string.runtime), Integer.toString(mDetailMovie.getMovieDetail().getRuntime())));


        List<Genre> genres = mDetailMovie.getMovieDetail().getGenres();
        StringBuilder sb = new StringBuilder();
        for (Genre genre : genres) {
            sb.append(genre.getName());

            sb.append(" / ");
        }
        //Set genre Object and update UI
        mTextViewGenre.setText(sb.toString());

        if (mIsDetailFavoriteFragmentFromActivity) {
            ((CallbackDetailFavoriteFragment) getActivity()).setTitleAndPosterOnActivity(mDetailMovie.getMovieDetail().getTitle(),
                    mDetailMovie.getMovieDetail().getPosterPath(), mDetailMovie.getMovieDetail().getId());
        }
    }

    @Override
    public void onCollectionMovieClicked(int idMovie) {

    }

    @Override
    public void onClick(View v) {

        MovieToFavoriteTask movieToFavoriteTask = new MovieToFavoriteTask(getContext().getContentResolver(), this);
        movieToFavoriteTask.startDelete(-1, null, MovieContract.DetailEntry.CONTENT_URI,
                MovieContract.DetailEntry.COLUMN_MOVIE_ID,
                new String[]{Integer.toString(mDetailMovie.getMovieDetail().getId())});

        if (v.getId() == R.id.floatingButton_favorite_tablet) {
            ((CallbackFavorite) getActivity()).onClickFavoriteMovie();
        }
        if (v.getId() == R.id.floatingButton_favorite) {
            getActivity().onBackPressed();

        }
    }

    @Override
    public void onQueryComplete(Cursor data, int token) {

    }

    @Override
    public void onInsertComplete(Uri uri) {

    }

    @Override
    public void onDeleteComplete(int result) {

    }

    public interface CallbackFavorite {
        void onClickFavoriteMovie();
    }

    public interface CallbackDetailFavoriteFragment {
        void setTitleAndPosterOnActivity(String title, String posterPath, int idMovie);
    }


}
