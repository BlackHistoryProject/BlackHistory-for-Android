package com.nanami.chikechike.testhistory.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.nanami.chikechike.myapplication.R;
import com.nanami.chikechike.testhistory.BlackUtil;
import com.nanami.chikechike.testhistory.activity.TweetExpansionTweetActivity;
import com.nanami.chikechike.testhistory.TweetSerialize;
import com.nanami.chikechike.testhistory.activity.TweetActivity;

import twitter4j.Status;

/**
 * Created by nanami on 2014/09/05.
 */

public class TweetAdapter extends ArrayAdapter<Status> {

    private LayoutInflater mInflater;

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_tweet,null);
        }
        final Status item = getItem(position);

        RelativeLayout tweetItemMain = (RelativeLayout) convertView.findViewById(R.id.tweet_item_main);
        tweetItemMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TweetExpansionTweetActivity.class);              // TLのツイートを押した時そのツイートが拡大される
                intent.putExtra("tweet", new TweetSerialize(item));
                getContext().startActivity(intent);
            }
        }) ;

        SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
        icon.setImageUrl(item.getUser().getProfileImageURL());

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(item.getUser().getName());

        TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
        screenName.setText("@" + item.getUser().getScreenName());

        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(item.getText());

        TextView time = (TextView) convertView.findViewById(R.id.time);
        time.setText(BlackUtil.getDateFormat(item.getCreatedAt()));


        TextView via = (TextView) convertView.findViewById(R.id.via);
        via.setText("via " + BlackUtil.getVia(item.getSource()));

        ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.tweet_reply1);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TweetActivity.class);
                intent.putExtra("tweet", new TweetSerialize(item));
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
    public void deleteTweet(long paramLong)
    {
        Status status = null;
        for (int i = 0; i < this.getCount(); i++){
            if(this.getItem(i).getId() == paramLong){
                status = this.getItem(i);
            }
        }
        if(status != null){
            this.remove(status);
        }
        notifyDataSetInvalidated();
    }
}
