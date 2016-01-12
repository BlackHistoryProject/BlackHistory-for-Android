package com.nanami.android.blackhistory.fragment.list;

/**
 * Created by atsumi on 2016/01/12.
 */
public class TimelineListTypeObject {
    private final TimelineListType listType;
    private final long userId;
    public TimelineListTypeObject(TimelineListType listType, long userId){
        this.listType = listType;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public TimelineListType getListType() {
        return listType;
    }

    public boolean isEqual(TimelineListTypeObject object){
        return this.userId == object.getUserId() && this.listType == object.getListType();
    }

}
