package jp.promin.android.blackhistory.event;

import twitter4j.Status;

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
