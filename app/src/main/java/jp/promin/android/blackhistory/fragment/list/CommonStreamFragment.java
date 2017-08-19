package jp.promin.android.blackhistory.fragment.list;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.activity.MainStreamActivity;
import jp.promin.android.blackhistory.adapter.TweetAdapter;
import jp.promin.android.blackhistory.base.BaseFragment;
import jp.promin.android.blackhistory.base.BaseStreamListener;
import jp.promin.android.blackhistory.fragment.listener.ListStreamListener;
import jp.promin.android.blackhistory.fragment.listener.SimpleStreamListener;
import jp.promin.android.blackhistory.model.ModelAccessTokenObject;
import jp.promin.android.blackhistory.utils.BHLogger;
import jp.promin.android.blackhistory.utils.RxWrap;
import jp.promin.android.blackhistory.utils.TwitterUtils;
import twitter4j.Status;
import twitter4j.Twitter;

public abstract class CommonStreamFragment extends BaseFragment implements ListStreamListener {
    final static String ARGS_USER_ID = "args_user_id";
    final static String ARGS_LIST_TYPE = "args_list_type";
    @Bind(android.R.id.list)
    ListView listView;
    //////// getter setter //////////
    @NonNull
    private TimelineListType listType;
    private Long userId;
    private ModelAccessTokenObject userObject;
    private TweetAdapter mAdapter;
    private Twitter mTwitter;
    private BaseStreamListener listener;

    @NonNull
    final public Long getUserId() {
        return userId;
    }

    @NonNull
    final public TimelineListType getListType() {
        return listType;
    }

    @NonNull
    final public ModelAccessTokenObject getUserObject() {
        return userObject;
    }

    final public TweetAdapter getAdapter() {
        return mAdapter;
    }

    final public Twitter getTwitter() {
        return mTwitter;
    }

    final public BaseStreamListener getListener() {
        return listener;
    }

    //////////////////////////////////

    final public void setListener(BaseStreamListener listener) {
        this.listener = listener;
    }

    @Override
    final protected int getLayoutID() {
        return R.layout.fragment_common_list;
    }

    @Override
    final protected void init() {
        this.userId = getArguments().getLong(ARGS_USER_ID);
        this.listType = TimelineListType.getType(getArguments().getInt(ARGS_LIST_TYPE, -1));

        BHLogger.printlnDetail("Loading User Object");
        this.userObject = TwitterUtils.getAccount(this.userId);
    }

    @Override
    final public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.mAdapter == null) {
            this.mAdapter = new TweetAdapter(this);
            this.listView.setAdapter(this.mAdapter);
        }
        if (this.mTwitter == null) {
            this.mTwitter = TwitterUtils.getTwitterInstance(getActivity(), userId);
        }

        setListener(this);
        reloadTimeLine();
    }

    /**
     * タイムラインの更新
     * 初回呼び出しや、リストをスクロールした時など
     */
    final protected void reloadTimeLine() {
        RxWrap.create(RxWrap.createObservable(() -> listener.call(mTwitter)))
                .subscribe(statuses -> {
                    if (listener instanceof SimpleStreamListener) {
                        ((SimpleStreamListener) listener).response(statuses);
                    } else if (listener instanceof ListStreamListener) {
                        ((ListStreamListener) listener).response(listView, mAdapter, statuses);
                    } else {
                        BHLogger.toast("Unknown Listener");
                    }
                }, throwable -> {
                    BHLogger.toast("Response is invalid");
                });
    }

    /**
     * 一つのツイートをリストに追加する時につかうよ
     *
     * @param status
     */
    final protected void insertTweet(final Status status) {
        getActivity().runOnUiThread(() -> {
            try {
                mAdapter.insert(status, 0);
                mAdapter.notifyDataSetChanged();
                listView.invalidateViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    final protected void insertAllTweet(@NonNull final List<Status> result) {
        insertAllTweet(result, true);
    }

    final protected void insertAllTweet(@NonNull final List<Status> result, final Boolean clear) {
        getActivity().runOnUiThread(() -> {
            try {
                if (clear) mAdapter.clear();
                mAdapter.addAll(result);
                if (clear) listView.setSelection(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Androidの通知用メソッド
     */
    final public void showNotification(int image, String title, String text, int id) {
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

    /**
     * タブに表示されるカラム名を返すよ
     *
     * @return
     */
    final public String getTitle() {
        try {
            BHLogger.println("getUserId", userId);
            BHLogger.println("getListType", listType);
            BHLogger.println("getUserObject", userObject);

            return this.listType.name() + " - " + this.userObject.getUserScreenName();
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    final protected void setArguments(@NonNull Long userId, @NonNull TimelineListType listType) {
        Bundle bundle = new Bundle();
        bundle.putLong(ARGS_USER_ID, userId);
        bundle.putInt(ARGS_LIST_TYPE, listType.index);
        this.setArguments(bundle);

        // for viewPager
        this.userId = userId;
        this.listType = listType;
    }

    public void invalidateListView(int targetPosition) {
        this.listView.getAdapter().getView(targetPosition, this.listView.getChildAt(targetPosition), this.listView);
    }
}
