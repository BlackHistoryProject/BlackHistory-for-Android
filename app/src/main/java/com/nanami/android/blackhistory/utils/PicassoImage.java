package com.nanami.android.blackhistory.utils;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by atsumi on 2016/01/10.
 */
public class PicassoImage {
    private static boolean isDebug = false;
    public static synchronized void setDebug(boolean debug){
        isDebug = debug;
    }

    private final static int DISK_CACHE_SIZE_MB = 100;
    private final static int MEMORY_CACHE_SIZE_MB = 5;

    public static Picasso Builder(Context context){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(context.getCacheDir(), DISK_CACHE_SIZE_MB * 1024 * 1024));

        if (isDebug){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(builder.build()))
                .memoryCache(new LruCache(MEMORY_CACHE_SIZE_MB * 1024 * 1024))
                .build();
    }
}
