package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.griffin.popularmovies.movie_list.FavoriteListFragment;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by griffin on 17/07/16.
 */
public class FavoriteMoviesAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 1;
    private static final int VIEW_TYPE = 0;

    private static final String LOG_TAG = FavoriteMoviesAdapter.class.getSimpleName();

    static class ViewHolder {
        @BindView(R.id.movieItemPictureImageView) ImageView moviePicture;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

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
        String urlPicture = cursor.getString(FavoriteListFragment.COLUMN_MOVIE_PICTURE);

        try {
            Bitmap bitmap = Utilities.getPoster(urlPicture, cursor.getInt(FavoriteListFragment.COLUMN_MOVIE_ID));
            viewHolder.moviePicture.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e) {
            Log.d(LOG_TAG, e.getMessage());
        }


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
