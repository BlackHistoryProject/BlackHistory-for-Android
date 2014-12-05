package com.nanami.chikechike.testhistory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nanami.chikechike.myapplication.R;
import com.nanami.chikechike.testhistory.MyFragmentPagerAdapter;

/**
 * Created by nanami on 2014/09/05.
 */
public class MainStreamActivity extends FragmentActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActionBar().setSplitBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.back_actionbar)); <= ここで上下のメニューバー表示させる
        setContentView(R.layout.fragment_main_stream);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(
                new MyFragmentPagerAdapter(
                        getSupportFragmentManager()));

        findViewById(R.id.Geolocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.addList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
