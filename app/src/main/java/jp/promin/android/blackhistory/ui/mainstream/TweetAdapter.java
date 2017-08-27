package jp.promin.android.blackhistory.ui.mainstream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.ui.tweet.TweetActivity;
import jp.promin.android.blackhistory.ui.tweet.TweetExpansionTweetActivity;
import jp.promin.android.blackhistory.utils.BHLogger;
import jp.promin.android.blackhistory.utils.BlackUtil;
import jp.promin.android.blackhistory.utils.LinkText;
import jp.promin.android.blackhistory.utils.ShowToast;
import jp.promin.android.blackhistory.utils.picture.ImageManager;
import jp.promin.android.blackhistory.utils.twitter.TwitterAction;
import twitter4j.Status;

public class TweetAdapter extends ArrayAdapter<Status> {
    private final long mUserId;
    private final Listener mListener;
    private ViewHolder.Listener mClickListener = new ViewHolder.Listener() {
        @Override
        public void onClickReply(@NonNull ImageButton button, @NonNull Status status) {
            TweetActivity.startActivity(getContext(), mUserId, status);
        }

        @Override
        public void onClickFavorite(@NonNull ImageButton button, @NonNull Status status) {
            TwitterAction.favorite(getContext(), button, mUserId, status);
        }

        @Override
        public void onClickReTweet(@NonNull ImageButton button, @NonNull Status status) {
            TwitterAction.reTweet(getContext(), button, mUserId, status);
        }

        @Override
        public void onClickDetail(@NonNull Status status) {
            TweetExpansionTweetActivity.createIntent(getContext(), status);
        }

        @Override
        public void onClickMenu(@NonNull Status status) {

        }
    };

    public TweetAdapter(@NonNull Context context, long userId, @NonNull Listener listener) {
        super(context, android.R.layout.simple_list_item_1);
        mUserId = userId;
        mListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tweet, parent, false);
            holder = new ViewHolder(convertView, mClickListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Status _item = getItem(position);
        assert _item != null;

        Status item = _item;

        if (_item.isRetweet()) {
            item = _item.getRetweetedStatus();
            holder.main.setBackgroundColor(Color.parseColor("#505050"));
            holder.via.setText("via " + BlackUtil.getVia(item.getSource()) + " ReTweeted by" + _item.getUser().getName());
        } else {
            holder.main.setBackgroundColor(Color.parseColor("#000000"));
            holder.via.setText("via " + BlackUtil.getVia(item.getSource()));
        }

        holder.setStatus(item);

        ImageManager.getPicasso().load(item.getUser().getProfileImageURL()).into(holder.icon);
        holder.name.setText(item.getUser().getName());
        holder.screenName.setText("@" + item.getUser().getScreenName());

        holder.text.setText(LinkText.setLinkText(getContext(), item.getText(), new LinkText.OnClickLinkListener() {
            @Override
            public void onClick(View widget, boolean isUrl, String clickText) {
                if (isUrl) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clickText)));
                } else {
                    ShowToast.showToast(String.format("タグやで %s", clickText));
                }
            }
        }));
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());

        holder.time.setText(BlackUtil.getDateFormat(item.getCreatedAt()));
        holder.favoriteButton.setImageResource(item.isFavorited() ? R.drawable.iconlarge_favourite : android.R.drawable.star_off);
        holder.retweetButton.setImageResource(item.isRetweetedByMe() ? R.drawable.iconlarge_retweet : R.drawable.iconlarge_retweet);

        return convertView;
    }

    private int indexOf(Long statusId) {
        for (int i = 0; i < this.getCount(); i++) {
            Status item = this.getItem(i);
            if (item.getId() == statusId) return i;
        }
        return -1;
    }

    public void updateStatus(Status status) {
        /// ツイートが見つからなかった時はとりあえず何もしない
        final int pos = indexOf(status.getId());
        if (pos == -1) {
            BHLogger.println("Status not found");
            return;
        }

        this.remove(getItem(pos));
        this.insert(status, pos);
        this.notifyDataSetChanged();

        try {
            // 応急処理
            mListener.onInvalidateList(pos);
//            ((CommonStreamFragment) getContext()).invalidateListView(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BHLogger.println("Status updated");
    }

    public interface Listener {
        void onInvalidateList(int position);
    }

    static class ViewHolder {
        private final Listener mListener;
        @Bind(R.id.tweet_item_main)
        RelativeLayout main;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.screen_name)
        TextView screenName;
        @Bind(R.id.text)
        TextView text;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.via)
        TextView via;
        @Bind(R.id.reply)
        ImageButton replyButton;
        @Bind(R.id.retweet)
        ImageButton retweetButton;
        @Bind(R.id.favorite)
        ImageButton favoriteButton;

        private Status mStatus;
        ViewHolder(View view, @NonNull Listener listener) {
            ButterKnife.bind(this, view);
            mListener = listener;
        }

        @OnClick(R.id.reply)
        void OnClickReply(ImageButton button) {
            mListener.onClickReply(button, mStatus);
        }

        @OnClick(R.id.retweet)
        void OnClickReTweet(ImageButton button) {
            mListener.onClickReTweet(button, mStatus);
        }

        @OnClick(R.id.favorite)
        void OnClickFavorite(ImageButton button) {
            mListener.onClickFavorite(button, mStatus);
        }

        @OnClick(R.id.menu)
        void OnClickMenu() {
            mListener.onClickMenu(mStatus);
        }

        @OnClick(R.id.tweet_item_main)
        void OnClickStatus() {
            mListener.onClickDetail(mStatus);
        }

        public void setStatus(@NonNull Status status) {
            mStatus = status;
        }

        interface Listener {
            void onClickReply(@NonNull ImageButton button, @NonNull Status status);

            void onClickFavorite(@NonNull ImageButton button, @NonNull Status status);

            void onClickReTweet(@NonNull ImageButton button, @NonNull Status status);

            void onClickDetail(@NonNull Status status);

            void onClickMenu(@NonNull Status status);
        }
    }
}
