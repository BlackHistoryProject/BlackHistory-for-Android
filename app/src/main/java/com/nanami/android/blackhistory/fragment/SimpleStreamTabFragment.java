package com.nanami.android.blackhistory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.adapter.TweetAdapter;

/**
 * Created by Telneko on 2015/01/17.
 */
public class SimpleStreamTabFragment extends CommonStreamFragment {
    public static final String EXTRA_INNER_FUNCTION = "inner_function";

    public static SimpleStreamTabFragment newInstance(String title){
        SimpleStreamTabFragment fragment = new SimpleStreamTabFragment();
        fragment.setTitle(title);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        mAdapter = new TweetAdapter(getActivity());
        setListAdapter(mAdapter);
        return inflater.inflate(R.layout.fragment_common_list, null);
    }
}
