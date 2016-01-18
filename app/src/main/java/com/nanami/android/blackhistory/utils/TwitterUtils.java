package com.nanami.android.blackhistory.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nanami.android.blackhistory.AppController;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.model.ModelAccessTokenObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by nanami on 2014/09/03.
 */
final public class TwitterUtils {
    /*
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     *
     * @param context
     * @return
     */

    public static Twitter getTwitterInstance(Context context,@Nullable Long userId){
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if ( userId != null && hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(userId));
        }
        return twitter;
    }

    public static TwitterStream getTwitterStreamInstance(Context context, Long userId){
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);
                AccessToken accessToken = loadAccessToken(userId);
                if (accessToken != null) {
                    builder.setOAuthAccessToken(accessToken.getToken());
                    builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
                    BHLogger.println(accessToken.getToken() + " " + accessToken.getTokenSecret());
                } else {
                    BHLogger.println("Access token is null");
                    deleteAccount(context, userId);
                    return null;
                }
        }
        twitter4j.conf.Configuration configuration = builder.build();
        return new TwitterStreamFactory(configuration).getInstance();
    }

    @Nullable
    public static AccessToken loadAccessToken(long userId) {
        ModelAccessTokenObject tokenObject = getAccount(userId);
        if (tokenObject == null){
            return null;
        }
        return new AccessToken(tokenObject.getUserToken(), tokenObject.getUserTokenSecret(), tokenObject.getUserId());
    }

    /***
     * アクセストークンが存在する場合はtrueを返します。
     * @return false or true
     */
    public static boolean hasAccessToken(Context context) {
        return Realm.getInstance(context).where(ModelAccessTokenObject.class).count() > 0;
    }

    /* ---  database 操作 --- */

    public static void addAccount(Context context, AccessToken accessToken){
        ModelAccessTokenObject tokenObject = new ModelAccessTokenObject();
        tokenObject.setUserId(accessToken.getUserId());
        tokenObject.setUserName(accessToken.getScreenName());
        tokenObject.setUserScreenName(accessToken.getScreenName());
        tokenObject.setUserToken(accessToken.getToken());
        tokenObject.setUserTokenSecret(accessToken.getTokenSecret());

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tokenObject);
        realm.commitTransaction();
    }

    public static void deleteAccount(Context context, Long userId){
        BHLogger.println("あかうんとけしたぞいｗｗｗ");
        Realm realm = Realm.getInstance(context);
        ModelAccessTokenObject result = realm.where(ModelAccessTokenObject.class).equalTo("userId", userId).findFirst();
        if (result == null){
            return;
        }
        realm.beginTransaction();
        result.removeFromRealm();
        realm.commitTransaction();
    }

    public static void deleteAllAccount(Context context){
        BHLogger.println("あかうんとぜんぶけすぞいｗｗｗ");
        Realm realm = Realm.getInstance(context);
        RealmResults<ModelAccessTokenObject> result = realm.where(ModelAccessTokenObject.class).findAll();
        realm.beginTransaction();
        result.clear();
        realm.commitTransaction();
    }

    public static ArrayList<Long> getAccountIds(Context context) {
        Realm realm = Realm.getInstance(context);
        ArrayList<Long> results = new ArrayList<>();
        for (ModelAccessTokenObject token : realm.where(ModelAccessTokenObject.class).findAll()){
            results.add(token.getUserId());
        }
        return results;
    }

    public static ArrayList<ModelAccessTokenObject> getAccounts(Context context) {
        Realm realm = Realm.getInstance(context);
        ArrayList<ModelAccessTokenObject> results = new ArrayList<>();
        for (ModelAccessTokenObject token : realm.where(ModelAccessTokenObject.class).findAll()){
            results.add(token);
        }
        return results;
    }

    @Nullable
    public static ModelAccessTokenObject getAccount(Long userId) {

        Realm realm = Realm.getInstance(AppController.get().getApplicationContext());
        return realm
                .where(ModelAccessTokenObject.class)
                .equalTo("userId", userId)
                .findFirst();
    }
}
