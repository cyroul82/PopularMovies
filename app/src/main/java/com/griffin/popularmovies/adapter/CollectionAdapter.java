package com.griffin.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.Part;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;

import java.util.List;

/**
 * Created by griffin on 21/08/16.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    List<Part> partList;

    public CollectionAdapter(List<Part> partList) {
        this.partList = partList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_adapter, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Part part = partList.get(position);
        ViewHolder.textViewTitle.setText(part.getTitle());
        if(part.getReleaseDate() != null) {
            String year = Utilities.getYear(part.getReleaseDate());
            ViewHolder.textViewYear.setText(year);
        }
    }


    @Override
    public int getItemCount() {
        return partList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static TextView textViewTitle;
        static TextView textViewYear;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textView_collection_title);
            textViewYear = (TextView) itemView.findViewById(R.id.textView_collection_year);
        }
    }
}
