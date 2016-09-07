package com.griffin.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.R;

import java.util.List;

/**
 * Created by griffin on 19/08/16.
 */
public class ReviewMovieAdapter extends RecyclerView.Adapter<ReviewMovieAdapter.ReviewsViewHolder> {

    private List<Reviews> reviewsList;

    public ReviewMovieAdapter(List<Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @Override
    public ReviewMovieAdapter.ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.reviews_movie_adapter, parent, false);

        return new ReviewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewMovieAdapter.ReviewsViewHolder holder, int position) {
        Reviews reviews = reviewsList.get(position);
        ReviewsViewHolder.textViewAuthor.setText(reviews.getAuthor());
        ReviewsViewHolder.textViewReview.setText(reviews.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        static TextView textViewAuthor;
        static TextView textViewReview;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            textViewAuthor = (TextView) itemView.findViewById(R.id.textViewReview_author);
            textViewReview = (TextView) itemView.findViewById(R.id.textViewReview);
        }
    }
}
