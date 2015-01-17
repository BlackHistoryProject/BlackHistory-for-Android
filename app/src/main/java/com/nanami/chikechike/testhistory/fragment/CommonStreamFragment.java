package com.nanami.chikechike.testhistory.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanami.chikechike.testhistory.TwitterUtils;
import com.nanami.chikechike.testhistory.adapter.TweetAdapter;

import twitter4j.Twitter;

/**
 * Created by Telneko on 2015/01/17.
 */
public class CommonStreamFragment extends ListFragment {
    String title = "";
    public TweetAdapter mAdapter;
    public void setTitle(String title){
        this.title = title + " - " + TwitterUtils.nowLoginUserScreenName;
    }
    public String getTitle(){
        return this.title;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fragment再生成抑止
        setRetainInstance(true);
    }
}
