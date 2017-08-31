package jp.promin.android.blackhistory.event;

import android.support.annotation.NonNull;

import twitter4j.Status;

public final class FavoriteSuccessEvent {
    private final Status mStatus;

    public FavoriteSuccessEvent(@NonNull Status status) {
        mStatus = status;
    }

    public Status getStatus() {
        return mStatus;
    }
}
