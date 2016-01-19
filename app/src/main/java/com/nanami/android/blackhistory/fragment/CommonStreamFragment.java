package com.nanami.android.blackhistory.fragment;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.activity.MainStreamActivity;
import com.nanami.android.blackhistory.event.EventBusHolder;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.fragment.listener.BaseStreamListener;
import com.nanami.android.blackhistory.fragment.listener.ListStreamListener;
import com.nanami.android.blackhistory.fragment.listener.SimpleStreamListener;
import com.nanami.android.blackhistory.model.ModelAccessTokenObject;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.adapter.TweetAdapter;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Telneko on 2015/01/17.
 */
abstract public class CommonStreamFragment extends ListFragment {
    final static String ARGS_USER_ID = "args_user_id";
    final static String ARGS_LIST_TYPE = "args_list_type";
    final static String ARGS_LISTENER = "args_listener";

    //////// getter setter //////////
    @Nullable private TimelineListType listType;
    @Nullable private Long userId;
    @Nullable private ModelAccessTokenObject userObject;
    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Nullable
    public Long getUserId() {
        return userId;
    }

    @Nullable
    public TimelineListType getListType() {
        return listType;
    }

    @Nullable
    public ModelAccessTokenObject getUserObject() {
        return userObject;
    }

    public TweetAdapter getAdapter() {
        return mAdapter;
    }

    public Twitter getTwitter() {
        return mTwitter;
    }
    //////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        TimelineListType listType = TimelineListType.getType(getArguments().getInt(ARGS_LIST_TYPE));
        Long userId = getArguments().getLong(ARGS_USER_ID);
        BaseStreamListener listener = (BaseStreamListener) getArguments().getSerializable(ARGS_LISTENER);

        if (listType == null || userId == 0L || listener == null){
            BHLogger.println("タブの生成に失敗しました");

            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setText("タブの生成に失敗しています");
            return textView;
        } else {

            this.setParams(listType, userId);

            LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_common_list, container, false);
            mAdapter = new TweetAdapter(getActivity(), getUserId());
            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(getActivity(), getUserId());
            reloadTimeLine(listener);

            return ll;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemLongClickListener(new mOnItemLongClockListener());
        getListView().setOnItemClickListener(new mOnItemClickListener());
    }
    @Override
    public void onResume() {
        super.onResume();
        EventBusHolder.EVENT_BUS.register(this);
    }

    @Override
    public void onPause() {
        EventBusHolder.EVENT_BUS.unregister(this);
        super.onPause();
    }

    public class mOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?>parent, View view, int position, long id){
            BHLogger.toast(((Status)parent.getItemAtPosition(position)).getUser().getName());
        }
    }

    public class mOnItemLongClockListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) {

            BHLogger.toast("長押し　POS:" + String.valueOf(position) + "id:" + String.valueOf(id));

            return false;
        }
    }

    /**
     * タイムラインの更新
     * 初回呼び出しや、リストをスクロールした時など
     * @param listener
     */
    final public void reloadTimeLine(final BaseStreamListener listener){
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return listener.call(mTwitter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    if (listener instanceof SimpleStreamListener) {
                        ((SimpleStreamListener)listener).response(result);
                    } else if (listener instanceof ListStreamListener){
                        ((ListStreamListener)listener).response(getListView(), mAdapter, result);
                    } else {
                        BHLogger.toast("Unknown Listener");
                    }
                } else {
                    BHLogger.toast("Response is invalid");
                }
            }
        };
        task.execute();
    }

    /**
     * 一つのツイートをリストに追加する時につかうよ
     * @param status
     */
    public void refreshView(final Status status) {
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
            getAdapter().insert(status, 0);
            getAdapter().notifyDataSetChanged();
            getListView().invalidateViews();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Androidの通知用メソッド
     */
    final public void showNotification(int image, String title, String text, int id){
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

    final public void setParams(@NonNull TimelineListType listType,@NonNull Long userId){
        this.listType = listType;
        this.userId = userId;
        this.userObject = TwitterUtils.getAccount(userId);
    }

    /**
     * タブに表示されるカラム名を返すよ
     * @return
     */
    final public String getTitle(){
        String listType = "";
        if(this.listType != null){
            listType = this.listType.name();
        }

        String userObject = "";
        if (this.userObject != null){
            userObject = this.userObject.getUserScreenName();
        }
        return listType + " - " + userObject;
    }

    final public void setArguments(Long userId, TimelineListType listType, BaseStreamListener listener){
        Bundle bundle = new Bundle();
        bundle.putLong(ARGS_USER_ID, userId);
        bundle.putInt(ARGS_LIST_TYPE, listType.getIndex());
        bundle.putSerializable(ARGS_LISTENER, listener);
        this.setArguments(bundle);
    }
}
