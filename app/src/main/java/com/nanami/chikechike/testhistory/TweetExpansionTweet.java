package com.nanami.chikechike.testhistory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.nanami.chikechike.myapplication.R;

import twitter4j.Status;

/**
 * Created by nanami on 2014/09/11.
 */
public class TweetExpansionTweet extends Activity{
    Status status;                                                                              //StatusはここではTLにある１つ１つのツイートの事
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.onetap_expansion_tweet);
        status = ((TweetSerialize)getIntent().getSerializableExtra("tweet")).getStatus();


        SmartImageView expansion_icon = (SmartImageView)findViewById(R.id.expansion_icon);
        expansion_icon.setImageUrl(status.getUser().getProfileImageURL());

        TextView expansion_name = (TextView)findViewById(R.id.expansion_name);
        expansion_name.setText(status.getUser().getName());

        TextView expansion_screen_name = (TextView)findViewById(R.id.expansion_screen_name);
        expansion_screen_name.setText("@" + status.getUser().getScreenName());

        TextView expansion_text = (TextView)findViewById(R.id.expansion_text);
        expansion_text.setText(status.getText());

        TextView expansion_time = (TextView)findViewById(R.id.expansion_time);
        expansion_time.setText(BlackUtil.getDateFormat(status.getCreatedAt()));

        TextView expansion_via = (TextView)findViewById(R.id.expansion_via);
        expansion_via.setText("via " +  BlackUtil.getVia(status.getSource()));

        }


}
