package jp.promin.android.blackhistory.ui.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.event.FavoriteFailureEvent;
import jp.promin.android.blackhistory.event.FavoriteSuccessEvent;
import jp.promin.android.blackhistory.event.ReTweetFailureEvent;
import jp.promin.android.blackhistory.event.ReTweetSuccessEvent;
import jp.promin.android.blackhistory.model.UserToken;
import jp.promin.android.blackhistory.ui.mainstream.TweetAdapter;
import jp.promin.android.blackhistory.ui.mainstream.lists.ListStreamListener;
import jp.promin.android.blackhistory.ui.mainstream.lists.SimpleStreamListener;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;
import jp.promin.android.blackhistory.utils.BHLogger;
import jp.promin.android.blackhistory.utils.ShowToast;
import jp.promin.android.blackhistory.utils.rx.RxListener;
import jp.promin.android.blackhistory.utils.twitter.BaseStreamListener;
import jp.promin.android.blackhistory.utils.twitter.TwitterUtils;
import twitter4j.Status;
import twitter4j.Twitter;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
public abstract class CommonStreamFragment extends BaseFragment implements ListStreamListener {
    final static String ARGS_USER_ID = "args_user_id";
    final static String ARGS_LIST_TYPE = "args_list_type";
    @Bind(android.R.id.list)
    ListView listView;
    //////// getter setter //////////
    private UserToken userObject;
    private TweetAdapter mAdapter;
    private Twitter mTwitter;
    private BaseStreamListener listener;

    public final long getOwnerUserId() {
        return getArguments().getLong(ARGS_USER_ID);
    }

    public final TimelineListType getListType() {
        final int listType = getArguments().getInt(ARGS_LIST_TYPE);
        return TimelineListType.kindOf(listType);
    }

    protected final Twitter getTwitter() {
        return mTwitter;
    }

    //////////////////////////////////

    protected final void setListener(BaseStreamListener listener) {
        this.listener = listener;
    }

    @Override
    protected final int getLayoutId() {
        return R.layout.fragment_common_list;
    }

    @Override
    protected final void init() {
        this.userObject = TwitterUtils.getAccount(getOwnerUserId());
    }

    @Override
    final public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.mAdapter == null) {
            this.mAdapter = new TweetAdapter(getContext(), getOwnerUserId(), new TweetAdapter.Listener() {
                @Override
                public void onInvalidateList(int position) {
                    invalidateListView(position);
                }
            });
            this.listView.setAdapter(this.mAdapter);
        }
        if (this.mTwitter == null) {
            this.mTwitter = TwitterUtils.getTwitterInstance(getActivity(), getOwnerUserId());
        }

        setListener(this);
        reloadTimeLine();
    }

    /**
     * タイムラインの更新
     * 初回呼び出しや、リストをスクロールした時など
     */
    protected final void reloadTimeLine() {
        new RxListener<List<Status>>() {
            @Override
            public List<Status> result() throws Throwable {
                return listener.call(mTwitter);
            }
        }.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<Status>>() {
                    @Override
                    public void accept(List<Status> statuses) throws Exception {
                        if (listener instanceof SimpleStreamListener) {
                            ((SimpleStreamListener) listener).response(statuses);
                        } else if (listener instanceof ListStreamListener) {
                            ((ListStreamListener) listener).response(listView, mAdapter, statuses);
                        } else {
                            BHLogger.toast("Unknown Listener");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        BHLogger.toast("Response is invalid");
                    }
                });
    }

    protected final void insertTweet(final Status status) {
        try {
            mAdapter.insert(status, 0);
            mAdapter.notifyDataSetChanged();
            listView.invalidateViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final void insertAllTweet(@NonNull final List<Status> result) {
        insertAllTweet(result, true);
    }

    protected final void insertAllTweet(@NonNull final List<Status> result, final Boolean clear) {
        try {
            if (clear) mAdapter.clear();
            mAdapter.addAll(result);
            if (clear) listView.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final String getTitle() {
        try {
            return getListType().name() + " - " + this.userObject.getUserScreenName();
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    protected final void setArguments(@NonNull Long userId, @NonNull TimelineListType listType) {
        Bundle bundle = new Bundle();
        bundle.putLong(ARGS_USER_ID, userId);
        bundle.putInt(ARGS_LIST_TYPE, listType.getKind());
        setArguments(bundle);
    }

    public final void invalidateListView(int targetPosition) {
        this.listView.getAdapter().getView(targetPosition, this.listView.getChildAt(targetPosition), this.listView);
    }

    @Override
    protected final boolean shouldUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoriteSuccess(FavoriteSuccessEvent event) {
        ShowToast.showToast("ふぁぼった");
        mAdapter.updateStatus(event.getStatus());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoriteFailure(FavoriteFailureEvent event) {
        ShowToast.showToast(event.getThrowable().getLocalizedMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReTweetSuccess(ReTweetSuccessEvent event) {
        ShowToast.showToast("RTしました");
        mAdapter.updateStatus(event.getStatus());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReTweetFailure(ReTweetFailureEvent event) {
        ShowToast.showToast(event.getThrowable().getLocalizedMessage());
    }
}
