package it.smileapp.smilemovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import it.smileapp.smilemovies.adapters.PosterAdapter;
import it.smileapp.smilemovies.db.MoviesContract;
import it.smileapp.smilemovies.db.MoviesContract.MoviesEntry;
import it.smileapp.smilemovies.jobs.JobsProvider;
import it.smileapp.smilemovies.utilities.MoviesDBRequests;
import it.smileapp.smilemovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements PosterAdapter.PosterClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mPostersRecyclerView;
    private PosterAdapter mPosterAdapter;
    private JSONArray mMoviesList;
    private LinearLayout mErrorMessage;
    private LinearLayout mNoFavouriteMessage;
    private ProgressBar mProgressBar;

    private TextView mFilterName;

    private String currentFilter;

    private static final int LOADER_ID = 48;
    private String TAG;
    private GridLayoutManager mLayoutManager;
    private int mSavedListPostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TAG = getApplicationInfo().className;

        mPostersRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);

        mErrorMessage = (LinearLayout) findViewById(R.id.main_error_message);
        mNoFavouriteMessage = (LinearLayout) findViewById(R.id.main_empty_favourite_message);
        mProgressBar = (ProgressBar) findViewById(R.id.main_progressbar);
        mFilterName = (TextView) findViewById(R.id.tv_filter_name);

        Context context = MainActivity.this;

        mLayoutManager = new GridLayoutManager(context, calculateNoOfColumns(getBaseContext()));

        mPostersRecyclerView.setLayoutManager(mLayoutManager);
        mPostersRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(MainActivity.this, null, MainActivity.this);
        mPostersRecyclerView.setAdapter(mPosterAdapter);


        if (savedInstanceState == null) {
            //Do it only if it's the first onCreate called when the app launches
            displayFilter(null);

            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

            JobsProvider.initializeNotificationJob(this);
        }
    }

    /**
     * Starts the films' list loading based on the passed filter.
     *
     * @param filter - A string rappresenting the key of the filter (ex: getString(R.string.filter_most_rated_key)
     */
    private void displayFilter(String filter) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String defaultFilter = filter != null ?
                filter :
                sharedPreferences.getString(getString(R.string.preferences_movie_filter_key), getString(R.string.preferences_movie_filter_default));

        getMoviesByFilter(defaultFilter);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.preferences_movie_filter_key))) {
            displayFilter(null);
        }
    }

    /**
     * Calculates the number of columns that can be seen based on the device width.
     *
     * @param context
     * @return Number of columns that can be nicely seen in the current device width
     * @see "http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns"
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    /**
     * Displays the movies grids and hides everything else
     */
    private void showGrid() {
        mNoFavouriteMessage.setVisibility(View.INVISIBLE);
        mPostersRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Displays the error message and hides everything else
     */
    private void showError() {
        mNoFavouriteMessage.setVisibility(View.INVISIBLE);
        mPostersRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Displays the "no movies" error and hides everything else
     */
    private void showEmptyFavourite() {
        mNoFavouriteMessage.setVisibility(View.VISIBLE);
        mPostersRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Displays the loader indicator and hides everything else
     */
    private void showLoader() {
        mNoFavouriteMessage.setVisibility(View.INVISIBLE);
        mPostersRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    /**
     * Starts the AsyncTask to load the filtered movies from the Internet
     *
     * @param filter        - A string rappresenting the key of the filter (ex: getString(R.string.filter_most_rated_key)
     * @param fetchFilmsUrl - The Url (taken from TMD) from which the list has to be taken
     */
    private void getFilteredFilms(String filter, URL fetchFilmsUrl) {
        mFilterName.setText(filter);
        new FetchMovies().execute(fetchFilmsUrl);
    }

    private void getMoviesByFilter(String filter) {
        try {

            currentFilter = filter;
            if (filter.equals(getString(R.string.filter_most_popular_key))) {

                URL filterUrl = MoviesDBRequests.getMostPopularFilmsURL();
                String filterTitle = getString(R.string.filter_most_popular_text);
                getFilteredFilms(filterTitle, filterUrl);

            } else if (filter.equals(getString(R.string.filter_most_rated_key))) {

                URL filterUrl = MoviesDBRequests.getMostRatedFilmsURL();
                String filterTitle = getString(R.string.filter_most_rated_text);
                getFilteredFilms(filterTitle, filterUrl);

            } else if (filter.equals(getString(R.string.filter_italian_most_popular_key))) {

                URL filterUrl = MoviesDBRequests.getMostPopularItalianFilmsURL();
                String filterTitle = getString(R.string.filter_italian_most_popular_text);
                getFilteredFilms(filterTitle, filterUrl);

            } else if (filter.equals(getString(R.string.filter_italian_most_rated_key))) {

                URL filterUrl = MoviesDBRequests.getMostRatedItalianFilmsURL();
                String filterTitle = getString(R.string.filter_italian_most_rated_text);
                getFilteredFilms(filterTitle, filterUrl);

            } else if (filter.equals(getString(R.string.filter_my_favourites_key))) {

                mFilterName.setText(getString(R.string.filter_my_favourites_text));
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);

            }
        } catch (MalformedURLException e) {
            showError();
            e.printStackTrace();
        }
    }

    @Override
    public void onPosterClick(int position) {
        try {
            JSONObject movie = mMoviesList.getJSONObject(position);
            String stringifiedMovie = movie.toString();

            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("MOVIE", stringifiedMovie);

            startActivity(intent);

        } catch (JSONException e) {
            showError();
            e.printStackTrace();
        }
    }

    //************LOADER FOR FAVOURITE QUERY CALL************

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            @Override
            protected void onStartLoading() {

                //Reload from database only if the current filter selected is 'my favourites'
                if (currentFilter.equals(getString(R.string.filter_my_favourites_key))) {
                    showLoader();
                    if (mPosterAdapter != null) {
                        mPosterAdapter.reloadMoviesList(null);
                    }
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, MoviesContract.MoviesEntry._ID + " DESC");
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {

            try {

                JSONArray newMoviesList = new JSONArray();
                int cursorLength = data.getCount();

                if (cursorLength > 0) {
                    for (int i = 0; i < cursorLength; i++) {
                        JSONObject obj = new JSONObject();
                        data.moveToPosition(i);
                        obj.put("id", data.getInt(data.getColumnIndex(MoviesEntry.COLUMN_MOVIE_ORIGINAL_ID)));
                        obj.put("title", data.getString(data.getColumnIndex(MoviesEntry.COLUMN_MOVIE_TITLE)));
                        obj.put("poster_path", data.getString(data.getColumnIndex(MoviesEntry.COLUMN_MOVIE_POSTER)));
                        obj.put("vote_average", data.getDouble(data.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RATING)));
                        obj.put("isTakenFromDB", true);

                        newMoviesList.put(obj);
                    }

                    mMoviesList = newMoviesList;
                    mPosterAdapter.reloadMoviesList(mMoviesList);
                    mLayoutManager.scrollToPosition(mSavedListPostion);
                    mSavedListPostion = 0;
                    showGrid();
                } else {
                    showEmptyFavourite();
                }

            } catch (JSONException e) {
                showError();
            }
        } else {
            showError();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    //************LOAD MOVIES FROM THE INTERNET************

    private class FetchMovies extends AsyncTask<URL, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            showLoader();
            if (mPosterAdapter != null) {
                mPosterAdapter.reloadMoviesList(null);
            }
        }

        @Override
        protected JSONArray doInBackground(URL... params) {

            URL url = params[0];
            try {

                if (!NetworkUtils.isOnline(getApplicationContext())) {
                    return null;
                }


                String unformattedResponse = NetworkUtils.getResponseFromHttpUrl(url);
                if (unformattedResponse != null) {
                    JSONObject response = new JSONObject(unformattedResponse);
                    JSONArray films = response.getJSONArray("results");
                    return films;
                }
                throw new Exception("Error in the response");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null) {
                showGrid();
                mMoviesList = jsonArray;
                mPosterAdapter.reloadMoviesList(mMoviesList);
                mLayoutManager.scrollToPosition(mSavedListPostion);
                mSavedListPostion = 0;
            } else {
                showError();
            }
        }
    }

    //************MENU OPTIONS************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_item_sort_popular:
                getMoviesByFilter(getString(R.string.filter_most_popular_key));
                return true;
            case R.id.menu_item_sort_rated:
                getMoviesByFilter(getString(R.string.filter_most_rated_key));
                return true;
            case R.id.menu_item_sort_popular_italian:
                getMoviesByFilter(getString(R.string.filter_italian_most_popular_key));
                return true;
            case R.id.menu_item_sort_rated_italian:
                getMoviesByFilter(getString(R.string.filter_italian_most_rated_key));
                return true;
            case R.id.menu_item_my_favourites:
                getMoviesByFilter(getString(R.string.filter_my_favourites_key));
                return true;
            case R.id.menu_item_preferences:
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("filter", currentFilter);
        outState.putInt("listPosition", mLayoutManager.findFirstVisibleItemPosition());


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        currentFilter = savedInstanceState.getString("filter");
        displayFilter(currentFilter);

        mSavedListPostion = savedInstanceState.getInt("listPosition");
    }

}
