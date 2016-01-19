package com.nanami.android.blackhistory.fragment.listener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nanami.android.blackhistory.adapter.TweetAdapter;
import com.nanami.android.blackhistory.fragment.listener.BaseStreamListener;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by atsumi on 2016/01/19.
 */
public interface ListStreamListener extends BaseStreamListener {
    void response(ListView listView, TweetAdapter adapter, @NonNull List<twitter4j.Status> result);
}
