package jp.promin.android.blackhistory.event;

import android.support.annotation.NonNull;

import twitter4j.Status;

public final class ReTweetSuccessEvent {
    private final Status mStatus;

    public ReTweetSuccessEvent(@NonNull Status status) {
        mStatus = status;
    }

    public Status getStatus() {
        return mStatus;
    }
}
