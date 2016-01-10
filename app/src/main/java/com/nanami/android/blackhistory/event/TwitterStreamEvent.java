package com.nanami.android.blackhistory.event;

import twitter4j.Status;

/**
 * Created by atsumi on 2016/01/09.
 */
final public class TwitterStreamEvent extends TwitterAbstractEvent {
    private final Status status;
    public TwitterStreamEvent(long userId, Status tweet) {
        super(userId);
        this.status = tweet;
    }

    public Status getStatus() {
        return status;
    }
}
