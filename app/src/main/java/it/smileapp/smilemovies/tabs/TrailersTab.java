package it.smileapp.smilemovies.tabs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import it.smileapp.smilemovies.MainActivity;
import it.smileapp.smilemovies.MovieActivity;
import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.adapters.TrailerAdapter;
import it.smileapp.smilemovies.utilities.MoviesDB;
import it.smileapp.smilemovies.utilities.NetworkUtils;

public class TrailersTab extends Fragment implements TrailerAdapter.TrailerClickListener {

    private RecyclerView mRecyclerView;
    private TrailerAdapter mAdapter;
    private Context mContext;
    private JSONArray mTrailers;

    private ProgressBar mProgressBar;
    private LinearLayout mError;
    private LinearLayout mNoContentError;

    @Override
    //Im going a bit crazy with Fragments and Tabs, even if it'll be in the next lessons but I just think they're neat oo
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trailers_tab, container, false);
        MovieActivity parent = (MovieActivity) getActivity();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.tab_trailers_recycler_view);

        mProgressBar = (ProgressBar) view.findViewById(R.id.tab_trailers_progressbar);
        mError = (LinearLayout) view.findViewById(R.id.tab_trailers_error_message);
        mNoContentError = (LinearLayout) view.findViewById(R.id.tab_trailers_no_content);

        mContext = view.getContext();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TrailerAdapter(mContext, null, this);
        mRecyclerView.setAdapter(mAdapter);

        JSONObject mMovie = parent.mMovie;
        try {
            String movieId = mMovie.getString("id");

            URL url = MoviesDB.getMovieTrailersURL(movieId);

            new FetchTrailers().execute(url);


        } catch (Exception e) {
            e.printStackTrace();
            showError();
        }


        return view;
    }

    @Override
    public void onTrailerClick(int position) {
        try {
            JSONObject clickedTrailer = mTrailers.getJSONObject(position);
            String YTKey = clickedTrailer.getString("source");

            Uri YTUri = MoviesDB.getYoutubeUri(YTKey);

            Intent intent = new Intent(Intent.ACTION_VIEW, YTUri);

            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FetchTrailers extends AsyncTask<URL, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected JSONArray doInBackground(URL... params) {

            try {

                if (!NetworkUtils.isOnline(mContext)) {
                    throw new Exception("No internet connection");
                }

                String result = NetworkUtils.getResponseFromHttpUrl(params[0]);
                if (result != null) {
                    return new JSONObject(result).getJSONArray("youtube");
                }
                throw new Exception("Error getting request");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null) {
                if (jsonArray.length() > 0) {
                    mTrailers = jsonArray;
                    mAdapter.reloadContent(mTrailers);
                    showList();
                } else {
                    showNoContentError();
                }
            } else {
                showError();
            }
        }


    }

    private void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.VISIBLE);
        mNoContentError.setVisibility(View.INVISIBLE);
    }

    private void showNoContentError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.INVISIBLE);
        mNoContentError.setVisibility(View.VISIBLE);
    }

    private void showList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.INVISIBLE);
        mNoContentError.setVisibility(View.INVISIBLE);
    }

    private void showProgress() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mError.setVisibility(View.INVISIBLE);
        mNoContentError.setVisibility(View.INVISIBLE);
    }
}
