package com.griffin.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailMovieActivityFragment extends Fragment {

    private final String RATING_OUT_OF_TEN = "/10";
    private Movie movie = null;

    public DetailMovieActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Creates a new movies if no previous state
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.key_movie))){
            movie = new Movie();
        }
        //restore the previous state
        else {
            movie = savedInstanceState.getParcelable(getString(R.string.key_movie));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        //Get the back the intent
        Intent intent = getActivity().getIntent();

        //Get back the movie from the Intent
        movie = (Movie)intent.getParcelableExtra(getString(R.string.key_movies_list));

        TextView titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        titleTextView.setText(movie.getTitle());

        ImageView imageViewMoviePicture = (ImageView) rootView.findViewById(R.id.moviePictureImageView);
        Picasso.with(getActivity())
                .load(movie.getUrl())
                .into(imageViewMoviePicture);

        TextView textViewMovieYear = (TextView) rootView.findViewById(R.id.movieYearTextView);
        textViewMovieYear.setText(movie.getMovieDate());

        TextView textViewOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        textViewOriginalTitle.setText(movie.getOriginalTitle());

        TextView textViewOverview = (TextView) rootView.findViewById(R.id.overviewMovieTextView);
        textViewOverview.setText(movie.getOverview());

        TextView textViewMovieRating = (TextView) rootView.findViewById(R.id.movieRatingTextView);
        textViewMovieRating.setText(movie.getMovieRating() + RATING_OUT_OF_TEN);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.key_movie), movie);
        super.onSaveInstanceState(outState);
    }
}
