package jp.promin.android.blackhistory.event;

import android.support.annotation.NonNull;

public final class FavoriteFailureEvent {
    private final Throwable mThrowable;

    public FavoriteFailureEvent(@NonNull Throwable throwable) {
        mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
