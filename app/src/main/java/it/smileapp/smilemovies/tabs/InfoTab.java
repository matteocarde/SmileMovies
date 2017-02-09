package it.smileapp.smilemovies.tabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;

import it.smileapp.smilemovies.MovieActivity;
import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.utilities.MoviesDB;

public class InfoTab extends Fragment {

    private TextView mDescription;

    @Override
    //Im going a bit crazy with Fragments and Tabs, even if it'll be in the next lessons but I just think they're neat oo
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MovieActivity parent = (MovieActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_info_tab, container, false);

        mDescription = (TextView) view.findViewById(R.id.info_movie_description);

        JSONObject mMovie = parent.mMovie;
        try {
            String movieId = mMovie.getString("overview");
            mDescription.setText(movieId);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
