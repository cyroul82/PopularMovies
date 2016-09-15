package com.griffin.popularmovies.task;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;

/**
 * Created by griffin on 10/09/16.
 */
public class MovieToFavoriteTask extends AsyncQueryHandler{

    public static final int ON_ADD_FAVORITE_TOKEN = 0;
    public static final int IS_MOVIE_FAVORITE_TOKEN = 1;
    public static final int ON_DELETE_FAVORITE_TOKEN = 2;
    private WeakReference<OnQueryCompleteListener> mListener;

    public MovieToFavoriteTask(ContentResolver cr, OnQueryCompleteListener listener) {
        super(cr);
        this.mListener = new WeakReference<>(listener);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if(mListener != null && mListener.get() != null){
            mListener.get().onQueryComplete(cursor, token);
        } else {
            if(cursor != null){
                cursor.close();
            }
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if(mListener != null && mListener.get() != null){
            mListener.get().onDeleteComplete(result);
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if(mListener != null && mListener.get() != null){
            mListener.get().onInsertComplete(uri);
        }
    }

    public interface OnQueryCompleteListener {
        void onQueryComplete(Cursor data, int token);

        void onInsertComplete(Uri uri);

        void onDeleteComplete(int result);
    }


}
