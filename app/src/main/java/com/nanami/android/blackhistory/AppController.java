package com.nanami.android.blackhistory;import android.app.Application;/** * Created by TEJNEK on 2016/01/13. */public class AppController extends Application {    private static AppController mInstance;    @Override    public void onCreate() {        super.onCreate();        mInstance = this;    }    public static synchronized AppController get(){        return mInstance;    }}