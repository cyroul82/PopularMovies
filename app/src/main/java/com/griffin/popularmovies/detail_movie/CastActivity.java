package com.griffin.popularmovies.detail_movie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CastActivity extends AppCompatActivity {

    public static final String CAST_KEY = "cast_key";

    private Cast mCast;

    @BindView(R.id.cast_birthday)
    TextView mBirthday;
    @BindView(R.id.cast_deathday)
    TextView mDeathday;
    @BindView(R.id.cast_biography)
    TextView mBiography;
    @BindView(R.id.cast_home_page)
    TextView mHomePage;
    @BindView(R.id.cast_imageView)
    ImageView mImageView;
    @BindView(R.id.toolbar_cast)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cast);

        ButterKnife.bind(this);
        mCast = getIntent().getParcelableExtra(CAST_KEY);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mCast.getName());
        }

        mBirthday.setText(mCast.getPerson().getBirthday());
        mDeathday.setText(mCast.getPerson().getDeathday());
        mBiography.setText(mCast.getPerson().getBiography());
        mHomePage.setText(mCast.getPerson().getHomepage());
        //load the picture using picasso library
        Picasso.with(this)
                .load(getString(R.string.IMAGE_BASE_URL) + mCast.getPerson().getProfilePath())
                .fit()
                .centerInside()
                .into(mImageView);



    }

}
