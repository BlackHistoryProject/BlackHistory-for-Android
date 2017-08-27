package jp.promin.android.blackhistory.event;

import android.support.annotation.NonNull;

public final class TweetFailureEvent {
    private final Throwable mThrowable;

    public TweetFailureEvent(@NonNull Throwable throwable) {
        mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
