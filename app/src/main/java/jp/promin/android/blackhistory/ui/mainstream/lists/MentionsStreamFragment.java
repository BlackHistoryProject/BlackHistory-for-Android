package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import jp.promin.android.blackhistory.event.TwitterReplyEvent;
import jp.promin.android.blackhistory.ui.common.CommonStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.TweetAdapter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

final public class MentionsStreamFragment extends CommonStreamFragment {
    @Nullable
    public static MentionsStreamFragment newInstance(long userId) {
        if (2 > userId) return null;
        MentionsStreamFragment fragment = new MentionsStreamFragment();
        fragment.setArguments(userId, TimelineListType.Mentions);
        return fragment;
    }

    @Override
    public void response(ListView listView, TweetAdapter adapter, @NonNull List<Status> result) {
        insertAllTweet(result);
    }

    @Nullable
    @Override
    public List<Status> call(Twitter twitter) throws TwitterException {
        return twitter.getMentionsTimeline();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTwitterReply(TwitterReplyEvent event) {
        // 1.このリストのオーナー宛の情報かどうかのチェック
        if (event.getUserId() != getOwnerUserId()) return;

        // 2. 自分宛てのリプじゃない時は何もしない
        if (event.getStatus().getInReplyToUserId() != getOwnerUserId()) return;

        insertTweet(event.getStatus());
    }
}
