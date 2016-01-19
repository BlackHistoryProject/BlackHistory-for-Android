package com.nanami.android.blackhistory.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nanami.android.blackhistory.component.PicassoImageView;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.utils.BlackUtil;
import com.nanami.android.blackhistory.activity.TweetExpansionTweetActivity;
import com.nanami.android.blackhistory.activity.TweetActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.Status;

/**
 * Created by nanami on 2014/09/05.
 */

public class TweetAdapter extends ArrayAdapter<Status> {

    private LayoutInflater mInflater;

    final private Long ownerUserId;

    public TweetAdapter(Context context, Long ownerUserId) {
        super(context, android.R.layout.simple_list_item_1);
        this.ownerUserId = ownerUserId;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        private final Long ownerUserId;
        private final Context context;
        private Status status;

        @Bind(R.id.icon) PicassoImageView icon;
        @Bind(R.id.name) TextView name;
        @Bind(R.id.screen_name) TextView screenName;
        @Bind(R.id.text) TextView text;
        @Bind(R.id.time) TextView time;
        @Bind(R.id.via) TextView via;

        @OnClick(R.id.reply) void OnClickReply(){
            TweetActivity.startActivity(context, this.ownerUserId, status, true);
        }

        @OnClick(R.id.retweet) void OnClickReTweet(){

        }

        @OnClick(R.id.favorite) void OnClickFavorite(){

        }

        @OnClick(R.id.menu) void OnClickMenu(){

        }

        @OnClick(R.id.tweet_item_main) void OnClickStatus(){
            TweetExpansionTweetActivity.startActivity(context, status);
        }

        public ViewHolder(View view, Long ownerUserId){
            ButterKnife.bind(this, view);
            this.context = view.getContext();
            this.ownerUserId = ownerUserId;
        }

        public void setStatus(Status status){
            this.status = status;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_tweet, parent, false);
            holder = new ViewHolder(convertView, ownerUserId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Status item = getItem(position);
        holder.setStatus(item);

        holder.icon.loadImage(item.getUser().getProfileImageURL());
        holder.name.setText(item.getUser().getName());
        holder.screenName.setText("@" + item.getUser().getScreenName());
        holder.text.setText(item.getText());
        holder.time.setText(BlackUtil.getDateFormat(item.getCreatedAt()));
        holder.via.setText("via " + BlackUtil.getVia(item.getSource()));

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
