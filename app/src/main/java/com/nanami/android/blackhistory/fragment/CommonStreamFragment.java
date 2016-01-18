package com.nanami.android.blackhistory.fragment;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.activity.MainStreamActivity;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.model.ModelAccessTokenObject;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.adapter.TweetAdapter;

import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by Telneko on 2015/01/17.
 */
abstract public class CommonStreamFragment extends ListFragment {
    final String ARGS_USER_ID = "args_user_id";
    final String ARGS_LIST_TYPE = "args_list_type";

    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Nullable private TimelineListType listType;
    @Nullable private Long userId;
    @Nullable private ModelAccessTokenObject userObject;

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

    final public void setParams(@NonNull TimelineListType listType,@NonNull Long userId){
        this.listType = listType;
        this.userId = userId;
        this.userObject = TwitterUtils.getAccount(userId);
    }

    public String getTitle(){
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fragment再生成抑止
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        this.setParams(
                TimelineListType.getType(getArguments().getInt(ARGS_LIST_TYPE)),
                getArguments().getLong(ARGS_USER_ID));

        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.fragment_common_list, container, false);
        mAdapter = new TweetAdapter(getActivity(), getUserId());
        setListAdapter(mAdapter);

        mTwitter = TwitterUtils.getTwitterInstance(getActivity(), getUserId());
        reloadTimeLine();

        return ll;
    }

    abstract public void reloadTimeLine();

    public TweetAdapter getAdapter() {
        return mAdapter;
    }

    public Twitter getTwitter() {
        return mTwitter;
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
}
