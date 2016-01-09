package com.nanami.android.blackhistory.event;

import twitter4j.Status;

/**
 * Created by atsumi on 2016/01/09.
 */
abstract public class TwitterAbstractEvent {
    private final String userName;
    private final Status tweet;

    public TwitterAbstractEvent(final String userName, final Status tweet) {
        this.userName = userName;
        this.tweet = tweet;
    }

    public String getUserName(){
        return this.userName;
    }

    public Status getStatus() {
        return tweet;
    }
}
