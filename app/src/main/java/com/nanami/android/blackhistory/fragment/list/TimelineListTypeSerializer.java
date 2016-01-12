package com.nanami.android.blackhistory.fragment.list;

/**
 * Created by atsumi on 2016/01/12.
 */
public class TimelineListTypeSerializer {
    static TimelineListTypeObject mClass;
    public TimelineListTypeSerializer(TimelineListTypeObject listType){
        mClass = listType;
    }

    public TimelineListTypeObject getListType() {
        return mClass;
    }
}
