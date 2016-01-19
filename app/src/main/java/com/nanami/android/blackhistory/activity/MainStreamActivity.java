package com.nanami.android.blackhistory.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nanami.android.blackhistory.fragment.CommonStreamFragment;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.ObservableUserStreamListener;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.adapter.MyFragmentPagerAdapter;
import com.nanami.android.blackhistory.dialog.SelectAccountDialogFragment;
import com.nanami.android.blackhistory.dialog.SelectTabKindDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

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
    final static public String EXTRA_USER_ID = "extra_user_id";
    final static public String EXTRA_FROM_AUTH = "extra_from_auth";

    // View Injecter //
    @Bind(R.id.pager) ViewPager viewPager;
    public MyFragmentPagerAdapter mAdapter;

    public MainStreamActivity() {
    }

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
        startActivity(new Intent(this, TweetActivity.class));
    }

    @Bind(R.id.menuber_menu) ImageButton menuBar;
    @OnClick(R.id.menuber_menu) void OnClickMenu(){

    }
    //////////////////////////////
    HashMap<Long, ObservableUserStreamListener> streams = new HashMap<>();
    //////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TwitterUtils.hasAccessToken(this)) {
            setContentView(R.layout.fragment_main_stream);
            ButterKnife.bind(this);
            registerForContextMenu(menuBar);
            this.mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            this.viewPager.setAdapter(this.mAdapter);

            Boolean fromAuth = getIntent().getBooleanExtra(EXTRA_FROM_AUTH, false);
            Long userId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
                for (Long _userId :TwitterUtils.getAccountIds(this)) {
                    if (this.streams.containsKey(_userId)) continue;
                    TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(this, userId);
                    if (twitterStream == null){
                        continue;
                    }
                    ObservableUserStreamListener listener =  new ObservableUserStreamListener(this, userId);
                    twitterStream.addListener(listener);
                    twitterStream.user();

                    this.streams.put(userId, listener);
                }

            if (this.streams.size() == 0){
                TwitterUtils.deleteAllAccount(this);
                startActivity(new Intent(this, TwitterOAuthActivity.class));

            }

            if (fromAuth && userId > 0){
                this.mAdapter.addTab(TimelineListType.Home, userId);
            }


        }else {
            startActivity(new Intent(this, TwitterOAuthActivity.class));
        }
    }

    public static void startActivity(Context context, Long userId){
        Intent intent = new Intent(context, MainStreamActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_FROM_AUTH, true);
        context.startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_activity_main_stream, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                this.mAdapter.deleteTab(this.mAdapter.getItemAtIndex(viewPager.getCurrentItem()));
            default:
                return super.onContextItemSelected(item);
        }
    }
}
