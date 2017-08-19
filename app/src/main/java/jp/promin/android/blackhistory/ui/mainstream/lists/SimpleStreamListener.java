package jp.promin.android.blackhistory.ui.mainstream.lists;

import android.support.annotation.NonNull;

import java.util.List;

import jp.promin.android.blackhistory.utils.twitter.BaseStreamListener;

public interface SimpleStreamListener extends BaseStreamListener {
    void response(@NonNull List<twitter4j.Status> result);
}
