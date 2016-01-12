package com.nanami.android.blackhistory.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nanami.android.blackhistory.fragment.CommonStreamFragment;
import com.nanami.android.blackhistory.fragment.HomeStreamFragment;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;

import java.util.ArrayList;

/**
 * Created by nanami on 2014/09/05.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<CommonStreamFragment> tab = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addTab(TimelineListType timelineListType, Long userId){
        CommonStreamFragment additionalFragment = null;

        switch (timelineListType){
            case Home:
                additionalFragment = HomeStreamFragment.newInstance(userId);
                break;
            case Notification:
                break;
            case Mentions:
                break;
            case Favorites:
                break;
            case Lists:
                break;
            case Search:
                break;
            case Followers:
                break;
            case Messages:
                break;
            case User:
                break;
        }
        if (additionalFragment != null){
            tab.add(additionalFragment);
            this.notifyDataSetChanged();
        }
    }

    public void deleteTab(CommonStreamFragment fragment){
        tab.remove(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return tab.get(position);
    }

    @Override
    public int getCount() {
        return tab.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.tab.get(position).getTitle();
    }
}
