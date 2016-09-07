package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.R;
import com.griffin.popularmovies.detail_movie.CastActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by griffin on 20/08/16.
 */
public class CastingAdapter extends RecyclerView.Adapter<CastingAdapter.ViewHolder> {

    public static final int MAX_CASTING_TO_DISPLAY = 5;
    private List<Cast> castList;
    private Context mContext;


    public CastingAdapter(List<Cast> castList, Context context) {
        this.castList = castList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.casting_movie_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Cast cast = castList.get(position);
        ViewHolder.textViewCasting.setText(cast.getName());
        StringBuilder casting = new StringBuilder();
        casting.append("  (").append(cast.getCharacter()).append(")");
        ViewHolder.textViewCharacter.setText(casting);
        if (cast.getPerson() != null) {
            Picasso.with(mContext)
                    .load(mContext.getString(R.string.IMAGE_BASE_URL) + cast.getPerson().getProfilePath())
                    .placeholder(R.drawable.ic_wallpaper_black_48dp)
                    .error(R.drawable.ic_wallpaper_black_48dp)
                    .fit()
                    .centerInside()
                    .into(ViewHolder.circleImageViewProfile);
        }

        ViewHolder.customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CastActivity.class);
                intent.putExtra(CastActivity.CAST_KEY, cast);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (castList.size() < MAX_CASTING_TO_DISPLAY) {
            return castList.size();
        } else {
            return MAX_CASTING_TO_DISPLAY;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static TextView textViewCasting;
        static TextView textViewCharacter;
        static CircleImageView circleImageViewProfile;
        static LinearLayout customButton;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewCasting = (TextView) itemView.findViewById(R.id.textView_casting_adapter);
            textViewCharacter = (TextView) itemView.findViewById(R.id.textView_character_adapter);
            circleImageViewProfile = (CircleImageView) itemView.findViewById(R.id.casting_profile_image);
            customButton = (LinearLayout) itemView.findViewById(R.id.casting_custom_button);
        }
    }
}
