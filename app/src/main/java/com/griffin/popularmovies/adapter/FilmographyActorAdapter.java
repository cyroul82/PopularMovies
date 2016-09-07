package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.CastFilmographyDetail;
import com.griffin.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by griffin on 01/09/16.
 */
public class FilmographyActorAdapter extends BaseAdapter {
    private List<CastFilmographyDetail> mData = new ArrayList<>(0);
    private Context mContext;

    public FilmographyActorAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<CastFilmographyDetail> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_filmography, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.filmography_title);
            viewHolder.year = (TextView) rowView.findViewById(R.id.filmography_year);
            viewHolder.image = (ImageView) rowView
                    .findViewById(R.id.filmography_imageView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Picasso.with(mContext)
                .load(mContext.getString(R.string.IMAGE_BASE_URL) + mData.get(position).getPosterPath())
                .placeholder(R.drawable.ic_wallpaper_black_48dp)
                .error(R.drawable.ic_wallpaper_black_48dp)
                .fit()
                .centerInside()
                .into(holder.image);

        holder.text.setText(mData.get(position).getTitle());
        holder.year.setText(mData.get(position).getReleaseDate());

        return rowView;
    }


    static class ViewHolder {
        public TextView text;
        public ImageView image;
        public TextView year;
    }
}
