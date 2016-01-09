package com.nanami.android.blackhistory.event;

import twitter4j.Status;

/**
 * Created by atsumi on 2016/01/09.
 */
final public class TwitterStreamEvent extends TwitterAbstractEvent {
    public TwitterStreamEvent(String userName, Status tweet) {
        super(userName, tweet);
    }
}
