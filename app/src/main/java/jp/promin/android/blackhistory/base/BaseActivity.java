package jp.promin.android.blackhistory.base;import android.content.res.Configuration;import android.os.Bundle;import android.support.annotation.Nullable;import android.support.v7.app.AppCompatActivity;import android.view.Window;import butterknife.ButterKnife;import jp.promin.android.blackhistory.utils.BHLogger;abstract public class BaseActivity extends AppCompatActivity {    @Override    protected void onCreate(@Nullable Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);        BHLogger.printlnDetail();    }    @Override    public void setContentView(int layoutResID) {        super.setContentView(layoutResID);        ButterKnife.bind(this);        BHLogger.printlnDetail();    }    @Override    protected void onResume() {        super.onResume();        BHLogger.printlnDetail();    }    @Override    public void onConfigurationChanged(Configuration newConfig) {        super.onConfigurationChanged(newConfig);        BHLogger.printlnDetail();    }    @Override    protected void onStart() {        super.onStart();        BHLogger.printlnDetail();    }    @Override    protected void onStop() {        super.onStop();        BHLogger.printlnDetail();    }}