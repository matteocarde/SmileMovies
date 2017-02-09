package it.smileapp.smilemovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import it.smileapp.smilemovies.adapters.PosterAdapter;
import it.smileapp.smilemovies.utilities.MoviesDB;
import it.smileapp.smilemovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterClickListener {

    private RecyclerView mPostersRecyclerView;
    private PosterAdapter mPosterAdapter;
    private JSONArray mMoviesList;
    private LinearLayout mErrorMessage;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPostersRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);

        mErrorMessage = (LinearLayout) findViewById(R.id.main_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.main_progressbar);

        Context context = MainActivity.this;

        GridLayoutManager layoutManager = new GridLayoutManager(context, calculateNoOfColumns(getBaseContext()));

        mPostersRecyclerView.setLayoutManager(layoutManager);
        mPostersRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(MainActivity.this, null, MainActivity.this);
        mPostersRecyclerView.setAdapter(mPosterAdapter);

        getMostPopularFilms();

    }

    //Suggested in Code Review. Stack Overflow: http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    private void showGrid() {
        mPostersRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showError() {
        mPostersRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showLoader() {
        mPostersRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void getMostPopularFilms() {
        URL mostPopularFilmsUrl = null;
        try {
            mostPopularFilmsUrl = MoviesDB.getMostPopularFilmsURL();

            new FetchMovies().execute(mostPopularFilmsUrl);
        } catch (MalformedURLException e) {
            showError();
            e.printStackTrace();
        }
    }

    private void getMostRatedFilms() {
        URL mostRatedFilmsUrl = null;
        try {
            mostRatedFilmsUrl = MoviesDB.getMostRatedFilmsURL();

            new FetchMovies().execute(mostRatedFilmsUrl);
        } catch (MalformedURLException e) {
            showError();
            e.printStackTrace();
        }
    }

    private void getMostRatedItalianFilms() {
        URL mostRatedFilmsUrl = null;
        try {
            mostRatedFilmsUrl = MoviesDB.getMostRatedItalianFilmsURL();

            new FetchMovies().execute(mostRatedFilmsUrl);
        } catch (MalformedURLException e) {
            showError();
            e.printStackTrace();
        }
    }

    private void getMostPopularItalianFilms() {
        URL mostRatedFilmsUrl = null;
        try {
            mostRatedFilmsUrl = MoviesDB.getMostPopularItalianFilmsURL();

            new FetchMovies().execute(mostRatedFilmsUrl);
        } catch (MalformedURLException e) {
            showError();
            e.printStackTrace();
        }
    }

    private void getMatteosFavouritesFilms() {
        URL mostRatedFilmsUrl = null;
        try {
            mostRatedFilmsUrl = MoviesDB.getMatteosFavouritesFilmsURL();

            new FetchMovies().execute(mostRatedFilmsUrl);
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

            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra("MOVIE", stringifiedMovie);

            startActivity(intent);

        } catch (JSONException e) {
            showError();
            e.printStackTrace();
        }
    }

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
                    throw new Exception("No internet connection");
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
            } else {
                showError();
            }
        }
    }

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
                getMostPopularFilms();
                return true;
            case R.id.menu_item_sort_rated:
                getMostRatedFilms();
                return true;
            case R.id.menu_item_sort_popular_italian:
                getMostPopularItalianFilms();
                return true;
            case R.id.menu_item_sort_rated_italian:
                getMostRatedItalianFilms();
                return true;
            case R.id.menu_item_matteos_favourite:
                getMatteosFavouritesFilms();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
