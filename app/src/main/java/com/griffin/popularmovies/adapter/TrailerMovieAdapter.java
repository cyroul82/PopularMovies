package com.griffin.popularmovies.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.R;

import java.util.List;

/**
 * Created by griffin on 19/08/16.
 */
public class TrailerMovieAdapter extends RecyclerView.Adapter<TrailerMovieAdapter.TrailerViewHolder> {

    private List<TrailerDetail> trailerDetailList;

    public interface OnItemClickListener {
        void onItemClick(TrailerDetail trailerDetail);
    }

    public TrailerMovieAdapter(List<TrailerDetail> trailerDetailList) {
        this.trailerDetailList = trailerDetailList;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_movie_adapter, parent , false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        final String YOUTUBE_BASE_URL="https://www.youtube.com/watch?";
        final String PARAM = "v";
        final TrailerDetail trailerDetail = trailerDetailList.get(position);

        TrailerViewHolder.buttonTrailer.setText(trailerDetail.getName());


    }

    @Override
    public int getItemCount() {
        return trailerDetailList.size();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder{

        static Button buttonTrailer;


        public TrailerViewHolder(View itemView) {
            super(itemView);
            buttonTrailer = (Button) itemView.findViewById(R.id.button_Trailer);

        }
    }
}
