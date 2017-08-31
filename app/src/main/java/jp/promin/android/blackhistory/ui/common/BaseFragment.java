package jp.promin.android.blackhistory.ui.common;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import jp.promin.android.blackhistory.utils.BHLogger;

abstract public class BaseFragment extends Fragment {
    protected boolean shouldUseEventBus() {
        return false;
    }

    @LayoutRes
    abstract protected int getLayoutId();

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
        View rootView = inflater.inflate(getLayoutId(), container, false);
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
    public void onStart() {
        super.onStart();
        if (shouldUseEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (shouldUseEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }
}
