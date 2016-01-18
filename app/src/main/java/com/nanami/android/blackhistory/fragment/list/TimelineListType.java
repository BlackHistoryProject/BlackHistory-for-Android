package com.nanami.android.blackhistory.fragment.list;

import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by atsumi on 2016/01/12.
 */
public enum TimelineListType {
    Home(0),
    Notification(1),
    Mentions(2),
    Favorites(3),
    Lists(4),
    Search(5),
    Followers(6),
    Messages(7),
    User(8);

    final private Integer index;
    TimelineListType(Integer index){
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    @Nullable
    static public TimelineListType getType(int index){
        if (index == 0) return Home;
        if (index == 1) return Notification;
        if (index == 2) return Mentions;
        if (index == 3) return Favorites;
        if (index == 4) return Lists;
        if (index == 5) return Search;
        if (index == 6) return Followers;
        if (index == 7) return Messages;
        if (index == 8) return User;
        return null;
    }

    static public String[] getValues(){
        ArrayList<String> _ret = new ArrayList<>();
        for (TimelineListType type : TimelineListType.values()){
            _ret.add(type.name());
        }
        String[] ret = new String[_ret.size()];
        ret = _ret.toArray(ret);
        return ret;
    }
}