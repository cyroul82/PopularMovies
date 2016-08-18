package com.griffin.popularmovies.detail_movie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import butterknife.BindView;
import butterknife.ButterKnife;

import com.griffin.popularmovies.Pojo.Genre;
import com.griffin.popularmovies.Pojo.Movie;
import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<DetailMovie>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String MOVIE = "MOVIE";
    public static final String DETAIL_MOVIE="DETAILMOVIE";
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;
    private ProgressDialog mProgressDialog;


    private static final int DETAIL_LOADER = 0;

    @BindView(R.id.textView_Title) TextView mTitleTextView;
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
    @BindView(R.id.textView_tagline) TextView mTextViewTagline;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE)){
            mMovie = null;
        }
        //restore the previous state
        else {
            mMovie = savedInstanceState.getParcelable(DetailFragment.MOVIE);
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
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(MOVIE);
        }

        ButterKnife.bind(this, rootView);

        mFavoriteButton.init(getActivity());

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {

            Movie movie = mMovie;
            @Override
            public void onClick(View v) {
                if(mFavoriteButton.isChecked()) {
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


                }
                else {
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
        if(mMovie != null){
            mProgressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
            mProgressDialog.setTitle("Connecting to themovieDB.org");
            mProgressDialog.setMessage("Loading data...");

            mProgressDialog.show();
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public Loader<DetailMovie> onCreateLoader(int id, Bundle args) {

        return new FetchDetailMovieTask(getActivity(), mMovie.getId());
    }

    @Override
    public void onLoadFinished(Loader<DetailMovie> loader, DetailMovie detailMovie) {
        mProgressDialog.dismiss();
        mMovie.setDetail(detailMovie);

        Picasso.with(getActivity())
                .load(getString(R.string.IMAGE_BASE_URL) + mMovie.getPosterPath())
                .into(mImageViewMoviePicture);

        String date = mMovie.getReleaseDate();

        mTextViewMovieYear.setText(Utilities.getMonthAndYear(date));

        String title = mMovie.getTitle();
        mTitleTextView.setText(title);

        String originalTitle = mMovie.getOriginalTitle();
        mTextViewOriginalTitle.setText(originalTitle);

        String tagline = mMovie.getDetailMovie().getMovieDetail().getTagline();
        if (tagline != null){
            if(tagline.isEmpty()) {
                mTextViewTagline.setVisibility(View.GONE);
            }
        }

        String overview = mMovie.getOverview();
        mTextViewOverview.setText(overview);

        String rating = Double.toString(mMovie.getVoteAverage());
        String rating_text = String.format(getString(R.string.rating),rating);
        mTextViewMovieRating.setText(rating_text);

        if (mMovie.getFavorite() == 0){
            mFavoriteButton.setChecked(false);
        }
        else mFavoriteButton.setChecked(true);

        setDetailUI();



    }

    @Override
    public void onLoaderReset(Loader<DetailMovie> loader) {

    }

    private void setDetailUI(){

        final String YOUTUBE_BASE_URL="https://www.youtube.com/watch?";
        final String PARAM = "v";

        //set Casting to movie Object and update UI
        int maxActors;
        int mNumberMaxDisplayedActors = 5;
        if(mMovie.getDetailMovie().getCredits().getCast().size() > mNumberMaxDisplayedActors){
            maxActors = mNumberMaxDisplayedActors;
        }
        else maxActors = mMovie.getDetailMovie().getCredits().getCast().size();

        StringBuilder casting = new StringBuilder();
        for (int i=0 ; i < maxActors  ; i++){
            casting.append(mMovie.getDetailMovie().getCredits().getCast().get(i).getName()).append("  (").append(mMovie.getDetailMovie().getCredits()
                    .getCast().get(i).getCharacter()).append(")\n");
        }
        mTextViewCasting.setText(casting);

        //Set genre Object and update UI
        List<Genre> genres = mMovie.getDetailMovie().getMovieDetail().getGenres();
        StringBuilder sb = new StringBuilder();


        for(Genre genre : genres){
            sb.append(genre.getName());

            if (genres.iterator().hasNext()){
                sb.append(" / ");
            }
        }

        mTextViewGenre.setText(sb);


        //set review Object and update UI
        mLinearLayoutReview.removeAllViews();
        for (Reviews reviews : mMovie.getDetailMovie().getReviewsList()){
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(textViewLayoutParams);
            textViewLayoutParams.setMargins(0,0,0,10);
            String review = String.format(getString(R.string.reviews),reviews.getAuthor(), reviews.getContent());
            textView.setText(review);
            textView.setBackground(getResources().getDrawable(R.drawable.border_top, null));
            mLinearLayoutReview.addView(textView);

        }


        //set Trailer to movie Object and update UI
        mLinearLayoutTrailer.removeAllViews();
        for(final TrailerDetail trailerDetail : mMovie.getDetailMovie().getTrailerDetails()){
            Button button  = new Button(getActivity());
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(buttonLayoutParams);
            buttonLayoutParams.setMargins(0,0,0,10);

            //button.setCompoundDrawablePadding(5);
            //button.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24dp), null, null, null);
            // button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            button.setText(trailerDetail.getName());
            button.setBackground(getResources().getDrawable(R.drawable.button_selector, null));
            button.setPadding(10, 10, 10, 10);// in pixels (left, top, right, bottom)
            mLinearLayoutTrailer.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(YOUTUBE_BASE_URL).buildUpon().appendQueryParameter(PARAM, trailerDetail.getKey()).build();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        String runtime = String.format(getString(R.string.runtime),mMovie.getDetailMovie().getMovieDetail().getRuntime());
        mTextViewRuntime.setText(runtime);

    }




}
