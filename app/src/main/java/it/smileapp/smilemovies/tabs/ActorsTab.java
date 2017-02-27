package it.smileapp.smilemovies.tabs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import it.smileapp.smilemovies.MovieDetailActivity;
import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.adapters.ActorAdapter;
import it.smileapp.smilemovies.utilities.MoviesDBRequests;
import it.smileapp.smilemovies.utilities.NetworkUtils;

public class ActorsTab extends Fragment {

    private RecyclerView mRecyclerView;
    private ActorAdapter mAdapter;
    private Context mContext;

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

        View view = inflater.inflate(R.layout.fragment_actors_tab, container, false);
        MovieDetailActivity parent = (MovieDetailActivity) getActivity();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.actors_recycler_view);


        mProgressBar = (ProgressBar) view.findViewById(R.id.tab_actors_progressbar);
        mError = (LinearLayout) view.findViewById(R.id.tab_actors_error_message);
        mNoContentError = (LinearLayout) view.findViewById(R.id.tab_actors_no_content);

        mContext = view.getContext();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ActorAdapter(mContext, null);
        mRecyclerView.setAdapter(mAdapter);

        JSONObject mMovie = parent.mMovie;
        try {
            String movieId = mMovie.getString("id");

            URL url = MoviesDBRequests.getMovieActorsURL(movieId);

            new ActorsTab.FetchActors().execute(url);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
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

    private class FetchActors extends AsyncTask<URL, Void, JSONArray> {

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
                    return new JSONObject(result).getJSONArray("cast");
                }
                throw new Exception("Error getting response");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null) {
                if (jsonArray.length() > 0) {
                    mAdapter.reloadContent(jsonArray);
                    showList();
                } else {
                    showNoContentError();
                }
            } else {
                showError();
            }
        }
    }
}
