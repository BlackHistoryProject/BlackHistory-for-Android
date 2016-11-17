package jp.promin.android.blackhistory.fragment.listener;

import android.support.annotation.NonNull;

import jp.promin.android.blackhistory.base.BaseStreamListener;

import java.util.List;

/**
 * Created by atsumi on 2016/01/19.
 */
public interface SimpleStreamListener extends BaseStreamListener {
    void response(@NonNull List<twitter4j.Status> result);
}
