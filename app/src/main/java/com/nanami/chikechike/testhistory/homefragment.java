package com.nanami.chikechike.testhistory;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.nanami.chikechike.myapplication.R;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamAdapter;
import twitter4j.UserStreamListener;

/**
 * Created by nanami on 2014/09/05.
 */
public class homefragment extends android.support.v4.app.ListFragment {

    android.os.Handler handler = new android.os.Handler();
    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.homefragment, container, false);

        Context context = getActivity();

        if (!TwitterUtils.hasAccessToken(context)) {
            Intent intent = new Intent(context, TwitterOAuthActivity.class);
            startActivity(intent);
        } else {
            mAdapter = new TweetAdapter(context);

            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(context);
            reloadTimeLine();

            TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(context);
            {
                twitterStream.addListener(new mMyUserStreamAdapter());
                twitterStream.user();
            }
        }


        return ll;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    class mMyUserStreamAdapter implements UserStreamListener {
        public void onStatus(final Status status) {
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
        public void onDeletionNotice(final StatusDeletionNotice statusDeletionNotice) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.deleteTweet(statusDeletionNotice.getStatusId());
                }
            });
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {

        }

        @Override
        public void onStallWarning(StallWarning warning) {

        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {

        }

        @Override
        public void onFriendList(long[] friendIds) {

        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {

        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

        }

        @Override
        public void onFollow(User source, User followedUser) {

        }

        @Override
        public void onUnfollow(User source, User unfollowedUser) {

        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {

        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {

        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {

        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {

        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {

        }

        @Override
        public void onBlock(User source, User blockedUser) {

        }

        @Override
        public void onUnblock(User source, User unblockedUser) {

        }

        @Override
        public void onException(Exception ex) {

        }
    }


    private void refreshView(final Status status) {
        try {
            if(status.getInReplyToUserId() > -1){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status.getText().contains("@baki_chike")){
                            String txt = status.getText().replace("@" + status.getInReplyToScreenName(), "");
                            showNotification(R.drawable.ic_launcher2, status.getUser().getName(), txt, 2 );
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
        public boolean onItemLongClick(AdapterView<?> parebt, View view, int position,long id) {

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
        Notification notification = new Notification();
        notification.icon = image;
        Intent intent = new Intent(getActivity(), MainStreamActivity.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        notification.setLatestEventInfo(getActivity(), title ,text ,pendingIntent );

        notification.tickerText = text;
        notification.defaults |= Notification.DEFAULT_VIBRATE;      // バイブレーション機能
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notification.ledARGB = 0x7700FF00;
        notification.ledOnMS = 500;
        notification.ledOffMS = 300;

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);

    }


}
