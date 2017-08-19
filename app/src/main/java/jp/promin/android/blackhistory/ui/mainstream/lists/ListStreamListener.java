package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.NonNull;
import android.widget.ListView;

import java.util.List;

import jp.promin.android.blackhistory.ui.mainstream.TweetAdapter;
import jp.promin.android.blackhistory.utils.twitter.BaseStreamListener;

public interface ListStreamListener extends BaseStreamListener {
    void response(ListView listView, TweetAdapter adapter, @NonNull List<twitter4j.Status> result);
}
