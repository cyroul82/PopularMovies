package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utility;
import com.griffin.popularmovies.pojo.Part;

import java.text.ParseException;
import java.util.List;

/**
 * Created by griffin on 21/08/16.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private static final String LOG_TAG = CollectionAdapter.class.getSimpleName();
    private List<Part> partList;
    private Context mContext;
    private CallbackCollectionAdapter callbackCollectionAdapter;

    public CollectionAdapter(List<Part> partList, Context context) {
        this.partList = partList;
        mContext = context;
    }

    public void setCallback(CallbackCollectionAdapter callbackCollectionAdapter) {

        this.callbackCollectionAdapter = callbackCollectionAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_adapter, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Part part = partList.get(position);

        ViewHolder.textViewTitle.setText(part.getTitle());
        if (part.getReleaseDate() != null) {
            try {
                String year = Utility.getYear(part.getReleaseDate());
                ViewHolder.textViewYear.setText(year);
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage(), e);

            }

        }
        ViewHolder.customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackCollectionAdapter.onCollectionMovieClicked(part.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return partList.size();
    }


    public interface CallbackCollectionAdapter {

        void onCollectionMovieClicked(int idMovie);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static TextView textViewTitle;
        static TextView textViewYear;
        static Context context;
        static LinearLayout customButton;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textViewTitle = (TextView) itemView.findViewById(R.id.textView_collection_title);
            textViewYear = (TextView) itemView.findViewById(R.id.textView_collection_year);
            customButton = (LinearLayout) itemView.findViewById(R.id.collection_custom_button);
        }
    }
}
