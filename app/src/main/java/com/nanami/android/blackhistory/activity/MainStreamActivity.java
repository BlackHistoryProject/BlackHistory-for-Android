package com.nanami.android.blackhistory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nanami.android.blackhistory.utils.ObservableUserStreamListener;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.Globals;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.adapter.MyFragmentPagerAdapter;
import com.nanami.android.blackhistory.dialog.SelectAccountDialogFragment;
import com.nanami.android.blackhistory.dialog.SelectTabKindDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.TwitterStream;

/**
 * Created by nanami on 2014/09/05.
 */
public class MainStreamActivity extends FragmentActivity {

    @Bind(R.id.pager) ViewPager viewPager;
    public MyFragmentPagerAdapter mAdapter;

    @OnClick(R.id.Geolocation) void OnClickGeo(){

    }

    @OnClick(R.id.account) void  OnClickAccount(){
        SelectAccountDialogFragment
                .newInstance(R.string.SELECT_ACCOUNT_TYPE__CHANGE_ACCOUNT)
                .show(getSupportFragmentManager(), "select_account");
    }

    @OnClick(R.id.addList) void  OnClickAddList(){
        SelectTabKindDialogFragment
                .newInstance()
                .show(getSupportFragmentManager(), "a");
    }

    @OnClick(R.id.menu_tweet) void OnClickTweet(){
        //アクティビティを開く　ここだとつぶやきに飛ぶ
        startActivity(new Intent(MainStreamActivity.this, TweetActivity.class));
    }

    @OnClick(R.id.menuber_menu) void OnClickMenu(){

    }

    long userId;
    boolean hasToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtils.hasAccessToken(this)) {
            hasToken = false;
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
        }else {
            hasToken = true;
            setContentView(R.layout.fragment_main_stream);
            ButterKnife.bind(this);

            Globals globals = (Globals) getApplication();

            // Load Account List
            TwitterUtils.Account _account = TwitterUtils.getAccount(this);
            if(_account != null){
                globals.initializeList();
                for(TwitterUtils.Account account : _account.accounts){
                    globals.addAccount(new Globals.Account(account.userId, account.screenName));
                    this.userId = account.userId;
                }
            }

            this.mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            this.viewPager.setAdapter(this.mAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (hasToken) {
            TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(this);
            {
                twitterStream.addListener(new ObservableUserStreamListener(this, this.userId));
                twitterStream.user();
            }
        }
    }
}
