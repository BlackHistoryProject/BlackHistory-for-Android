package com.nanami.android.blackhistory.utils;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by atsumi on 2016/01/10.
 */
public class PicassoImage {
    public static Picasso Builder(Context context){
        OkHttpClient okHttpClient = new OkHttpClient();
        return new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
    }
}
