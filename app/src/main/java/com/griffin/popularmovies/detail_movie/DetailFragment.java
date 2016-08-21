package com.griffin.popularmovies.detail_movie;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.Pojo.Collection;
import com.griffin.popularmovies.Pojo.Genre;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.Pojo.Part;
import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.adapter.CastingAdapter;
import com.griffin.popularmovies.adapter.CollectionAdapter;
import com.griffin.popularmovies.adapter.ReviewMovieAdapter;
import com.griffin.popularmovies.adapter.TrailerMovieAdapter;
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<DetailMovie> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String MOVIE = "MOVIE";
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;
    private ProgressDialog mProgressDialog;

    private static final int DETAIL_LOADER = 0;

    ///@BindView(R.id.textView_Title) TextView mTitleTextView;
    @BindView(R.id.imageView_Picture)
    ImageView mImageViewMoviePicture;
    @BindView(R.id.textView_Year)
    TextView mTextViewMovieYear;
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
    @BindView(R.id.shineButton_favorite)
    ShineButton mFavoriteButton;
    @BindView(R.id.textView_tagline)
    TextView mTextViewTagline;
    @BindView(R.id.textView_Collection_main_title) TextView mTextViewCollectionMainTitle;
    @BindView(R.id.cardViewReview)
    CardView mCardViewReview;
    @BindView(R.id.recyclerView_review)
    RecyclerView mRecyclerViewReview;
    @BindView(R.id.recyclerView_trailer)
    RecyclerView mRecyclerViewTrailer;
    @BindView(R.id.recyclerView_casting) RecyclerView mRecyclerViewCasting;
    @BindView(R.id.recyclerView_collection) RecyclerView mRecyclerViewCollection;
    @BindView(R.id.cardViewCollection) CardView mCardViewCollection;
    @BindView(R.id.cardViewCasting) CardView mCardViewCasting;
    @BindView(R.id.cardViewTrailer) CardView mCardViewTrailer;

    private List<Reviews> mReviewsList;
    private List<TrailerDetail> mTrailerDetailList;
    private List<Cast> mCastList;
    private List<Part> mPartList;

    private ReviewMovieAdapter mReviewMovieAdapter;
    private TrailerMovieAdapter mTrailerMovieAdapter;
    private CastingAdapter mCastingAdapter;
    private CollectionAdapter mCollectionAdapter;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if no previous state
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE)) {
            mMovie = null;
            mReviewsList = new ArrayList<>();
            mTrailerDetailList = new ArrayList<>();
            mCastList = new ArrayList<>();
            mPartList = new ArrayList<>();
        }

        //restore the previous state
        else {
            mMovie = savedInstanceState.getParcelable(MOVIE);
            if(mMovie  != null){
                mReviewsList = mMovie.getDetailMovie().getReviewsList();
                mTrailerDetailList = mMovie.getDetailMovie().getTrailerDetails();
                mCastList = mMovie.getDetailMovie().getCredits().getCast();
                mPartList = mMovie.getDetailMovie().getCollection().getParts();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save the movie
        outState.putParcelable(MOVIE, mMovie);
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_movie_fragment, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null && savedInstanceState == null) {
            mMovie = arguments.getParcelable(MOVIE);
        }
        if(savedInstanceState != null){
            setDetailUI();
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
        linearLayoutManagerTrailer.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewTrailer.setLayoutManager(linearLayoutManagerTrailer);

        mTrailerMovieAdapter = new TrailerMovieAdapter(mTrailerDetailList);
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

        mCollectionAdapter = new CollectionAdapter(mPartList);
        mRecyclerViewCollection.setAdapter(mCollectionAdapter);


        mFavoriteButton.init(getActivity());

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {

            Movie movie = mMovie;

            @Override
            public void onClick(View v) {
                if (mFavoriteButton.isChecked()) {
                    Picasso.with(getContext()).load(getString(R.string.IMAGE_BASE_URL) + movie.getPosterPath()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            String localUrl = Utilities.savePoster(bitmap, movie.getId(), getActivity().getApplicationContext());
                            movie.setPosterPath(localUrl);
                            movie.setFavorite(1);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                    Utilities.addMovieToFavorite(movie, getContext());


                } else {
                    movie.setFavorite(0);
                    Utilities.removeMovieFromFavorite(movie, getContext());
                }
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mProgressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
            mProgressDialog.setTitle("Connecting to themovieDB.org");
            mProgressDialog.setMessage("Loading data...");

            mProgressDialog.show();

            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }


    }

    @Override
    public Loader<DetailMovie> onCreateLoader(int id, Bundle args) {

        return new FetchDetailMovieTask(getActivity(), mMovie.getId());
    }

    @Override
    public void onLoadFinished(Loader<DetailMovie> loader, DetailMovie detailMovie) {

        mMovie.setDetail(detailMovie);

        //clear the review List and add the new one to the adapter
        if(mMovie.getDetailMovie().getReviewsList() != null) {
            mReviewsList.clear();
            mReviewsList.addAll(mMovie.getDetailMovie().getReviewsList());

            mReviewMovieAdapter.notifyDataSetChanged();
        }
        else if(mMovie.getDetailMovie().getReviewsList() == null){
            mCardViewReview.setVisibility(View.GONE);
        }

        //clear the trailer List and add the new one to the adapter
        if(mMovie.getDetailMovie().getTrailerDetails() != null){
            mTrailerDetailList.clear();
            mTrailerDetailList.addAll(mMovie.getDetailMovie().getTrailerDetails());

            mTrailerMovieAdapter.notifyDataSetChanged();
        }
        else if(mMovie.getDetailMovie().getTrailerDetails() == null){
            mCardViewTrailer.setVisibility(View.GONE);
        }


        //clear the Casting List and add the new one to the adapter
        if(mMovie.getDetailMovie().getCredits().getCast() != null) {
            mCastList.clear();
            mCastList.addAll(mMovie.getDetailMovie().getCredits().getCast());

            mCastingAdapter.notifyDataSetChanged();
        }
        else if(mMovie.getDetailMovie().getCredits().getCast() == null) {
            mCardViewCasting.setVisibility(View.GONE);
        }

        //clear the Casting List and add the new one to the adapter
        if(mMovie.getDetailMovie().getCollection() != null && !mMovie.getDetailMovie().getCollection().getParts().isEmpty()) {
            mPartList.clear();
            mPartList.addAll(mMovie.getDetailMovie().getCollection().getParts());

            mCollectionAdapter.notifyDataSetChanged();
            mTextViewCollectionMainTitle.setText(mMovie.getDetailMovie().getCollection().getName());
        }
        else if(mMovie.getDetailMovie().getCollection() == null || mMovie.getDetailMovie().getCollection().getParts().isEmpty()){
            mCardViewCollection.setVisibility(View.GONE);
        }



        //Display the UI with new elements
        setDetailUI();

        mProgressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<DetailMovie> loader) {

    }

    private void setDetailUI() {

        Picasso.with(getActivity())
                .load(getString(R.string.IMAGE_BASE_URL) + mMovie.getPosterPath())
                .fit()
                .centerInside()
                .into(mImageViewMoviePicture);

        String date = mMovie.getReleaseDate();

        mTextViewMovieYear.setText(Utilities.getMonthAndYear(date));

        String title = mMovie.getTitle();
        //mTitleTextView.setText(title);

        String originalTitle = mMovie.getOriginalTitle();
        mTextViewOriginalTitle.setText(originalTitle);

        String tagline = mMovie.getDetailMovie().getMovieDetail().getTagline();
        if (tagline != null) {
            if (tagline.isEmpty()) {
                mTextViewTagline.setVisibility(View.GONE);
            } else {
                mTextViewTagline.setText(tagline);
            }
        }

        String overview = mMovie.getOverview();
        mTextViewOverview.setText(overview);

        String rating = Double.toString(mMovie.getVoteAverage());
        String rating_text = String.format(getString(R.string.rating), rating);
        mTextViewMovieRating.setText(rating_text);

        if (mMovie.getFavorite() == 0) {
            mFavoriteButton.setChecked(false);
        } else mFavoriteButton.setChecked(true);


        final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
        final String PARAM = "v";

        //set Casting to movie Object and update UI
        int maxActors;
        int mNumberMaxDisplayedActors = 5;
        if (mMovie.getDetailMovie().getCredits().getCast().size() > mNumberMaxDisplayedActors) {
            maxActors = mNumberMaxDisplayedActors;
        } else maxActors = mMovie.getDetailMovie().getCredits().getCast().size();



        //Set genre Object and update UI
        List<Genre> genres = mMovie.getDetailMovie().getMovieDetail().getGenres();
        StringBuilder sb = new StringBuilder();


        for (Genre genre : genres) {
            sb.append(genre.getName());

            if (genres.iterator().hasNext()) {
                sb.append(" / ");
            }
        }

        mTextViewGenre.setText(sb);

        String runtime = String.format(getString(R.string.runtime), mMovie.getDetailMovie().getMovieDetail().getRuntime());
        mTextViewRuntime.setText(runtime);

    }


}
