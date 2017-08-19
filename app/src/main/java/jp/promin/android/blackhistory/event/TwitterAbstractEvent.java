package jp.promin.android.blackhistory.event;

abstract public class TwitterAbstractEvent {
    private final long userId;

    public TwitterAbstractEvent(final long userId) {
        this.userId = userId;
    }

    final public long getUserId(){
        return this.userId;
    }
}
