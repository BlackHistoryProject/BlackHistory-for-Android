package com.nanami.android.blackhistory.base;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.nanami.android.blackhistory.fragment.list.CommonStreamFragment;

/**
 * Created by atsumi on 2016/01/19.
 */
public interface BaseStreamListener{
    @Nullable List<Status> call(Twitter twitter) throws TwitterException;
}
