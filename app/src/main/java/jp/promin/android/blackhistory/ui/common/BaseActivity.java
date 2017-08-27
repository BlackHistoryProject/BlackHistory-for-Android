package jp.promin.android.blackhistory.ui.common;

import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import jp.promin.android.blackhistory.utils.BHLogger;

public abstract class BaseActivity extends AppCompatActivity {

    protected boolean shouldUseEventBus() {
        return false;
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.bind(this);
        BHLogger.printlnDetail();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (shouldUseEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shouldUseEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }
}
