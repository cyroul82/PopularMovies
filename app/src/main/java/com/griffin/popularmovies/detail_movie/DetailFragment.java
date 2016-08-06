package com.griffin.popularmovies.detail_movie;

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
import android.util.Log;
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

import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.movie_list.Movie;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.task.FetchDetailMovieTask;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<DetailMovie>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String DETAIL_MOVIE = "MOVIE";
    public static final String EXTRA_DETAIL_MOVIE="EXTRAMOVIE";
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;


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


    public DetailFragment() {
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(EXTRA_DETAIL_MOVIE)){
            mMovie = null;
        }
        //restore the previous state
        else {
            mMovie = savedInstanceState.getParcelable(DetailFragment.EXTRA_DETAIL_MOVIE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_DETAIL_MOVIE, mMovie);
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
            mMovie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);
        }
        ButterKnife.bind(this, rootView);

        mFavoriteButton.init(getActivity());

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavoriteButton.isChecked()) {
                    Picasso.with(getContext()).load(mMovie.getPicture_url()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            String localUrl = Utilities.savePoster(bitmap, mMovie.getId(), getActivity().getApplicationContext());
                            Log.i(LOG_TAG, localUrl);
                            mMovie.setPicture_url(localUrl);
                            Utilities.addMovieToFavorite(mMovie, getContext());
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

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
        if(mMovie != null){
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public Loader<DetailMovie> onCreateLoader(int id, Bundle args) {
        return new FetchDetailMovieTask(getActivity(), mMovie);
    }

    @Override
    public void onLoadFinished(Loader<DetailMovie> loader, DetailMovie detailMovie) {

        mMovie.setDetail(detailMovie);

        String urlPicture = null;

        Picasso.with(getActivity())
                .load(mMovie.getPicture_url())
                .into(mImageViewMoviePicture);

        String date = mMovie.getDate();
        mTextViewMovieYear.setText(date);

        String title = mMovie.getTitle();
        mTitleTextView.setText(title);

        String originalTitle = mMovie.getOriginalTitle();
        mTextViewOriginalTitle.setText(originalTitle);

        String overview = mMovie.getOverview();
        mTextViewOverview.setText(overview);

        String rating = mMovie.getRating();
        String rating_text = String.format(getString(R.string.rating),rating);
        mTextViewMovieRating.setText(rating_text);

        if (mMovie.getFavorite() == 0){
            mFavoriteButton.setChecked(false);
        }
        else mFavoriteButton.setChecked(true);

       if (mMovie.getDetailMovie() != null) {
           setDetailUI();
        }
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
        if(mMovie.getDetailMovie().getCasting().size() > mNumberMaxDisplayedActors){
            maxActors = mNumberMaxDisplayedActors;
        }
        else maxActors = mMovie.getDetailMovie().getCasting().size();

        for (int i=0 ; i < maxActors  ; i++){
            mTextViewCasting.append(mMovie.getDetailMovie().getCasting().get(i).getName() + "  (" + mMovie.getDetailMovie().getCasting().get(i).getCharacter() + ")\n");
        }

        //Set genre Object and update UI
        String[] genres = mMovie.getDetailMovie().getGenre();
        for (int i = 0 ; i < genres.length ; i++){
            StringBuilder sb = new StringBuilder();
            mTextViewGenre.append(genres[i]);
            if(i != genres.length-1){
                mTextViewGenre.append(" / ");
            }
        }

        //set review Object and update UI
        for (ReviewMovie reviewMovie : mMovie.getDetailMovie().getReviews()){
            TextView textView = new TextView(getActivity());
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textViewLayoutParams);
            textViewLayoutParams.setMargins(0,0,0,10);
            String review = String.format(getString(R.string.reviews),reviewMovie.getAuthor(), reviewMovie.getReview());
            textView.setText(review);
            //TODO put it back when finishing  API 21
            textView.setBackground(getResources().getDrawable(R.drawable.border_top));

            mLinearLayoutReview.addView(textView);
        }


        //set Trailer to movie Object and update UI
        for(final TrailerMovie trailerMovie : mMovie.getDetailMovie().getTrailers()){
            Button button  = new Button(getActivity());
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(buttonLayoutParams);
            buttonLayoutParams.setMargins(0,0,0,10);
            button.setText(trailerMovie.getNameTrailer());
            //button.setCompoundDrawablePadding(5);
            //button.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24dp), null, null, null);
           // button.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            //TODO put it back when finishing API 21
            button.setBackground(getResources().getDrawable(R.drawable.button_selector));
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

        String runtime = String.format(getString(R.string.runtime),mMovie.getDetailMovie().getRuntime());
        mTextViewRuntime.setText(runtime);

    }




}
