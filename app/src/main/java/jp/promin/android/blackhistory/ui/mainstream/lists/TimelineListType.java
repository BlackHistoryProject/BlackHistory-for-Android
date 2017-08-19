package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import rx.Observable;

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

    final public Integer index;

    TimelineListType(Integer index) {
        this.index = index;
    }

    @Nullable
    static public TimelineListType getType(int index) {
        for (TimelineListType type : TimelineListType.values()) {
            if (type.index == index) return type;
        }
        return null;
    }

    @Nullable
    static public TimelineListType getType(String name) {
        for (TimelineListType type : TimelineListType.values()) {
            if (type.name().equals(name)) return type;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    static public String[] getValues() {
        ArrayList<String> _ret = new ArrayList<>();
        _ret.addAll(Observable.from(TimelineListType.values()).map(Enum::name).toList().toBlocking().single());
        String[] ret = new String[_ret.size()];
        ret = _ret.toArray(ret);
        return ret;
    }
}