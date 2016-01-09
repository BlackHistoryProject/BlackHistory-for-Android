package com.nanami.android.blackhistory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nanami.android.blackhistory.EventBusHolder;
import com.nanami.android.blackhistory.ObservableUserStreamListener;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.Globals;
import com.nanami.android.blackhistory.TwitterUtils;
import com.nanami.android.blackhistory.adapter.MyFragmentPagerAdapter;
import com.nanami.android.blackhistory.dialog.SelectAccountDialogFragment;
import com.nanami.android.blackhistory.dialog.SelectTabKindDialogFragment;
import com.nanami.android.blackhistory.event.TwitterStreamEvent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

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

    ArrayList<TwitterStream> listeners

    String screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
        }else {
            setContentView(R.layout.fragment_main_stream);
            ButterKnife.bind(this);

            Globals globals = (Globals) getApplication();

            // Load Account List
            TwitterUtils.Account _account = TwitterUtils.getAccount(this);
            if(_account != null){
                globals.initializeList();
                for(TwitterUtils.Account account : _account.accounts){
                    globals.addAccount(new Globals.Account(account.userId, account.screenName));
                    this.screenName = account.screenName;
                }
            }

            this.mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            this.viewPager.setAdapter(this.mAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(this);
        {
            twitterStream.addListener(new ObservableUserStreamListener(this.screenName));
            twitterStream.user();
        }
    }
}
