package com.nanami.android.blackhistory.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanami.android.blackhistory.event.EventBusHolder;
import com.nanami.android.blackhistory.utils.BHLogger;

import butterknife.ButterKnife;

/**
 * Created by atsumi on 2016/01/20.
 */
abstract public class BaseFragment extends Fragment {
    private LayoutInflater inflater;

    @LayoutRes
    abstract protected int getLayoutID();

    abstract protected void init();

    @Override
    final public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        BHLogger.printlnDetail();
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutID(), container, false);
        this.inflater = inflater;
        ButterKnife.bind(this, rootView);
        init();
        BHLogger.printlnDetail();
        return rootView;
    }

    @Override
    final public void onDestroyView() {
        ButterKnife.unbind(this);
        BHLogger.printlnDetail();
        super.onDestroyView();
    }

    @Override
    final public void onResume() {
        super.onResume();
        EventBusHolder.EVENT_BUS.register(this);
        BHLogger.printlnDetail();
    }

    @Override
    final public void onPause() {
        EventBusHolder.EVENT_BUS.unregister(this);
        BHLogger.printlnDetail();
        super.onPause();
    }

}
