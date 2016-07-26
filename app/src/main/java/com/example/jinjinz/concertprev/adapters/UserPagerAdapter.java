package com.example.jinjinz.concertprev.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jinjinz.concertprev.fragments.LikedConcertsFragment;
import com.example.jinjinz.concertprev.fragments.LikedSongsFragment;
import com.example.jinjinz.concertprev.fragments.UserFragment;

/**
 * Created by noradiegwu on 7/26/16.
 */
public class UserPagerAdapter extends FragmentPagerAdapter {
    private LikedConcertsFragment mLikedConcertsFragment;
    private LikedSongsFragment mLikedSongsFragment;
    private static String tabTitles[] = {"Concerts", "Songs"};

    // Adapter gets the manager in order to insert or remove fragment from activity
    public UserPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return UserFragment.getmLikedConcertsFragment();
        } else if (position == 1) {
            return UserFragment.getmLikedSongsFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // returns tab title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
