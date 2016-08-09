package com.griffin.popularmovies.movie_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.griffin.popularmovies.MainActivity;
import com.griffin.popularmovies.R;

/**
 * Created by griffin on 29/07/16.
 */
public class BlankFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_blank_fragment, container, false);

        TextView textView_blank_title = (TextView) rootView.findViewById(R.id.textView_blank_title);

        Bundle b = getArguments();
        if(b != null){
            textView_blank_title.setText(b.getString(MainActivity.TITLE_BLANK_FRAGMENT_KEY));
        }

        return rootView;
    }
}
