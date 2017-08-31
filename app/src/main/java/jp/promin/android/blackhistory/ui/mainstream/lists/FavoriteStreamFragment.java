package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import jp.promin.android.blackhistory.event.TwitterFavoriteEvent;
import jp.promin.android.blackhistory.ui.common.CommonStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.TweetAdapter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

final public class FavoriteStreamFragment extends CommonStreamFragment {
    @Nullable
    public static FavoriteStreamFragment newInstance(@NonNull Long userId) {
        if (2 > userId) return null;
        FavoriteStreamFragment fragment = new FavoriteStreamFragment();
        fragment.setArguments(userId, TimelineListType.Favorites);
        return fragment;
    }

    @Override
    public void response(ListView listView, TweetAdapter adapter, @NonNull List<Status> result) {
        insertAllTweet(result, true);
    }

    @Nullable
    @Override
    public List<Status> call(Twitter twitter) throws TwitterException {
        return twitter.getFavorites();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTwitterFavorite(TwitterFavoriteEvent event) {
        if (event.getUserId() != getOwnerUserId()) return;
        insertTweet(event.getFavoritedStatus());
    }
}
