package com.nanami.android.blackhistory.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;

import com.nanami.android.blackhistory.fragment.CommonStreamFragment;
import com.nanami.android.blackhistory.fragment.FavoriteStreamFragment;
import com.nanami.android.blackhistory.fragment.HomeStreamFragment;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.utils.BHLogger;

import java.util.ArrayList;

/**
 * Created by nanami on 2014/09/05.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Pair<Long, CommonStreamFragment>> tab = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addTab(TimelineListType timelineListType, Long userId){
        if (search(userId, timelineListType) != null){
            return;
        }

        CommonStreamFragment additionalFragment = null;

        switch (timelineListType){
            case Home:
                additionalFragment = HomeStreamFragment.newInstance(userId, TimelineListType.Home);
                break;
            case Notification:
                break;
            case Mentions:
                break;
            case Favorites:
                additionalFragment = FavoriteStreamFragment.newInstance(userId, TimelineListType.Favorites);
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
            tab.add(new Pair<>(userId, additionalFragment));
            this.notifyDataSetChanged();
        }
    }

    public CommonStreamFragment search(Long userId, TimelineListType listType){
        for (Pair<Long, CommonStreamFragment> item : this.tab){
            if (item.first.equals(userId) && item.second.getListType() != null && item.second.getListType().equals(listType)){
                return item.second;
            }
        }
        return null;
    }

    public void deleteTab(Pair<Long, CommonStreamFragment> item){
        for (Pair<Long, CommonStreamFragment> _item : this.tab){
            if (_item.equals(item)){
                this.tab.remove(item);
                this.notifyDataSetChanged();
                BHLogger.toast("削除しました");
                return;
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return tab.get(position).second;
    }

    public Pair<Long, CommonStreamFragment> getItemAtIndex(int position){
        return tab.get(position);
    }

    @Override
    public int getCount() {
        return tab.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            try {
                return this.tab.get(position).second.getTitle();
            } catch (Exception e) {
                e.printStackTrace();
                return String.valueOf(position);
            }
        } catch (Exception e){
            e.printStackTrace();
            return "Non-Title";
        }
    }
}
