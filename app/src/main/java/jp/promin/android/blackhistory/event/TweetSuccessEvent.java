package jp.promin.android.blackhistory.event;

public final class TweetSuccessEvent {
    private final long mTweetId;

    public TweetSuccessEvent(long tweetId) {
        mTweetId = tweetId;
    }

    public long getTweetId() {
        return mTweetId;
    }
}
