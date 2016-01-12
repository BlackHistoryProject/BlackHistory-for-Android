package com.nanami.android.blackhistory.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.ObservableUserStreamListener;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.adapter.MyFragmentPagerAdapter;
import com.nanami.android.blackhistory.dialog.SelectAccountDialogFragment;
import com.nanami.android.blackhistory.dialog.SelectTabKindDialogFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.RequestToken;

/**
 * Created by nanami on 2014/09/05.
 */
public class MainStreamActivity extends FragmentActivity {
    // Authorization Values //
    private String mCallBackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    //////////////////////////////

    // View Injecter //
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
    //////////////////////////////
    ArrayList<Long> userIds = new ArrayList<>();
    ArrayList<ObservableUserStreamListener> streams = new ArrayList<>();
    //////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TwitterUtils.hasAccessToken(this)) {
            setContentView(R.layout.fragment_main_stream);
            ButterKnife.bind(this);
            this.userIds = TwitterUtils.getAccountIds(this);
            this.mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            this.viewPager.setAdapter(this.mAdapter);

            if (userIds.size() > 0) {
                for (Long userId : userIds) {
                    if (this.isExistListener(userId)) continue;
                    BHLogger.println(userId + " load");
                    TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(this, userId);
                    ObservableUserStreamListener listener =  new ObservableUserStreamListener(this, userId);
                    twitterStream.addListener(listener);
                    twitterStream.user();
                    this.streams.add(listener);
                }
            }

//            this.mAdapter.addTab(TimelineListType.Home, userIds.get(0));
        }else {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
        }
    }


    public boolean isExistListener(Long userId){
        for (ObservableUserStreamListener listener : this.streams){
            if (listener.getUserId() == userId){
                return true;
            }
        }
        return false;
    }
}
