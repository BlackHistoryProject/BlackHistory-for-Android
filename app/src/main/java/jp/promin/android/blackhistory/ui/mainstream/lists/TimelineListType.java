package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

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

    private final int mKind;

    TimelineListType(int kind) {
        mKind = kind;
    }

    @NonNull
    static public TimelineListType kindOf(int kind) {
        for (TimelineListType type : TimelineListType.values()) {
            if (type.mKind == kind) return type;
        }
        throw new RuntimeException("おかしいリストタイプ");
    }

    @Nullable
    static public TimelineListType getType(String name) {
        for (TimelineListType type : TimelineListType.values()) {
            if (type.name().equals(name)) return type;
        }
        return null;
    }

    static public String[] getValues() {
        List<String> list = Observable
                .fromArray(TimelineListType.values())
                .map(new Function<TimelineListType, String>() {
                    @Override
                    public String apply(@NonNull TimelineListType timelineListType) throws Exception {
                        return timelineListType.name();
                    }
                })
                .toList()
                .blockingGet();
        String[] ret = new String[list.size()];
        ret = list.toArray(ret);
        return ret;
    }

    public int getKind() {
        return mKind;
    }
}