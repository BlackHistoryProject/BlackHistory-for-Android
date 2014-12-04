package com.nanami.chikechike.testhistory;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by nanami on 2014/09/05.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

            switch(position){
                case 0:
                    return new homefragment();
                default:
                    return new ReplyStreamfragment();
            }
      }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "timeline";
    }
}
