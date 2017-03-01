package com.roomfinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import com.roomfinder.model.SearchResultItem;
import com.roomfinder.ui.fragments.HaveFlatSearchResultsFragment;
import com.roomfinder.ui.fragments.NeedFlatSearchResultsFragment;

/**
 * Created by Aditya on 3/6/2016.
 */
public class SearchResultsSectionsPagerAdapter extends FragmentPagerAdapter {

    ArrayList<SearchResultItem> haveFlatsList, needFlatsList;

    public SearchResultsSectionsPagerAdapter(FragmentManager fm, ArrayList<SearchResultItem> haveFlatsList, ArrayList<SearchResultItem> needFLatsList) {
        super(fm);
        this.haveFlatsList = haveFlatsList;
        this.needFlatsList = needFLatsList;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0)
            return HaveFlatSearchResultsFragment.newInstance(haveFlatsList);
        else
            return NeedFlatSearchResultsFragment.newInstance(needFlatsList);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Have Flat";
            case 1:
                return "Need Flat";
        }
        return null;
    }
}
