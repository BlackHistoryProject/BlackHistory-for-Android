package jp.promin.android.blackhistory.event;

import android.support.annotation.NonNull;

public final class ReTweetFailureEvent {
    private final Throwable mThrowable;

    public ReTweetFailureEvent(@NonNull Throwable throwable) {
        mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
