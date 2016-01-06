package com.nanami.android.blackhistory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.Globals;
import com.nanami.android.blackhistory.TwitterUtils;
import com.nanami.android.blackhistory.adapter.MyFragmentPagerAdapter;
import com.nanami.android.blackhistory.dialog.SelectAccountDialogFragment;
import com.nanami.android.blackhistory.dialog.SelectTabKindDialogFragment;

/**
 * Created by nanami on 2014/09/05.
 */
public class MainStreamActivity extends FragmentActivity {
    ViewPager viewPager;
    public MyFragmentPagerAdapter mAdapter;
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
        }else {
            globals = (Globals) getApplication();
            // Load Account List
            TwitterUtils.Account _account = TwitterUtils.getAccount(this);
            if(_account != null){
                globals.initializeList();
                for(TwitterUtils.Account account : _account.accounts){
                    globals.addAccount(new Globals.Account(account.userId, account.screenName));
                }
            }

            // getActionBar().setSplitBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.back_actionbar)); <= ここで上下のメニューバー表示させる
            setContentView(R.layout.fragment_main_stream);
            mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(mAdapter);

            findViewById(R.id.Geolocation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            findViewById(R.id.account).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectAccountDialogFragment dialogFragment = SelectAccountDialogFragment.newInstance(R.string.SELECT_ACCOUNT_TYPE__CHANGE_ACCOUNT);
                    dialogFragment.show(getSupportFragmentManager(), "select_account");
                }
            });

            findViewById(R.id.addList).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectTabKindDialogFragment fragment = SelectTabKindDialogFragment.newInstance();
                    fragment.show(getSupportFragmentManager(), "a");
                }
            });

            findViewById(R.id.menu_tweet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainStreamActivity.this, TweetActivity.class);   //アクティビティを開く　ここだとつぶやきに飛ぶ
                    startActivity(intent);
                }
            });

            findViewById(R.id.menuber_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

   /* public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tweet:
                Intent intent = new Intent(this, TweetActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
