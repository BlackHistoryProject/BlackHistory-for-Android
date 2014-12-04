package com.nanami.chikechike.testhistory;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanami.chikechike.myapplication.R;

/**
 * Created by nanami on 2014/09/05.
 */
public class ReplyStreamfragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.replyfragment, null);
    }

}
