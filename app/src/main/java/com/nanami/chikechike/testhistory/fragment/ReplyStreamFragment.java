package com.nanami.chikechike.testhistory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanami.chikechike.myapplication.R;
import com.nanami.chikechike.testhistory.fragment.CommonStreamFragment;

/**
 * Created by nanami on 2014/09/05.
 */
public class ReplyStreamFragment extends CommonStreamFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        this.setTitle("Reply");
        return inflater.inflate(R.layout.fragment_common_list, null);
    }

}
