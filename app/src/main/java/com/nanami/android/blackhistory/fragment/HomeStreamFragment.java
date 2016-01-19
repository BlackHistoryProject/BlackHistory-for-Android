package com.nanami.android.blackhistory.fragment;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.nanami.android.blackhistory.event.EventBusHolder;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.activity.MainStreamActivity;
import com.nanami.android.blackhistory.adapter.TweetAdapter;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.fragment.listener.ListStreamListener;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.event.TwitterStreamEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by nanami on 2014/09/05.
 */
public class HomeStreamFragment extends CommonStreamFragment {

    public static HomeStreamFragment newInstance(Long userId){
        HomeStreamFragment fragment = new HomeStreamFragment();
        fragment.setArguments(userId, TimelineListType.Home, new StreamListener());
        return fragment;
    }

    static class StreamListener implements ListStreamListener {
        @Override
        public void response(ListView listView, TweetAdapter adapter, @NonNull List<Status> result) {
            adapter.clear();
            adapter.addAll(result);
            listView.setSelection(0);
        }

        @Nullable
        @Override
        public List<Status> call(Twitter twitter) throws TwitterException {
            return twitter.getHomeTimeline();
        }
    }

    @Subscribe
    public void OnTwitterStreamEvent(final TwitterStreamEvent event) {
        if (getUserId() != null && event.getUserId() != getUserId()) return;
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshView(event.getStatus());
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
