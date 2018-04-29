package jp.promin.android.blackhistory.ui.mainstream;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pair;
import android.view.View;

import java.util.List;

import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.databinding.ActivityMainStreamBinding;
import jp.promin.android.blackhistory.model.UserToken;
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
    private static final String EXTRA_USER_ID = "extra_user_id";
    private static final String EXTRA_FROM_AUTH = "extra_from_auth";
    //////////////////////////////
    private final LongSparseArray<ObservableUserStreamListener> mUserStreams = new LongSparseArray<>();
    public MyFragmentPagerAdapter mAdapter;
    private ActivityMainStreamBinding mBinding;

    public static void startActivity(@NonNull Context context, long userId) {
        Intent intent = new Intent(context, MainStreamActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_FROM_AUTH, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_stream);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mBinding.pager.setAdapter(mAdapter);
        mBinding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuClick();
            }
        });
        mBinding.addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddAccountClick();
            }
        });
        mBinding.tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTweetClick();
            }
        });
        mBinding.settingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingTabClick();
            }
        });

        BlackHistoryController app = BlackHistoryController.get(this);
        if (app == null) return;

        if (app.getTokenManager().hasToken()) {
            startAllUserStreams();
            onStartFromAuthorization();
        } else {
            startActivity(new Intent(this, TwitterOAuthActivity.class));
        }
    }

    public Pair<Long, CommonStreamFragment> getCurrentTabUserId() {
        return mAdapter.getItemAtIndex(mBinding.pager.getCurrentItem());
    }

    private void onStartFromAuthorization() {
        long userId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
        if (userId == -1) {
            return;
        }
        boolean fromAuth = getIntent().getBooleanExtra(EXTRA_FROM_AUTH, false);
        if (fromAuth && userId > 0) {
            mAdapter.addTab(TimelineListType.Home, userId);
        }
    }

    private void startAllUserStreams() {
        BlackHistoryController app = BlackHistoryController.get(this);
        if (app == null) return;

        List<UserToken> userTokens = app.getTokenManager().getAllToken();

        for (UserToken token : userTokens) {
            if (mUserStreams.indexOfKey(token.getId()) > 0) return;
            TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(this, token);
            ObservableUserStreamListener listener = new ObservableUserStreamListener(this, token.getId());
            twitterStream.addListener(listener);
            twitterStream.user();
            mUserStreams.append(token.getId(), listener);
        }
    }

    private void onAddAccountClick() {
        SelectAccountDialogFragment
                .newInstance(R.string.SELECT_ACCOUNT_TYPE__CHANGE_ACCOUNT)
                .show(getSupportFragmentManager(), "select_account");
    }

    private void onSettingTabClick() {
        SelectTabKindDialogFragment
                .newInstance()
                .show(getSupportFragmentManager(), "a");
    }

    private void onTweetClick() {
        //アクティビティを開く　ここだとつぶやきに飛ぶ
        TweetActivity.startActivity(this, getCurrentTabUserId().first);
    }

    private void onMenuClick() {
        CustomDialogFragment.newInstance("", R.array.menu,
                new CustomDialogFragment.DialogListener() {
                    @Override
                    public void onClick(String[] menuRes, int position) {
                        switch (position) {
                            case 0: //リスト削除
                                break;
                            case 1: //設定
                                break;
                        }
                    }
                })
                .show(getSupportFragmentManager(), "menu");
    }
}
