package com.nanami.android.blackhistory.fragment.listener;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by atsumi on 2016/01/19.
 */
public interface BaseStreamListener extends Serializable {
    @Nullable List<Status> call(Twitter twitter) throws TwitterException;
}
