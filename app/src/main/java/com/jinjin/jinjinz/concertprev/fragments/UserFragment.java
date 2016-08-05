package com.jinjin.jinjinz.concertprev.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.jinjin.jinjinz.concertprev.R;
import com.jinjin.jinjinz.concertprev.adapters.UserPagerAdapter;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    private Toolbar tb;
    private ViewPager vpUser;
    private PagerSlidingTabStrip tsTabs;
    private UserFragmentListener userFragmentListener;
    public static boolean FLAG_MY_SONGS_CLICK = false;
    private static LikedConcertsFragment mLikedConcertsFragment;
    private static LikedSongsFragment mLikedSongsFragment;
    public static boolean isFlagMySongsClick() {
        return FLAG_MY_SONGS_CLICK;
    }

    public static void setFlagMySongsClick(boolean flagMySongsClick) {
        FLAG_MY_SONGS_CLICK = flagMySongsClick;
    }

    public static LikedConcertsFragment getLikedConcertsFragment() {
        return mLikedConcertsFragment;
    }

    public static LikedSongsFragment getLikedSongsFragment() {
        return mLikedSongsFragment;
    }

    public interface UserFragmentListener {
        //void onUnlike(Concert concert);
    }

    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            userFragmentListener = (UserFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement UserFragmentListener");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // user favorites
        mLikedConcertsFragment = LikedConcertsFragment.newInstance();
        mLikedSongsFragment = LikedSongsFragment.newInstance();

        vpUser = (ViewPager) view.findViewById(R.id.vpUser);
        vpUser.setAdapter(new UserPagerAdapter(getChildFragmentManager()));
        if(isFlagMySongsClick()) {
            vpUser.setCurrentItem(1, true);
        } else {
            vpUser.setCurrentItem(0, true);
        }

        tsTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tsTabs);
        tsTabs.setViewPager(vpUser);
        tsTabs.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        tsTabs.setIndicatorHeight(10);

        //toolbar
        setHasOptionsMenu(true);
        tb = (Toolbar) view.findViewById(R.id.userBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        return view;
    }


}