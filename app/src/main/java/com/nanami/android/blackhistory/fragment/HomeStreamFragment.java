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

    public android.os.Handler handler = new android.os.Handler();
    public TweetAdapter mAdapter;
    public Twitter mTwitter;

    public static HomeStreamFragment newInstance(Long userId){
        HomeStreamFragment fragment = new HomeStreamFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(fragment.ARGS_USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        this.setParams(TimelineListType.Home, getArguments().getLong(ARGS_USER_ID));

        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.fragment_common_list, container, false);

        Context context = getActivity();

        mAdapter = new TweetAdapter(context, getUserId());
        setListAdapter(mAdapter);

        mTwitter = TwitterUtils.getTwitterInstance(context, getUserId());
        reloadTimeLine();

        return ll;
    }

    private void refreshView(final Status status) {
        try {
            if(status.getInReplyToUserId() > -1){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status.getText().contains("@" + getUserObject().getUserScreenName())){
                            String txt = status.getText().replace("@" + status.getInReplyToScreenName(), "");       // 自分宛に通知が来た時に表示される自分のUserIDを消している
                            showNotification(R.drawable.ic_launcher2, status.getUser().getName(), txt, 2 );         // 通知の表示
                        }
                    }
                });
            }
            mAdapter.insert(status, 0);
            mAdapter.notifyDataSetChanged();
            getListView().invalidateViews();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemLongClickListener(new mOnItemLongClockListener());
        getListView().setOnItemClickListener(new mOnItemClickListener());
    }

    public class mOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?>parent, View view, int position, long id){
            showToast(((Status)parent.getItemAtPosition(position)).getUser().getName());
        }
    }

    public class mOnItemLongClockListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) {

            showToast("長押し　POS:" + String.valueOf(position) + "id:" + String.valueOf(id));

            return false;
        }
    }


    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました。");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    public void showNotification(int image, String title, String text, int id){
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setSmallIcon(image);

        Intent intent = new Intent(getContext(), MainStreamActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setContentTitle(title);
        builder.setContentText(text);

        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        builder.setLights(0x7700FF00, 500, 300);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());
        manager.notify(id, builder.build());

    }

    @Subscribe
    public void OnTwitterStreamEvent(TwitterStreamEvent event){
        BHLogger.println("ついーとがあったぞい " + event.getUserId());
        if(event.getUserId() != getUserId()) return;
        final Status status = event.getStatus();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshView(status);
                        }
                    });
                }
            }).start();
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
