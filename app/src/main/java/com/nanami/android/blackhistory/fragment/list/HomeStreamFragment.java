package com.nanami.android.blackhistory.fragment.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.nanami.android.blackhistory.adapter.TweetAdapter;
import com.nanami.android.blackhistory.event.TwitterStreamEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by nanami on 2014/09/05.
 */
final public class HomeStreamFragment extends CommonStreamFragment{

    @Nullable
    public static HomeStreamFragment newInstance(@NonNull Long userId){
        if (2 > userId) return null;
        HomeStreamFragment fragment = new HomeStreamFragment();
        fragment.setArguments(userId, TimelineListType.Home);
        return fragment;
    }

    @Override
    public void response(ListView listView, TweetAdapter adapter, @NonNull List<Status> result) {
        insertAllTweet(result);
    }

    @Nullable
    @Override
    public List<Status> call(Twitter twitter) throws TwitterException {
        return twitter.getHomeTimeline();
    }

    @Subscribe
    public void OnTwitterStreamEvent(final TwitterStreamEvent event) {
        // このリストのオーナー宛の情報かどうかのチェック
        if (event.getUserId() != getUserId()) return;

        insertTweet(event.getStatus());
    }
}
