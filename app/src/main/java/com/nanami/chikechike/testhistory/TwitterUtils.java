package com.nanami.chikechike.testhistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.mtp.MtpStorageInfo;

import com.nanami.chikechike.myapplication.R;

import javax.crypto.SecretKey;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by nanami on 2014/09/03.
 */
public class TwitterUtils {

    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "token_secret";
    private static final String PREF_NAME = "twitter_access_token";

    /*
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     *
     * @param context
     * @return
     */

    public static Twitter getTwitterInstance(Context context){
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        return twitter;
    }

    public static TwitterStream getTwitterStreamInstance(Context context){
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);
                AccessToken accessToken = loadAccessToken(context);
                if (accessToken != null) {
                    builder.setOAuthAccessToken(accessToken.getToken());
                    builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
                }
        }
        twitter4j.conf.Configuration configuration = builder.build();
        return new TwitterStreamFactory(configuration).getInstance();
    }
     /*
     * アクセストークンをプリファレンスから読み込みます。
     *
     * @param context
     * @return
     */


    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, accessToken.getToken() );
        editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }


    public static  AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN, null);
        String tokenSecret = preferences.getString(TOKEN_SECRET, null);
        if (token != null && tokenSecret!= null){
            return new AccessToken(token, tokenSecret);
        } else {
            return  null;
        }
    }

    /*
        * アクセストークンが存在する場合はtrueを返します。
        *
        * @return
        */

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }
}
