package it.smileapp.smilemovies.tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import it.smileapp.smilemovies.MovieDetailActivity;
import it.smileapp.smilemovies.R;

public class InfoTab extends Fragment {

    private TextView mDescription;
    private MovieDetailActivity mParent;
    private InfoTabBroadcastReceiver mReceiver;

    @Override
    //Im going a bit crazy with Fragments and Tabs, even if it'll be in the next lessons but I just think they're neat oo
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mParent = (MovieDetailActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_info_tab, container, false);
        mReceiver = new InfoTabBroadcastReceiver();

        mDescription = (TextView) view.findViewById(R.id.info_movie_description);

        JSONObject mMovie = mParent.mMovie;
        if (mMovie != null && !mMovie.optBoolean("isTakenFromDB")) {
            bindValues(mMovie);
        }

        return view;
    }

    public void bindValues(JSONObject data) {
        try {
            String movieOverview = data.getString("overview");
            mDescription.setText(movieOverview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(getString(R.string.broadcast_event_moviedatachanged));
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getContext().unregisterReceiver(mReceiver);
    }

    public class InfoTabBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            JSONObject mMovie = mParent.mMovie;
            bindValues(mMovie);
        }
    }

}
