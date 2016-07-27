package com.griffin.popularmovies.detail_movie;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.griffin.popularmovies.ReviewMovie;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.movie_list.Movie;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ExtraDetailMovie>{

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
    private TextView mTextViewRuntime;
    private LinearLayout mLinearLayoutTrailer;
    private LinearLayout mLinearLayoutReview;
    private ShineButton mFavoriteButton;

    private ExtraDetailMovie mExtraDetailMovie;

    public DetailMovieFragment() {
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creates a new List of movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(EXTRA_DETAIL_MOVIE)){
            mExtraDetailMovie = null;
        }
        //restore the previous state
        else {
            mExtraDetailMovie = savedInstanceState.getParcelable(DetailMovieFragment.EXTRA_DETAIL_MOVIE);
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
        MenuItem menuShare = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);


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
        mLinearLayoutTrailer = (LinearLayout) rootView.findViewById(R.id.linearLayout_trailer);
        mLinearLayoutReview = (LinearLayout) rootView.findViewById(R.id.linearLayout_Review);
        mFavoriteButton = (ShineButton) rootView.findViewById(R.id.shineButton_favorite);
        mTextViewRuntime = (TextView) rootView.findViewById(R.id.textView_runtime);

        mFavoriteButton.init(getActivity());

        Bundle arguments = getArguments();

        if (arguments != null) {

            mMovie = arguments.getParcelable(DetailMovieFragment.DETAIL_MOVIE);


            String urlPicture = null;
            if (mMovie != null) {
                urlPicture = mMovie.getUrl();
            }
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
            String rating_text = String.format(getString(R.string.rating),rating);
            mTextViewMovieRating.setText(rating_text);

            if (mMovie.getFavorite() == 0){
                mFavoriteButton.setChecked(false);
            }
            else mFavoriteButton.setChecked(true);

            if (mExtraDetailMovie != null) {
                setExtraDetail();
            }

        }




        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavoriteButton.isChecked()) {
                    Utilities.addMovieToFavorite(mMovie, getContext());

                }
                else {
                    Utilities.removeMovie(mMovie, getContext());
                }
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(mMovie != null && mExtraDetailMovie == null) getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<ExtraDetailMovie> onCreateLoader(int id, Bundle args) {
        return new FetchDetailMovieTask(getActivity(), mMovie);

    }

    @Override
    public void onLoadFinished(Loader<ExtraDetailMovie> loader, ExtraDetailMovie extraDetailMovie) {
        mExtraDetailMovie = extraDetailMovie;
       if (mExtraDetailMovie != null) {
           setExtraDetail();
        }
    }

    @Override
    public void onLoaderReset(Loader<ExtraDetailMovie> loader) {

    }

    private void setExtraDetail(){

        final String YOUTUBE_BASE_URL="https://www.youtube.com/watch?";
        final String PARAM = "v";

        //set Actors to movie Object and update UI
        mMovie.setActors(mExtraDetailMovie.getActors());
        int maxActors;
        int mNumberMaxDisplayedActors = 3;
        if(mExtraDetailMovie.getActors().size() > mNumberMaxDisplayedActors){
            maxActors = mNumberMaxDisplayedActors;
        }
        else maxActors = mExtraDetailMovie.getActors().size();

        for (int i=0 ; i < maxActors  ; i++){
            mTextViewActor.append(mMovie.getActors().get(i).getName() + "  (" + mMovie.getActors().get(i).getCharacter() + ")\n");
        }

        //Set genre Object and update UI
        mMovie.setGenre(mExtraDetailMovie.getGenre());
        String[] genres = mMovie.getGenre();
        for (int i = 0 ; i < genres.length ; i++){
            StringBuilder sb = new StringBuilder();
            mTextViewGenre.append(genres[i]);
            if(i != genres.length-1){
                mTextViewGenre.append(" / ");
            }
        }

        //set review Object and update UI
        mMovie.setReviewMovieList(mExtraDetailMovie.getReviewMovieList());
        for (ReviewMovie reviewMovie : mMovie.getReviewMovieList()){
            TextView textView = new TextView(getActivity());
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textViewLayoutParams);
            textViewLayoutParams.setMargins(0,0,0,10);
            String review = String.format(getString(R.string.reviews),reviewMovie.getAuthor(), reviewMovie.getReview());
            textView.setText(review);
            textView.setBackground(getResources().getDrawable(R.drawable.border_top));

            mLinearLayoutReview.addView(textView);
        }


        //set Trailer to movie Object and update UI
        mMovie.setTrailers(mExtraDetailMovie.getTrailers());
        for(final TrailerMovie trailerMovie : mMovie.getTrailers()){
            Button button  = new Button(getActivity());
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(buttonLayoutParams);
            buttonLayoutParams.setMargins(0,0,0,10);
            button.setText(trailerMovie.getNameTrailer());
            //button.setCompoundDrawablePadding(5);
            //button.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24dp), null, null, null);
           // button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            button.setBackground(getResources().getDrawable(R.drawable.button_selector, null));
            button.setPadding(10, 10, 10, 10);// in pixels (left, top, right, bottom)
            mLinearLayoutTrailer.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(YOUTUBE_BASE_URL).buildUpon().appendQueryParameter(PARAM, trailerMovie.getKeyTrailer()).build();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        mMovie.setRuntime(mExtraDetailMovie.getRuntime());
        String runtime = String.format(getString(R.string.runtime),mMovie.getRuntime());
        mTextViewRuntime.setText(runtime);

    }




}
