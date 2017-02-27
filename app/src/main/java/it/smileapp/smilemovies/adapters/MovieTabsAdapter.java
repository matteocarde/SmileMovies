package it.smileapp.smilemovies.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.tabs.ActorsTab;
import it.smileapp.smilemovies.tabs.InfoTab;
import it.smileapp.smilemovies.tabs.ReviewsTab;
import it.smileapp.smilemovies.tabs.TrailersTab;


public class MovieTabsAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabs = new ArrayList<>();

    public MovieTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        appendTab(new InfoTab(), context.getString(R.string.movie_infos).toUpperCase());
        appendTab(new TrailersTab(), context.getString(R.string.movie_trailers).toUpperCase());
        appendTab(new ActorsTab(), context.getString(R.string.movie_actors).toUpperCase());
        appendTab(new ReviewsTab(), context.getString(R.string.movie_reviews).toUpperCase());
    }

    int fragmentIndex = 0;

    /**
     * Appends a new tab at the end of the Adapter
     * @param fragment - The fragment that will be displayed inside the TabView
     * @param tabTitle - The text that will appear inside the tab
     */
    private void appendTab(Fragment fragment, String tabTitle) {
        fragments.add(fragmentIndex, fragment);
        tabs.add(fragmentIndex, tabTitle);
        fragmentIndex++;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
