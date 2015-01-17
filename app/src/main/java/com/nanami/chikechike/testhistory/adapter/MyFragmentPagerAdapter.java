package com.nanami.chikechike.testhistory.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nanami.chikechike.testhistory.TwitterUtils;
import com.nanami.chikechike.testhistory.fragment.CommonStreamFragment;
import com.nanami.chikechike.testhistory.fragment.HomeStreamFragment;
import com.nanami.chikechike.testhistory.fragment.ReplyStreamFragment;
import com.nanami.chikechike.testhistory.fragment.SimpleStreamTabFragment;

import java.util.ArrayList;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

/**
 * Created by nanami on 2014/09/05.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<CommonStreamFragment> tab = new ArrayList<CommonStreamFragment>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        if( 1 > tab.size()) initializeTab();
    }

    public void initializeTab(){
        tab.add(HomeStreamFragment.newInstance("Home"));
        //tab.add(new ReplyStreamFragment());
    }

    public void addTab(String title){
        tab.add(SimpleStreamTabFragment.newInstance(title));
        this.notifyDataSetChanged();
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
