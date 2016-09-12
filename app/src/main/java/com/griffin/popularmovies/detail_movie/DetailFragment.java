package com.griffin.popularmovies.detail_movie;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.pojo.Backdrop;
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
import com.griffin.popularmovies.movie_list.BlankFragment;
import com.griffin.popularmovies.task.MovieToFavoriteTask;
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<DetailMovie>, OnClickListener, CollectionAdapter
        .CallbackCollectionAdapter, MovieToFavoriteTask.OnQueryCompleteListener {

    public static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    public static final String IS_DETAIL_FRAGMENT_FROM_ACTIVITY = "idffa";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final String SHORTBRAIN_SHARE_HASHTAG = "#Shortbrain";
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
    private MovieToFavoriteTask movieToFavoriteTask;
    private DetailMovie mDetailMovie;
    private ShareActionProvider mShareActionProvider;
    private ProgressDialog mProgressDialog;
    private int mIdMovie;
    private List<Reviews> mReviewsList = new ArrayList<>();
    private List<TrailerDetail> mTrailerDetailList = new ArrayList<>();
    private List<Cast> mCastList = new ArrayList<>();
    private List<Part> mPartList = new ArrayList<>();
    private ReviewMovieAdapter mReviewMovieAdapter;
    private TrailerMovieAdapter mTrailerMovieAdapter;
    private CastingAdapter mCastingAdapter;
    private CollectionAdapter mCollectionAdapter;
    private String mShareMovie;
    private boolean mIsDetailFragmentFromActivity;
    private String posterPath;

    public DetailFragment() {
        //To display the menu , do not forget !!!
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if no previous state
        if (savedInstanceState != null && savedInstanceState.containsKey(DETAIL_MOVIE) && savedInstanceState.containsKey(IS_DETAIL_FRAGMENT_FROM_ACTIVITY)) {

            mDetailMovie = savedInstanceState.getParcelable(DETAIL_MOVIE);

            if (mDetailMovie != null) {

                if (mDetailMovie.getReviewsList() != null) {
                    mReviewsList = mDetailMovie.getReviewsList();
                }

                if (mDetailMovie.getTrailerDetails() != null) {
                    mTrailerDetailList = mDetailMovie.getTrailerDetails();
                }

                if (mDetailMovie.getCredits() != null) {
                    mCastList = mDetailMovie.getCredits().getCast();
                }

                if (mDetailMovie.getCollection() != null) {
                    mPartList = mDetailMovie.getCollection().getParts();
                }
            }

            mIsDetailFragmentFromActivity = savedInstanceState.getBoolean(IS_DETAIL_FRAGMENT_FROM_ACTIVITY);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save the movie
        outState.putParcelable(DETAIL_MOVIE, mDetailMovie);
        outState.putBoolean(IS_DETAIL_FRAGMENT_FROM_ACTIVITY, mIsDetailFragmentFromActivity);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuShare = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);

        if (mShareMovie != null) {
            mShareActionProvider.setShareIntent(createShareMovie());

        }

    }

    private Intent createShareMovie() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "\n" + mShareMovie + "\n" + SHORTBRAIN_SHARE_HASHTAG);

        // Always use string resources for UI text.
        String title = getResources().getString(R.string.share_movie);
        // Create intent to show chooser
        return Intent.createChooser(shareIntent, title);
    }


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);

            ButterKnife.bind(this, rootView);

            if (mFloatingButtonFavorite != null) {
                mFloatingButtonFavorite.setOnClickListener(this);
            }

            Bundle arguments = getArguments();
            if (arguments != null && savedInstanceState == null) {
                mIdMovie = arguments.getInt(DETAIL_MOVIE);
                mIsDetailFragmentFromActivity = arguments.getBoolean(IS_DETAIL_FRAGMENT_FROM_ACTIVITY);
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
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {

                case android.R.id.home:
                    getActivity().onBackPressed();
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState == null) {
                getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            }

        }

        @Override
        public Loader<DetailMovie> onCreateLoader (int id, Bundle args){
            // show ProgressDialog
            mProgressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
            mProgressDialog.setTitle("Connecting to themovieDB.org");
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mProgressDialog.dismiss();
                }
            });
            mProgressDialog.show();
            return new FetchDetailMovieTask(getActivity(), mIdMovie);
        }

        @Override
        public void onLoadFinished (Loader < DetailMovie > loader, DetailMovie detailMovie){
            if (detailMovie != null) {
                if (detailMovie.getMovieDetail().getId() != 0) {
                    mDetailMovie = detailMovie;
                }
                //clear the review List and add the new one to the adapter
                if (mDetailMovie.getReviewsList() != null && !mDetailMovie.getReviewsList().isEmpty()) {
                    mReviewsList.clear();
                    mReviewsList.addAll(mDetailMovie.getReviewsList());
                    mReviewMovieAdapter.notifyDataSetChanged();

                } else {
                    mCardViewReview.setVisibility(View.GONE);
                }

                //clear the trailer List and add the new one to the adapter
                if (mDetailMovie.getTrailerDetails() != null && !mDetailMovie.getTrailerDetails().isEmpty()) {
                    mTrailerDetailList.clear();
                    mTrailerDetailList.addAll(mDetailMovie.getTrailerDetails());
                    mTrailerMovieAdapter.notifyDataSetChanged();

                } else {
                    mCardViewTrailer.setVisibility(View.GONE);
                }

                //clear the Casting List and add the new one to the adapter
                if (mDetailMovie.getCredits().getCast() != null && !mDetailMovie.getCredits().getCast().isEmpty()) {
                    mCastList.clear();
                    mCastList.addAll(mDetailMovie.getCredits().getCast());

                    mCastingAdapter.notifyDataSetChanged();
                } else {
                    mCardViewCasting.setVisibility(View.GONE);
                }

                //clear the Casting List and add the new one to the adapter
                if (mDetailMovie.getCollection() != null && !mDetailMovie.getCollection().getParts().isEmpty()) {
                    mPartList.clear();
                    mPartList.addAll(mDetailMovie.getCollection().getParts());

                    mCollectionAdapter.notifyDataSetChanged();
                    mTextViewCollectionMainTitle.setText(mDetailMovie.getCollection().getName());

                } else {
                    mCardViewCollection.setVisibility(View.GONE);
                }

                //Display the UI with new elements
                setUI();

                mShareMovie = String.format("%s \n%s", mDetailMovie.getMovieDetail().getTitle(), mDetailMovie.getMovieDetail().getOverview());

                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovie());
                }
            } else {
                if (mIsDetailFragmentFromActivity) {

                    getActivity().onBackPressed();
                } else {
                    final int WHAT = 1;
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == WHAT) {
                                Bundle b = new Bundle();
                                b.putString(MainActivity.TITLE_BLANK_FRAGMENT_KEY, getString(R.string.error_loading));
                                BlankFragment blankFragment = new BlankFragment();
                                blankFragment.setArguments(b);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.detail_movie_container, blankFragment, MainActivity.BLANK_FRAGMENT_TAG)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }
                    };
                    handler.sendEmptyMessage(WHAT);

                }
            }

            // close ProgressDialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onLoaderReset (Loader < DetailMovie > loader) {
            // close ProgressDialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        //set the UI elements

    private void setUI() {

        //set the title text, only on tablet display !
        if (mTextViewMovieTitle != null) {
            mTextViewMovieTitle.setText(mDetailMovie.getMovieDetail().getTitle());
        }

        //load the picture using picasso library
        Picasso.with(getActivity())
                .load(getString(R.string.IMAGE_BASE_URL) + mDetailMovie.getMovieDetail().getPosterPath())
                .placeholder(R.drawable.ic_wallpaper_black_48dp)
                .error(R.drawable.ic_wallpaper_black_48dp)
                .fit()
                .centerInside()
                .into(mImageViewMoviePicture);


        //Set the month and the year of the movie text
        String date = mDetailMovie.getMovieDetail().getReleaseDate();
        try {
            mTextViewMovieYear.setText(Utilities.getMonthAndYear(date));
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);

        }

        //set the original title text
        if (!mDetailMovie.getMovieDetail().getOriginalTitle().equals(mDetailMovie.getMovieDetail().getTitle())) {
            String originalTitle = mDetailMovie.getMovieDetail().getOriginalTitle();
            mTextViewOriginalTitle.setText(originalTitle);
        } else {
            mTextViewOriginalTitle.setVisibility(View.GONE);
            mTextViewOriginalTitleText.setVisibility(View.GONE);
        }

        //set the tagline text
        String tagLine = mDetailMovie.getMovieDetail().getTagline();
        if (tagLine != null) {
            if (tagLine.isEmpty()) {
                mTextViewTagLine.setVisibility(View.GONE);
            } else {
                mTextViewTagLine.setText(tagLine);
            }
        }

        //set the overview text
        String overview = mDetailMovie.getMovieDetail().getOverview();
        mTextViewOverview.setText(overview);

        //set the rating text
        String rating = Double.toString(mDetailMovie.getMovieDetail().getVoteAverage());
        String rating_text = String.format(getString(R.string.rating), rating);
        mTextViewMovieRating.setText(rating_text);

        //Set genre Object and update UI
        List<Genre> genres = mDetailMovie.getMovieDetail().getGenres();

        if (genres != null && !genres.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            Iterator<Genre> iteratorGenre = genres.iterator();
            while (iteratorGenre.hasNext()) {
                Genre genre = iteratorGenre.next();
                sb.append(genre.getName());
                if (iteratorGenre.hasNext()) {
                    sb.append(" / ");
                }
            }
            mTextViewGenre.setText(sb);
        } else {
            mTextViewGenre.setVisibility(View.GONE);
        }

        String runtime = String.format(getString(R.string.runtime), mDetailMovie.getMovieDetail().getRuntime());
        mTextViewRuntime.setText(runtime);

        if (mIsDetailFragmentFromActivity) {
            ((CallbackDetailFragment) getActivity()).setTitleAndPosterOnActivity(mDetailMovie.getMovieDetail().getTitle(), mDetailMovie
                    .getMovieDetail()
                    .getPosterPath(), mDetailMovie.getMovieImages().getBackdrops());
        }

        movieToFavoriteTask = new MovieToFavoriteTask(getContext().getContentResolver(), this);
        movieToFavoriteTask.startQuery(MovieToFavoriteTask.IS_MOVIE_FAVORITE_TOKEN, null,
                //The URI content://com.griffin.popularmovies :
                MovieContract.DetailEntry.CONTENT_URI,
                //The list of which columns to return, in this case only the _ID column
                new String[]{MovieContract.DetailEntry._ID},
                    /* The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs)) */
                MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                //only one clause movie_id
                new String[]{Long.toString(mDetailMovie.getMovieDetail().getId())},
                null);


    }

    @Override
    public void onCollectionMovieClicked(int idMovie) {
        if (idMovie != mIdMovie) {

            if(mIsDetailFragmentFromActivity) {
                int stack = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < stack; i++) {
                    FragmentManager.BackStackEntry bse = getActivity().getSupportFragmentManager().getBackStackEntryAt(i);
                    if (bse.getName().equals(Integer.toString(idMovie))) {
                        getActivity().getSupportFragmentManager().popBackStack(Integer.toString(idMovie), 0);
                        return;
                    }
                }
            }

            Bundle args = new Bundle();
            args.putInt(DETAIL_MOVIE, idMovie);
            args.putBoolean(IS_DETAIL_FRAGMENT_FROM_ACTIVITY, mIsDetailFragmentFromActivity);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, df, MainActivity.DETAIL_FRAGMENT_TAG)
                    .addToBackStack(Integer.toString(idMovie))
                    .commit();
        }
    }

    @Override
    public void onClick(final View v) {

        if (v.getId() == R.id.floatingButton_favorite || v.getId() == R.id.floatingButton_favorite_tablet) {

            Picasso.with(getContext()).load(getString(R.string.IMAGE_BASE_URL) + mDetailMovie.getMovieDetail().getPosterPath()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    posterPath = Utilities.savePoster(bitmap, mDetailMovie.getMovieDetail().getId(),
                            getActivity().getApplicationContext());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

            movieToFavoriteTask.startQuery(MovieToFavoriteTask.ON_CLICK_FAVORITE_TOKEN, null,
                    //The URI content://com.griffin.popularmovies
                    MovieContract.DetailEntry.CONTENT_URI,
                    //The list of which columns to return, in this case only the _ID column
                    new String[]{MovieContract.DetailEntry._ID},
                    /* The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs)) */
                    MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                    //only one clause movie_id
                    new String[]{Long.toString(mDetailMovie.getMovieDetail().getId())},
                    null);

        }

    }

    @Override
    public void onQueryComplete(Cursor data, int token) {
        if(token == MovieToFavoriteTask.ON_CLICK_FAVORITE_TOKEN) {
            if (data != null) {
                if (!data.moveToFirst()) {
                    ContentValues detail = new ContentValues();
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, mDetailMovie.getMovieDetail().getId());

                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_PICTURE, posterPath);

                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, mDetailMovie.getMovieDetail().getTitle());

                    Gson gson = new GsonBuilder().create();
                    String casting = gson.toJson(mDetailMovie.getCredits().getCast());
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_CASTING, casting);

                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_DATE, mDetailMovie.getMovieDetail().getReleaseDate());

                    String genre = gson.toJson(mDetailMovie.getMovieDetail().getGenres());
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_GENRE, genre);

                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE, mDetailMovie.getMovieDetail().getOriginalTitle());
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW, mDetailMovie.getMovieDetail().getOverview());
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RATING, Double.toString(mDetailMovie.getMovieDetail().getVoteAverage()));

                    String reviews = gson.toJson(mDetailMovie.getReviewsList());
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS, reviews);
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME, mDetailMovie.getMovieDetail().getRuntime());

                    String trailers = gson.toJson(mDetailMovie.getTrailerDetails());
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER, trailers);
                    detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TAGLINE, mDetailMovie.getMovieDetail().getTagline());

                    movieToFavoriteTask.startInsert(-1, null, MovieContract.DetailEntry.CONTENT_URI, detail);
                } else {
                    movieToFavoriteTask.startDelete(-1, null, MovieContract.DetailEntry.CONTENT_URI,
                            MovieContract.DetailEntry.COLUMN_MOVIE_ID,
                            new String[]{Integer.toString(mDetailMovie.getMovieDetail().getId())});
                }
            }
        }
        if(token == MovieToFavoriteTask.IS_MOVIE_FAVORITE_TOKEN){
            if(data != null){
                if(data.moveToFirst()){
                    Bitmap bitmap= BitmapFactory.decodeResource(getContext().getResources(),
                            R.drawable.ic_unfavorite_black_24dp);
                    if(mFloatingButtonFavorite != null) {
                        mFloatingButtonFavorite.setImageBitmap(bitmap);
                    }
                    if (mFloatingButtonFavorite == null) {
                        ((CallbackDetailFragment) getActivity()).setFloatingButtonFavorite(true);
                    }
                }
                else {
                    Bitmap bitmap= BitmapFactory.decodeResource(getContext().getResources(),
                            R.drawable.ic_favorite_black_24dp);
                    if(mFloatingButtonFavorite != null) {
                        mFloatingButtonFavorite.setImageBitmap(bitmap);
                    }
                    if (mFloatingButtonFavorite == null) {
                        ((CallbackDetailFragment) getActivity()).setFloatingButtonFavorite(false);
                    }
                }
            }
        }
    }

    @Override
    public void onInsertComplete(Uri uri) {
        Bitmap bitmap= BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.ic_unfavorite_black_24dp);
        if(mFloatingButtonFavorite != null) {
            mFloatingButtonFavorite.setImageBitmap(bitmap);
        }else {
            ((CallbackDetailFragment) getActivity()).setFloatingButtonFavorite(true);
        }
        Toast.makeText(getContext(), "Added to your favorite", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteComplete(int result) {
        Bitmap bitmap= BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.ic_favorite_black_24dp);
        if(mFloatingButtonFavorite != null) {
            mFloatingButtonFavorite.setImageBitmap(bitmap);
        }else {
            ((CallbackDetailFragment) getActivity()).setFloatingButtonFavorite(false);
        }
        Toast.makeText(getContext(), "Deleted form your favorite", Toast.LENGTH_SHORT).show();
    }


    public interface CallbackDetailFragment {
        void setTitleAndPosterOnActivity(String title, String posterPath, List<Backdrop> posterList);

        void setFloatingButtonFavorite(boolean b);
    }
}
