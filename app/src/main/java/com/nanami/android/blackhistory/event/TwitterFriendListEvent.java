package com.nanami.android.blackhistory.event;

import twitter4j.Status;

/**
 * Created by atsumi on 2016/01/11.
 */
public class TwitterFriendListEvent extends TwitterAbstractEvent {

    private final long[] friendIds;
    public TwitterFriendListEvent(long userId, long[] friendIds) {
        super(userId);
        this.friendIds =  friendIds;
    }

    public long[] getFriendIds() {
        return friendIds;
    }
}
