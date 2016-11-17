package jp.promin.android.blackhistory.event;

/**
 * Created by atsumi on 2016/01/09.
 */
abstract public class TwitterAbstractEvent {
    private final long userId;

    public TwitterAbstractEvent(final long userId) {
        this.userId = userId;
    }

    final public long getUserId(){
        return this.userId;
    }
}
