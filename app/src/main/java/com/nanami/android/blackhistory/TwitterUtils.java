package com.nanami.android.blackhistory;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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

    private static final String NOW_USER_ID = "user_id";
    private static final String PREF_NAME = "twitter_user_id";

    public static String nowLoginUserScreenName = "";

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
        editor.putLong(NOW_USER_ID, accessToken.getUserId());
        editor.apply();
        addAccount(context, accessToken);
    }

    public static void storeUserID(Context context, long userID){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(NOW_USER_ID, userID);
        editor.apply();
    }


    public static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long userID = preferences.getLong(NOW_USER_ID, -1);
        Account account;
        if (userID > 0){
            account = getAccount(context, userID);
        } else {
            Account _account = getAccount(context);
            if(_account.accounts.size() > 0) {
                account = _account.accounts.get(0);
            }else{
                return null;
            }
        }
        if(account == null) return null;
        nowLoginUserScreenName = account.screenName;
        return account.getAccessToken();
    }

    /***
     * アクセストークンが存在する場合はtrueを返します。
     * @return false or true
     */
    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }

    /* ---  database 操作 --- */

    private static void addAccount(Context context, AccessToken accessToken){
        SQLiteManager manager = new SQLiteManager(context);
        SQLiteDatabase database = manager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", accessToken.getUserId());
        values.put("screenName", accessToken.getScreenName());
        values.put("token", accessToken.getToken());
        values.put("tokenSecret", accessToken.getTokenSecret());
        database.insert("account", null, values);

        database.close();
    }

    private static void deleteAccount(Context context, long userId){
        SQLiteManager manager = new SQLiteManager(context);
        SQLiteDatabase database = manager.getWritableDatabase();
        database.delete("account", "userId = " + String.valueOf(userId), null);
        database.close();
    }

    static void deleteAllAccount(Context context){
        SQLiteManager manager = new SQLiteManager(context);
        SQLiteDatabase database = manager.getWritableDatabase();
        database.rawQuery("Delete from account", new String[]{});
        database.close();
    }

    public static Account getAccount(Context context) {
            SQLiteManager manager = new SQLiteManager(context);
            SQLiteDatabase database = manager.getReadableDatabase();
            Account account = new Account(database.query("account", new String[]{"userName", "userId", "screenName", "token", "tokenSecret"}, null, null, null, null, null));
            database.close();
            return account;
    }

    private static Account getAccount(Context context, long userId) {
        try {
            SQLiteManager manager = new SQLiteManager(context);
            SQLiteDatabase database = manager.getReadableDatabase();
            Account account = new Account(database.query("account", new String[]{"userName", "userId", "screenName", "token", "tokenSecret"}, "userId = " + String.valueOf(userId), null, null, null, null));
            database.close();
            return account.accounts.get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static class Account{
        public String userName;
        public long userId;
        public String screenName;
        public String token;
        public String tokenSecret;

        public ArrayList<Account> accounts = new ArrayList<Account>();

        public Account(String userName, Long userId, String screenName, String token, String tokenSecret){
            this.userName = userName;
            this.userId = userId;
            this.screenName = screenName;
            this.token = token;
            this.tokenSecret = tokenSecret;
        }

        public Account(Cursor cursor){
            cursor.moveToFirst();
            int count = cursor.getCount();

            for(int i = 0 ; i < count ; i++){
                this.accounts.add(
                        new Account(cursor.getString(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4))
                );
                if(count -1 == i) break;
                cursor.moveToNext();
            }
        }
        public AccessToken getAccessToken(){
            if(isSet()) return new AccessToken(this.token, this.tokenSecret);
            return null;
        }
        public boolean isSet(){
            return token != null && tokenSecret != null;
        }
    }

    static final String dbName = "twitter_black_history.db";
    static final int dbVersion = 1;
    static final String CREATE_TABLE = "create table account ( id integer primary key autoincrement, userName text, userId long not null unique, screenName text not null, token text not null, tokenSecret text not null);";
    static final String DROP_TABLE = "drop table account;";
    /***
     * Create table account
     *  id integer
     *  userName text
     *  userId integer
     *  screenName text
     *  token text
     *  tokenSecret text
     */
    private static class SQLiteManager extends SQLiteOpenHelper{
        public SQLiteManager(Context c){
            super(c, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL(DROP_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
}
