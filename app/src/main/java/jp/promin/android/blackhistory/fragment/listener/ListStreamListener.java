package jp.promin.android.blackhistory.fragment.listener;

import android.support.annotation.NonNull;
import android.widget.ListView;

import jp.promin.android.blackhistory.adapter.TweetAdapter;
import jp.promin.android.blackhistory.base.BaseStreamListener;

import java.util.List;

/**
 * Created by atsumi on 2016/01/19.
 */
public interface ListStreamListener extends BaseStreamListener {
    void response(ListView listView, TweetAdapter adapter, @NonNull List<twitter4j.Status> result);
}
