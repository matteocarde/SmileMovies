package it.smileapp.smilemovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import it.smileapp.smilemovies.tabs.ActorsTab;
import it.smileapp.smilemovies.tabs.InfoTab;
import it.smileapp.smilemovies.tabs.ReviewsTab;
import it.smileapp.smilemovies.tabs.TrailersTab;
import it.smileapp.smilemovies.utilities.MoviesDB;

public class MovieActivity extends AppCompatActivity {

    private FragmentTabHost mTabHost;

    public JSONObject mMovie;

    private TextView mYear;
    private TextView mRatingText;
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mOriginalTitle;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Context context = MovieActivity.this;

        Intent intent = getIntent();

        mPoster = (ImageView) findViewById(R.id.movie_iv_poster);
        mTitle = (TextView) findViewById(R.id.movie_tv_title);
        mOriginalTitle = (TextView) findViewById(R.id.movie_tv_original_title);
        mRatingText = (TextView) findViewById(R.id.movie_tv_rating_text);
        mRatingBar = (RatingBar) findViewById(R.id.move_rb_rating);
        mYear = (TextView) findViewById(R.id.movie_tv_year);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(context, getSupportFragmentManager(), android.R.id.tabcontent);

        TabHost.TabSpec infoTab = mTabHost.newTabSpec("infos").setIndicator(getString(R.string.movie_infos));
        TabHost.TabSpec trailersTab = mTabHost.newTabSpec("trailers").setIndicator(getString(R.string.movie_trailers));
        TabHost.TabSpec actorsTab = mTabHost.newTabSpec("actors").setIndicator(getString(R.string.movie_actors));
        TabHost.TabSpec reviewsTab = mTabHost.newTabSpec("reviews").setIndicator(getString(R.string.movie_reviews));

        mTabHost.addTab(infoTab, InfoTab.class, null);
        mTabHost.addTab(trailersTab, TrailersTab.class, null);
        mTabHost.addTab(actorsTab, ActorsTab.class, null);
        mTabHost.addTab(reviewsTab, ReviewsTab.class, null);


        try {

            if (!intent.hasExtra("MOVIE")) {
                throw new Exception();
            }

            String stringifiedMovie = intent.getStringExtra("MOVIE");
            mMovie = new JSONObject(stringifiedMovie);

            String movieTitle = mMovie.getString("title");
            String movieOriginalTitle = mMovie.getString("original_title");

            Picasso.with(context).load(MoviesDB.getPhotoUrl(mMovie.getString("poster_path"))).into(mPoster);
            mTitle.setText(movieTitle);

            if (movieOriginalTitle.equals(movieTitle)) {
                mOriginalTitle.setVisibility(View.GONE);
            }

            mOriginalTitle.setText(movieOriginalTitle);
            mYear.setText(getYearFromReleaseDate(mMovie.getString("release_date")));

            float rating = (float) mMovie.getDouble("vote_average");
            mRatingBar.setRating(rating / 2f);

            mRatingText.setText(rating + "/10");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getYearFromReleaseDate(String releaseDate) {
        return releaseDate.split("-")[0];
    }
}
