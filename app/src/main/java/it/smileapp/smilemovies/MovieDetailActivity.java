package it.smileapp.smilemovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import it.smileapp.smilemovies.adapters.MovieTabsAdapter;
import it.smileapp.smilemovies.databinding.ActivityMovieBinding;
import it.smileapp.smilemovies.db.MoviesContract;
import it.smileapp.smilemovies.db.MoviesContract.MoviesEntry;
import it.smileapp.smilemovies.utilities.MoviesDBRequests;
import it.smileapp.smilemovies.utilities.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    public JSONObject mMovie;
    public String TAG;

    ActivityMovieBinding mBinding;
    FragmentTabHost mTabHost;

    private static final int LOADER_GET_MOVIE_FROM_INTERNET_ID = 130;
    private static final int LOADER_GET_MOVIE_FROM_DB_ID = 244;
    private ViewPager mViewPager;

    private boolean isMovieFavourite;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = MovieDetailActivity.this;
        Intent intent = getIntent();
        TAG = context.toString();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie);

        mViewPager = mBinding.viewMovieTabs.movieTabs;
        MovieTabsAdapter tabsAdapter = new MovieTabsAdapter(getSupportFragmentManager(), getApplicationContext());

        mViewPager.setAdapter(tabsAdapter);

        try {

            if (!intent.hasExtra("MOVIE")) {
                throw new Exception();
            }

            String stringifiedMovie = intent.getStringExtra("MOVIE");
            mMovie = new JSONObject(stringifiedMovie);
            int movieId = mMovie.getInt("id");

            Bundle args = new Bundle();
            args.putInt("movieId", movieId);
            if (mMovie.optBoolean("isTakenFromDB") || mMovie.optBoolean("isFromNotification")) {
                isMovieFavourite = true;

                getSupportLoaderManager().initLoader(LOADER_GET_MOVIE_FROM_INTERNET_ID, args, this);
            } else {
                getSupportLoaderManager().initLoader(LOADER_GET_MOVIE_FROM_DB_ID, args, this);
                bindMovieData(mMovie);
            }


        } catch (Exception e) {
            e.printStackTrace();
            showErrorToast();
        }

    }

    /**
     * Binds the movies information to all the Views in the layout
     * @param data - A JSONObject containing all the informations to be bound;
     * @throws JSONException
     */
    private void bindMovieData(JSONObject data) throws JSONException {


        String movieTitle = data.getString("title");
        String movieOriginalTitle = data.getString("original_title");

        Picasso.with(this).load(MoviesDBRequests.getPhotoUrl(data.getString("backdrop_path")))
                .placeholder(R.drawable.poster)
                .error(R.drawable.poster)
                .into(mBinding.viewMovieInfos.movieIvPoster);
        Picasso.with(this).load(MoviesDBRequests.getPhotoUrl(data.getString("poster_path")))
                .placeholder(R.drawable.poster)
                .error(R.drawable.poster)
                .into(mBinding.viewMovieInfos.movieIvPosterFull);

        mBinding.viewMovieInfos.movieTvTitle.setText(movieTitle);

        if (movieOriginalTitle.equals(movieTitle)) {
            mBinding.viewMovieInfos.movieTvOriginalTitle.setVisibility(View.GONE);
        } else {
            mBinding.viewMovieInfos.movieTvOriginalTitle.setText(movieOriginalTitle);
        }

        mBinding.viewMovieInfos.movieTvYear.setText(getYearFromReleaseDate(data.getString("release_date")));

        float rating = (float) data.getDouble("vote_average");
        mBinding.viewMovieInfos.moveRbRating.setRating(rating / 2f);

        mBinding.viewMovieInfos.movieTvRatingText.setText(rating + "/10");


        //When loading the data is completed send a broadcast event to everyone
        //that is listening saying that the movie data changed

        Intent i = new Intent(getString(R.string.broadcast_event_moviedatachanged));
        sendBroadcast(i);

    }


    Toast mToast;

    /**
     * If the movie is already in the favourites, it removes it from the database,
     * otherwise it add it.
     */
    private void toggleFavourite() {
        try {
            if (!isMovieFavourite) {
                ContentValues cv = new ContentValues();
                cv.put(MoviesEntry.COLUMN_MOVIE_ORIGINAL_ID, mMovie.getInt("id"));
                cv.put(MoviesEntry.COLUMN_MOVIE_TITLE, mMovie.getString("title"));
                cv.put(MoviesEntry.COLUMN_MOVIE_RATING, mMovie.getDouble("vote_average"));
                cv.put(MoviesEntry.COLUMN_MOVIE_POSTER, mMovie.getString("poster_path"));

                Uri insertUri = getContentResolver().insert(MoviesEntry.CONTENT_URI, cv);
                if (insertUri != null) {

                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(this, getString(R.string.toast_success_favourite_add), Toast.LENGTH_LONG);
                    mToast.show();

                    isMovieFavourite = true;
                }
            } else {
                Uri uri = MoviesEntry.CONTENT_URI.buildUpon().appendPath("" + mMovie.getInt("id")).build();
                int deletedRows = getContentResolver().delete(uri, null, null);
                if (deletedRows != 0) {

                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(this, getString(R.string.toast_success_favourite_remove), Toast.LENGTH_LONG);
                    mToast.show();

                    isMovieFavourite = false;
                }
            }

            updateStarIcon();
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorToast();
        }
    }

    /**
     * Changes the star icon based on the fact if the movies is a favourite or not
     */
    private void updateStarIcon() {
        if (mMenu != null) {
            MenuItem star = mMenu.getItem(0);
            if (isMovieFavourite) {
                star.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_accent_24dp));
            } else {
                star.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_black_24dp));
            }
        }
    }

    private Toast mErrorToast;

    /**
     * Displays an error toast saying that there is no internet connection
     */
    private void showErrorToast() {
        if (mErrorToast != null) mErrorToast.cancel();

        mErrorToast = Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG);
        mErrorToast.show();
    }

    /**
     * Retrieves the release year from a release date
     * @param releaseDate - A date in the format "MM/DD/YYYY"
     * @return A string containing the year in the format "YYYY"
     */
    private String getYearFromReleaseDate(String releaseDate) {
        return releaseDate.split("-")[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.movie, menu);
        updateStarIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_item_toggle_favourite:
                toggleFavourite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader onCreateLoader(int id, final Bundle args) {

        if (id == LOADER_GET_MOVIE_FROM_DB_ID) {

            return new AsyncTaskLoader(this) {

                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public Object loadInBackground() {

                    int movieId = args.getInt("movieId");
                    Uri uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath("" + movieId).build();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                    return cursor.getCount() > 0;
                }
            };
        } else {

            return new AsyncTaskLoader(this) {

                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public Object loadInBackground() {
                    Log.d(TAG, "loadInBackground is called on MovieDetailActivity");
                    try {

                        int movieId = args.getInt("movieId");

                        URL url = MoviesDBRequests.getMovieById(movieId);

                        if (!NetworkUtils.isOnline(getApplicationContext())) {
                            return null;
                        }


                        String unformattedResponse = NetworkUtils.getResponseFromHttpUrl(url);
                        Log.w(TAG, unformattedResponse);
                        if (unformattedResponse != null) {
                            JSONObject response = new JSONObject(unformattedResponse);
                            return response;
                        }
                        throw new Exception("Error in the response");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorToast();
                        return null;
                    }
                }
            };
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        if (id == LOADER_GET_MOVIE_FROM_DB_ID) {
            isMovieFavourite = (boolean) data;
            updateStarIcon();
        } else {
            try {
                mMovie = (JSONObject) data;
                if (mMovie != null) {
                    bindMovieData((JSONObject) data);
                } else {
                    showErrorToast();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorToast();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
