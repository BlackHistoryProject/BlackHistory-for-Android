package com.nanami.android.blackhistory.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;

import com.nanami.android.blackhistory.fragment.list.TimelineListType;
import com.nanami.android.blackhistory.model.ModelAccessTokenObject;
import com.nanami.android.blackhistory.utils.TwitterUtils;
import com.nanami.android.blackhistory.adapter.TweetAdapter;

/**
 * Created by Telneko on 2015/01/17.
 */
public class CommonStreamFragment extends ListFragment {
     final String ARGS_USER_ID = "args_user_id";

    public TweetAdapter mAdapter;

    @Nullable private TimelineListType listType;
    @Nullable private Long userId;
    @Nullable private ModelAccessTokenObject userObject;

    @Nullable
    public Long getUserId() {
        return userId;
    }

    @Nullable
    public TimelineListType getListType() {
        return listType;
    }

    @Nullable
    public ModelAccessTokenObject getUserObject() {
        return userObject;
    }

    final public void setParams(@NonNull TimelineListType listType,@NonNull Long userId){
        this.listType = listType;
        this.userId = userId;
        this.userObject = TwitterUtils.getAccount(getContext(), userId);
    }

    public String getTitle(){
        return this.listType.name() + " - " + this.userObject.getUserScreenName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fragment再生成抑止
        setRetainInstance(true);
    }

    // OnClick Event
}
