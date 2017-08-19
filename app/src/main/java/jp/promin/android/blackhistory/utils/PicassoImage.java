package jp.promin.android.blackhistory.utils;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;


public class PicassoImage {
    private final static int DISK_CACHE_SIZE_MB = 100;
    private final static int MEMORY_CACHE_SIZE_MB = 5;

    public static Picasso Builder(Context context){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(context.getCacheDir(), DISK_CACHE_SIZE_MB * 1024 * 1024));

        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(builder.build()))
                .memoryCache(new LruCache(MEMORY_CACHE_SIZE_MB * 1024 * 1024))
                .build();
    }
}
