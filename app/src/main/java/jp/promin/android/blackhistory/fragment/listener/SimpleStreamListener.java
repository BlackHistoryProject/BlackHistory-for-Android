package jp.promin.android.blackhistory.fragment.listener;

import android.support.annotation.NonNull;

import java.util.List;

import jp.promin.android.blackhistory.base.BaseStreamListener;

public interface SimpleStreamListener extends BaseStreamListener {
    void response(@NonNull List<twitter4j.Status> result);
}
