package jp.promin.android.blackhistory.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.squareup.picasso.Picasso;

public class ImageManager {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static Picasso picasso;

    private ImageManager() {
    }

    static public synchronized void Initialize(Context applicationContext) {
        context = applicationContext;
        picasso = PicassoImage.Builder(applicationContext);
    }

    static public synchronized Context getContext() {
        return context;
    }

    static public synchronized Picasso getPicasso() {
        return picasso;
    }
}
