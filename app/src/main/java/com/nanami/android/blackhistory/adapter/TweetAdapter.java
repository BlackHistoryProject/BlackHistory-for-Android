package com.nanami.android.blackhistory.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nanami.android.blackhistory.component.PicassoImageView;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.fragment.list.CommonStreamFragment;
import com.nanami.android.blackhistory.utils.BHLogger;
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

    protected CommonStreamFragment owner;

    public TweetAdapter(CommonStreamFragment owner) {
        super(owner.getContext(), android.R.layout.simple_list_item_1);
        this.owner = owner;
        mInflater = (LayoutInflater) owner.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        private TweetAdapter adapter;
        private final Long ownerUserId;
        private final Activity context;
        private Status status;

        @Bind(R.id.icon) PicassoImageView icon;
        @Bind(R.id.name) TextView name;
        @Bind(R.id.screen_name) TextView screenName;
        @Bind(R.id.text) TextView text;
        @Bind(R.id.time) TextView time;
        @Bind(R.id.via) TextView via;

        @Bind(R.id.reply) ImageButton replyButton;
        @OnClick(R.id.reply) void OnClickReply(){
            TweetActivity.startActivity(context, this.ownerUserId, status, true);
        }

        @Bind(R.id.retweet)
        ImageButton retweetButton;
        @OnClick(R.id.retweet) void OnClickReTweet(ImageButton button){
            BHLogger.toast("一時的に止めています");
//            UserAction.retweet(context, button, ownerUserId, status, new UserAction.Callback() {
//                @Override
//                public void finish(Status status) {
//                    adapter.updateStatus(status);
//                }
//            });
//            , new UserAction.Response() {
//                @Override
//                public void response(Status result) {
//                    adapter.updateStatus(status);
//                }
//            });
        }

        @Bind(R.id.favorite)
        ImageButton favoriteButton;
        @OnClick(R.id.favorite) void OnClickFavorite(ImageButton button){

            BHLogger.toast("一時的に止めています");

//            UserAction.favorite(context, button, ownerUserId, status, new UserAction.Callback() {
//                @Override
//                public void finish(Status status) {
//                    adapter.updateStatus(status);
//                }
//            });
//                    , new UserAction.Response() {
//                @Override
//                public void response(Status result) {
//                    adapter.updateStatus(status);
//                }
//            });
        }

        @OnClick(R.id.menu) void OnClickMenu(){

        }

        @OnClick(R.id.tweet_item_main) void OnClickStatus(){
            TweetExpansionTweetActivity.startActivity(context, status);
        }

        public ViewHolder(View view, TweetAdapter adapter, Long ownerUserId){
            ButterKnife.bind(this, view);
            this.adapter = adapter;
            this.context = adapter.owner.getActivity();
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
            holder = new ViewHolder(convertView, this, owner.getUserId());
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

        holder.favoriteButton.setImageResource(item.isFavorited() ? android.R.drawable.star_on : android.R.drawable.star_off);
        holder.retweetButton.setImageResource(item.isRetweetedByMe() ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);

        return convertView;
    }

    @Nullable
    public Status getItemObject(Long statusId){
        for (int i = 0; i < this.getCount(); i ++){
            Status item = this.getItem(i);
            if (item.getId() == statusId) return item;
        }
        return null;
    }

    public int indexOf(Long statusId){
        for (int i = 0; i < this.getCount(); i ++){
            Status item = this.getItem(i);
            if (item.getId() == statusId) return i;
        }
        return -1;
    }

    public void updateStatus(Status status){
        /// ツイートが見つからなかった時はとりあえず何もしない
        final int pos = indexOf(status.getId());
        if (pos == -1){
            BHLogger.println("Status not found");
            return;
        }

        this.remove(getItem(pos));
        this.insert(status, pos);
        this.notifyDataSetChanged();

        try {
            this.owner.invalidateListView(pos);
        } catch (Exception e){
            e.printStackTrace();
        }
        BHLogger.println("Status updated");
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
