package jp.promin.android.blackhistory.event;

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
