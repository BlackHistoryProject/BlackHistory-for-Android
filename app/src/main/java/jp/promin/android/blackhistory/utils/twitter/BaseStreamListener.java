package jp.promin.android.blackhistory.utils.twitter;

import android.support.annotation.Nullable;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public interface BaseStreamListener {
    @Nullable
    List<Status> call(Twitter twitter) throws TwitterException;
}
