package com.griffin.popularmovies.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.griffin.popularmovies.detail_movie.ActorMovie;
import com.griffin.popularmovies.R;

import java.util.List;

/**
 * Created by griffin on 23/07/16.
 */
public class ActorAdapter extends ArrayAdapter<ActorMovie> {

    private Context mContext;
    private List<ActorMovie> mActorList;

    public ActorAdapter(Context context, List<ActorMovie> actorMovieList) {
        super(context, 0, 0, actorMovieList);
        mContext = context;
        mActorList = actorMovieList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view= View.inflate(mContext, R.layout.actor_detail, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView_actor_name.setText(mActorList.get(position).getName());
        viewHolder.textView_actor_name.setText(mActorList.get(position).getCharacter());

        return view;

    }

    static class ViewHolder {
        TextView textView_actor_name;
        TextView textView_character;
        public ViewHolder(View v) {
            textView_actor_name = (TextView) v.findViewById(R.id.textView_actor_name);
            textView_character = (TextView) v.findViewById(R.id.textView_actor_character);
        }
    }
}
