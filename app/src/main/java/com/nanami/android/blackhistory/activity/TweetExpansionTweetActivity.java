package com.nanami.android.blackhistory.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.BlackUtil;
import com.nanami.android.blackhistory.TweetSerialize;

import butterknife.Bind;
import twitter4j.Status;

/**
 * Created by nanami on 2014/09/11.
 */
public class TweetExpansionTweetActivity extends CommonActivityAbstract{
    Status status;                                                                              //StatusはここではTLにある１つ１つのツイートの事

    @Bind(R.id.expansion_icon) SmartImageView imageUserIcon;
    @Bind(R.id.expansion_name) TextView textUserName;
    @Bind(R.id.expansion_screen_name) TextView textUserScreenName;
    @Bind(R.id.expansion_text) TextView textUserTweet;
    @Bind(R.id.expansion_time) TextView textUserTime;
    @Bind(R.id.expansion_via) TextView textUserVia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.onetap_expansion_tweet);

        status = ((TweetSerialize) getIntent().getSerializableExtra("tweet")).getStatus();
        imageUserIcon.setImageUrl(status.getUser().getProfileImageURL());
        textUserName.setText(status.getUser().getName());
        textUserScreenName.setText("@" + status.getUser().getScreenName());
        textUserTweet.setText(status.getText());
        textUserTime.setText(BlackUtil.getDateFormat(status.getCreatedAt()));
        textUserVia.setText("via " + BlackUtil.getVia(status.getSource()));
    }
}
