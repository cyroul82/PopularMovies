package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.griffin.popularmovies.movie_list.Movie;
import com.griffin.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by griffin on 08/07/16.
 */
public class PopularMoviesAdapter extends ArrayAdapter<Movie> {



    private Context mContext;
    private List<Movie> mMoviesList;

    public PopularMoviesAdapter(Context context, int layoutResource, int idResource, List<Movie> moviesList) {
        super(context, layoutResource, idResource, moviesList);
        mContext = context;
        mMoviesList = moviesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view= View.inflate(mContext, R.layout.movie_item_picture, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(mContext)
                .load(mMoviesList.get(position).getUrl())
                .into(viewHolder.popularMoviePicture);

        return view;
    }

    @Override
    public int getCount() {
        return mMoviesList.size();
    }

    static class ViewHolder {
        ImageView popularMoviePicture;
        public ViewHolder(View v) {
            popularMoviePicture = (ImageView) v.findViewById(R.id.movieItemPictureImageView);
        }
    }

}
