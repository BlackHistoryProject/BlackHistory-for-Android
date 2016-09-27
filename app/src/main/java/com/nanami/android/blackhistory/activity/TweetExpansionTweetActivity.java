package com.nanami.android.blackhistory.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.base.BaseActivity;
import com.nanami.android.blackhistory.component.PicassoImageView;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.BlackUtil;

import butterknife.Bind;
import twitter4j.Status;

/**
 * Created by nanami on 2014/09/11.
 */
public class TweetExpansionTweetActivity extends BaseActivity {
    final private static String EXTRA_STATUS = "extra_status";
    private Status status;                                                                              //StatusはここではTLにある１つ１つのツイートの事

    @Bind(R.id.expansion_icon)
    PicassoImageView imageUserIcon;
    @Bind(R.id.expansion_name) TextView textUserName;
    @Bind(R.id.expansion_screen_name) TextView textUserScreenName;
    @Bind(R.id.expansion_text) TextView textUserTweet;
    @Bind(R.id.expansion_time) TextView textUserTime;
    @Bind(R.id.expansion_via) TextView textUserVia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.onetap_expansion_tweet);

        this.status = (Status) getIntent().getSerializableExtra(EXTRA_STATUS);
        if (this.status == null){
            BHLogger.toast("ツイートの読み込みに失敗しました");
            finish();
            return;
        }

        this.imageUserIcon.loadImage(status.getUser().getProfileImageURL());
        this.textUserName.setText(status.getUser().getName());
        this.textUserScreenName.setText("@" + this.status.getUser().getScreenName());
        this.textUserTweet.setText(status.getText());
        this.textUserTime.setText(BlackUtil.getDateFormat(status.getCreatedAt()));
        this.textUserVia.setText("via " + BlackUtil.getVia(this.status.getSource()));
    }

    public static void startActivity(Context context, Status status){
        Intent intent = new Intent(context, TweetExpansionTweetActivity.class);
        intent.putExtra(EXTRA_STATUS, status);
        context.startActivity(intent);
    }

}
