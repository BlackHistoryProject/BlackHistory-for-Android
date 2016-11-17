package jp.promin.android.blackhistory.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.activity.TweetActivity;
import jp.promin.android.blackhistory.activity.TweetExpansionTweetActivity;
import jp.promin.android.blackhistory.fragment.list.CommonStreamFragment;
import jp.promin.android.blackhistory.utils.BHLogger;
import jp.promin.android.blackhistory.utils.BlackUtil;
import jp.promin.android.blackhistory.utils.ImageManager;
import jp.promin.android.blackhistory.utils.LinkText;
import jp.promin.android.blackhistory.utils.ShowToast;
import jp.promin.android.blackhistory.utils.UserAction;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.Status;

/**
 * Created by nanami on 2014/09/05.
 */

public class TweetAdapter extends ArrayAdapter<Status> {

    private LayoutInflater mInflater;

    private CommonStreamFragment owner;

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

        @OnClick(R.id.reply)
        void OnClickReply() {
            TweetActivity.createIntent(context, this.ownerUserId, status, true);
        }

        @Bind(R.id.retweet)
        ImageButton retweetButton;

        @OnClick(R.id.retweet)
        void OnClickReTweet(ImageButton button) {
            // リツイート
            UserAction.retweet(this.adapter.getContext(), button, this.ownerUserId, status, new UserAction.Callback() {
                @Override
                public void result(Status status) {
                    ShowToast.showToast("RTしました");
                    adapter.updateStatus(status);
                }

                @Override
                public void error(Throwable error) {
                    ShowToast.showToast(error.getLocalizedMessage());
                }
            });
        }

        @Bind(R.id.favorite)
        ImageButton favoriteButton;

        @OnClick(R.id.favorite)
        void OnClickFavorite(ImageButton button) {
            // お気に入り
            UserAction.favorite(this.adapter.getContext(), button, this.ownerUserId, status, new UserAction.Callback() {
                @Override
                public void result(Status status) {
                    ShowToast.showToast("ふぁぼった");
                    adapter.updateStatus(status);
                }

                @Override
                public void error(Throwable error) {
                    ShowToast.showToast(error.getLocalizedMessage());
                }
            });
        }

        @OnClick(R.id.menu)
        void OnClickMenu() {

        }

        @OnClick(R.id.tweet_item_main)
        void OnClickStatus() {
            TweetExpansionTweetActivity.createIntent(context, status);
        }

        public ViewHolder(View view, TweetAdapter adapter, Long ownerUserId) {
            ButterKnife.bind(this, view);
            this.adapter = adapter;
            this.context = adapter.owner.getActivity();
            this.ownerUserId = ownerUserId;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_tweet, parent, false);
            holder = new ViewHolder(convertView, this, owner.getUserId());
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

        holder.text.setText(LinkText.setLinkText(getContext(), item.getText(), (widget, isUrl, clickText) -> {
            if (isUrl) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clickText)));
            } else {
                ShowToast.showToast(String.format("タグやで %s", clickText));
            }
        }));
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());

        holder.time.setText(BlackUtil.getDateFormat(item.getCreatedAt()));
        holder.favoriteButton.setImageResource(item.isFavorited() ? R.drawable.iconlarge_favourite : android.R.drawable.star_off);
        holder.retweetButton.setImageResource(item.isRetweetedByMe() ? R.drawable.iconlarge_retweet : R.drawable.iconlarge_retweet);

        return convertView;
    }

    @Nullable
    public Status getItemObject(Long statusId) {
        for (int i = 0; i < this.getCount(); i++) {
            Status item = this.getItem(i);
            if (item.getId() == statusId) return item;
        }
        return null;
    }

    private int indexOf(Long statusId) {
        for (int i = 0; i < this.getCount(); i++) {
            Status item = this.getItem(i);
            if (item.getId() == statusId) return i;
        }
        return -1;
    }

    private void updateStatus(Status status) {
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
            this.owner.invalidateListView(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BHLogger.println("Status updated");
    }

    public void deleteTweet(long paramLong) {
        Status status = null;
        for (int i = 0; i < this.getCount(); i++) {
            if (this.getItem(i).getId() == paramLong) {
                status = this.getItem(i);
            }
        }
        if (status != null) {
            this.remove(status);
        }
        notifyDataSetInvalidated();
    }
}
