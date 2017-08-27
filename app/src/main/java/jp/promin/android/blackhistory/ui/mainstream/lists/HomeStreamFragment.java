package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import jp.promin.android.blackhistory.event.TwitterStreamEvent;
import jp.promin.android.blackhistory.ui.common.CommonStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.TweetAdapter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

final public class HomeStreamFragment extends CommonStreamFragment {

    @Nullable
    public static HomeStreamFragment newInstance(@NonNull Long userId) {
        if (2 > userId) return null;
        HomeStreamFragment fragment = new HomeStreamFragment();
        fragment.setArguments(userId, TimelineListType.Home);
        return fragment;
    }

    @Override
    public void response(ListView listView, TweetAdapter adapter, @NonNull List<Status> result) {
        insertAllTweet(result);
    }

    @Nullable
    @Override
    public List<Status> call(Twitter twitter) throws TwitterException {
        return twitter.getHomeTimeline();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTwitterStream(TwitterStreamEvent event) {
        // このリストのオーナー宛の情報かどうかのチェック
        if (event.getUserId() != getOwnerUserId()) return;

        insertTweet(event.getStatus());
    }
}
