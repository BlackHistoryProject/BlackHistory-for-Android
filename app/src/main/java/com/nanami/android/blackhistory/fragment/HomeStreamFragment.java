package com.nanami.android.blackhistory.fragment;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nanami.android.blackhistory.event.EventBusHolder;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.activity.MainStreamActivity;
import com.nanami.android.blackhistory.adapter.TweetAdapter;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;
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

    public static HomeStreamFragment newInstance(Long userId, TimelineListType listType){
        HomeStreamFragment fragment = new HomeStreamFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(fragment.ARGS_USER_ID, userId);
        bundle.putInt(fragment.ARGS_LIST_TYPE, listType.getIndex());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void reloadTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return getTwitter().getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    getAdapter().clear();
                    getAdapter().addAll(result);
                    getListView().setSelection(0);
                } else {
                    BHLogger.toast("タイムラインの取得に失敗しました。");
                }
            }
        };
        task.execute();
    }


    @Subscribe
    public void OnTwitterStreamEvent(TwitterStreamEvent event){
        BHLogger.println("ついーとがあったぞい " + event.getUserId());
        if(event.getUserId() != getUserId()) return;
        final Status status = event.getStatus();
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshView(status);
                        }
                    });
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // EventBus の登録
        EventBusHolder.EVENT_BUS.register(this);
    }

    @Override
    public void onPause() {
        // 登録の解除
        EventBusHolder.EVENT_BUS.unregister(this);
        super.onPause();
    }
}
