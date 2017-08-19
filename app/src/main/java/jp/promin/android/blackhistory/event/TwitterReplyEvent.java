package jp.promin.android.blackhistory.event;

import twitter4j.Status;

final public class TwitterReplyEvent extends TwitterAbstractEvent {
    final private Status status;
    public TwitterReplyEvent(long userId, Status tweet) {
        super(userId);
        this.status = tweet;
    }

    public Status getStatus() {
        return status;
    }
}
