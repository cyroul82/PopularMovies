package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.griffin.popularmovies.movie_list.FavoriteMovieFragment;
import com.griffin.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by griffin on 17/07/16.
 */
public class FavoriteMoviesAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 1;
    private static final int VIEW_TYPE = 0;

    static class ViewHolder {
        ImageView moviePicture;

        public ViewHolder(View view) {
            moviePicture = (ImageView) view.findViewById(R.id.movieItemPictureImageView);

        }
    }

    public FavoriteMoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item_picture, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //get the url Picture from cursor
        String urlPicture = cursor.getString(FavoriteMovieFragment.COLUMN_MOVIE_PICTURE);
        Picasso.with(mContext)
                .load(urlPicture)
                .into(viewHolder.moviePicture);

    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
