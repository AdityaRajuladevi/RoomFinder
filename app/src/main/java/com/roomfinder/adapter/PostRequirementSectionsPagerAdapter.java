package com.roomfinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.roomfinder.ui.fragments.PostRequirementHaveFlatFragment;
import com.roomfinder.ui.fragments.PostRequirementNeedFlatFragment;
import com.roomfinder.utils.Logger;

/**
 * Created by admin on 2/15/16.
 */
public class PostRequirementSectionsPagerAdapter extends FragmentPagerAdapter {

    public PostRequirementSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0)
            return PostRequirementHaveFlatFragment.newInstance();
        else
            return PostRequirementNeedFlatFragment.newInstance();
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

    public void checkToRemoveRequest(int position) {
        Logger.v(" Checking Which fragment  = " + getPageTitle(position) + " is " + getItem(position).isVisible());
    }
}
