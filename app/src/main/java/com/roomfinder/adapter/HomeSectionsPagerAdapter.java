package com.roomfinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.roomfinder.ui.fragments.ChatFragment;
import com.roomfinder.ui.fragments.FlatMateFragment;

/**
 * Created by Aditya on 2/20/2016.
 */
public class HomeSectionsPagerAdapter extends FragmentPagerAdapter {

    public HomeSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0)
            return FlatMateFragment.newInstance();
        else
            return ChatFragment.newInstance();
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
                return "FlatMates";
            case 1:
                return "Chats";
        }
        return null;
    }
}

