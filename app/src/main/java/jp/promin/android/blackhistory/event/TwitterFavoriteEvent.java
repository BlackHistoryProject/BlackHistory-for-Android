package jp.promin.android.blackhistory.event;

import twitter4j.Status;
import twitter4j.User;

public class TwitterFavoriteEvent extends TwitterAbstractEvent {
    final private User source;
    final private User target;
    final private Status favoritedStatus;

    public TwitterFavoriteEvent(long userId, User source, User target, Status favoritedStatus) {
        super(userId);
        this.source = source;
        this.target = target;
        this.favoritedStatus = favoritedStatus;
    }


    public User getSource() {
        return source;
    }

    public User getTarget() {
        return target;
    }

    public Status getFavoritedStatus() {
        return favoritedStatus;
    }
}
