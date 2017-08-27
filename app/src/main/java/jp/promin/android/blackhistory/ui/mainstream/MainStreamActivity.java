package jp.promin.android.blackhistory.ui.mainstream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;

import com.github.gfx.android.orma.Inserter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.model.ShowList;
import jp.promin.android.blackhistory.model.ShowList_Deleter;
import jp.promin.android.blackhistory.ui.common.BaseActivity;
import jp.promin.android.blackhistory.ui.common.CommonStreamFragment;
import jp.promin.android.blackhistory.ui.common.CustomDialogFragment;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;
import jp.promin.android.blackhistory.ui.tweet.TweetActivity;
import jp.promin.android.blackhistory.ui.twitter.TwitterOAuthActivity;
import jp.promin.android.blackhistory.utils.twitter.ObservableUserStreamListener;
import jp.promin.android.blackhistory.utils.twitter.TwitterUtils;
import twitter4j.TwitterStream;

public class MainStreamActivity extends BaseActivity {
    final static public String EXTRA_USER_ID = "extra_user_id";
    final static public String EXTRA_FROM_AUTH = "extra_from_auth";
    public MyFragmentPagerAdapter mAdapter;
    @Bind(R.id.pager)
    ViewPager viewPager;
    @Bind(R.id.menuber_menu)
    ImageButton menuBar;
    //////////////////////////////
    HashMap<Long, ObservableUserStreamListener> streams = new HashMap<>();

    public MainStreamActivity() {
    }

    public static void startActivity(Context context, Long userId) {
        Intent intent = new Intent(context, MainStreamActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_FROM_AUTH, true);
        context.startActivity(intent);
    }

    @OnClick(R.id.Geolocation)
    void OnClickGeo() {

    }

    @OnClick(R.id.account)
    void OnClickAccount() {
        SelectAccountDialogFragment
                .newInstance(R.string.SELECT_ACCOUNT_TYPE__CHANGE_ACCOUNT)
                .show(getSupportFragmentManager(), "select_account");
    }

    @OnClick(R.id.addList)
    void OnClickAddList() {
        SelectTabKindDialogFragment
                .newInstance()
                .show(getSupportFragmentManager(), "a");
    }

    @OnClick(R.id.menu_tweet)
    void OnClickTweet() {
        //アクティビティを開く　ここだとつぶやきに飛ぶ
        TweetActivity.startActivity(this, getCurrentTabUserId().first);
    }
    //////////////////////////////

    @OnClick(R.id.menuber_menu)
    void OnClickMenu() {
        CustomDialogFragment.newInstance("", R.array.menu,
                new CustomDialogFragment.DialogListener() {
                    @Override
                    public void onClick(String[] menuRes, int position) {
                        switch (position) {
                            case 0: //リスト削除
                                removeTab();
                                break;
                            case 1: //設定
                                break;
                        }
                    }
                })
                .show(getSupportFragmentManager(), "menu");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TwitterUtils.hasAccessToken(this)) {
            setContentView(R.layout.fragment_main_stream);
            ButterKnife.bind(this);
            this.mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            this.viewPager.setAdapter(this.mAdapter);

            Boolean fromAuth = getIntent().getBooleanExtra(EXTRA_FROM_AUTH, false);
            Long userId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
            for (Long _userId : TwitterUtils.getAccountIds(this)) {
                if (this.streams.containsKey(_userId)) continue;
                TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(this, _userId);
                ObservableUserStreamListener listener = new ObservableUserStreamListener(this, _userId);
                if (twitterStream != null) {
                    twitterStream.addListener(listener);
                    twitterStream.user();
                }

                this.streams.put(_userId, listener);
                //this.mAdapter.addTab(TimelineListType.Home, _userId);
            }

            if (this.streams.size() == 0) {
                //TwitterUtils.deleteAllAccount(this);
                //createIntent(new Intent(this, TwitterOAuthActivity.class));
            }

            if (fromAuth && userId > 0) {
                this.mAdapter.addTab(TimelineListType.Home, userId);
            }
        } else {
            startActivity(new Intent(this, TwitterOAuthActivity.class));
        }
    }

    public Pair<Long, CommonStreamFragment> getCurrentTabUserId() {
        return this.mAdapter.getItemAtIndex(this.viewPager.getCurrentItem());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadTabs();
    }

    @Override
    protected void onStop() {
        saveTabs();
        super.onStop();
    }

    private void saveTabs() {
        final BlackHistoryController app = BlackHistoryController.get(this);
        final Inserter<ShowList> db = app.getDatabase().relationOfShowList().upserter();

        Observable.fromIterable(mAdapter.getTab())
                .map(new Function<Pair<Long, CommonStreamFragment>, ShowList>() {
                    @Override
                    public ShowList apply(@NonNull Pair<Long, CommonStreamFragment> item) throws Exception {
                        return new ShowList(item.second.getListType(), item.first);
                    }
                })
                .toList()
                .flatMapObservable(new Function<List<ShowList>, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(@NonNull List<ShowList> showLists) throws Exception {
                        return db.executeAllAsObservable(showLists);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void loadTabs() {
        BlackHistoryController.get(this).getDatabase()
                .selectFromShowList().executeAsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ShowList>() {
                    @Override
                    public void accept(ShowList showList) throws Exception {
                        mAdapter.addTab(showList.getListType(), showList.getUserId());
                    }
                });
    }

    private void removeTab() {
        final Pair<Long, CommonStreamFragment> item = getCurrentTabUserId();
        final ShowList targetList = new ShowList(item.second.getListType(), item.first);
        final BlackHistoryController app = BlackHistoryController.get(this);
        final ShowList_Deleter deleter = app.getDatabase().relationOfShowList().deleter();

        deleter
                .where("hash = ?", targetList.getHash())
                .executeAsSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        int current = viewPager.getCurrentItem();
                        mAdapter.remove(current);
                        ArrayList<Pair<Long, CommonStreamFragment>> list = new ArrayList<>();
                        list.addAll(mAdapter.getTab());
                        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list); //Refresh page caches
                        viewPager.setAdapter(mAdapter);
                        viewPager.setCurrentItem(current, false);
                    }
                });
    }
}
