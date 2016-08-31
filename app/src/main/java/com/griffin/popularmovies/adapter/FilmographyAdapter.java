package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.CastFilmographyDetail;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by griffin on 30/08/16.
 */
public class FilmographyAdapter  extends RecyclerView.Adapter<FilmographyAdapter.ViewHolder> {

    private List<CastFilmographyDetail> castFilmographyDetailList;
    private Context mContext;

    public FilmographyAdapter(List<CastFilmographyDetail> castFilmographyDetailList, Context context){
        this.castFilmographyDetailList = castFilmographyDetailList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filmography, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CastFilmographyDetail castFilmographyDetail = castFilmographyDetailList.get(position);
        Picasso.with(mContext)
                .load(mContext.getString(R.string.IMAGE_BASE_URL) + castFilmographyDetail.getPosterPath())
                .fit()
                .centerInside()
                .into(ViewHolder.poster);

        ViewHolder.title.setText(castFilmographyDetail.getTitle());
        if(castFilmographyDetail.getReleaseDate() != null) {
            String year = Utilities.getYear(castFilmographyDetail.getReleaseDate());

            ViewHolder.year.setText(year);
        }

    }

    @Override
    public int getItemCount() {
        return castFilmographyDetailList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static ImageView poster;
        static TextView title;
        static TextView year;

        public ViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.filmography_imageView);
            title = (TextView) itemView.findViewById(R.id.filmography_title);
            year = (TextView) itemView.findViewById(R.id.filmography_year);

        }
    }
}
