package com.nanami.android.blackhistory.fragment.listener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nanami.android.blackhistory.fragment.listener.BaseStreamListener;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by atsumi on 2016/01/19.
 */
 public interface SimpleStreamListener extends BaseStreamListener {
    void response(@NonNull List<twitter4j.Status> result);
}
