package jp.promin.android.blackhistory.utils.twitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.model.UserToken;
import jp.promin.android.blackhistory.utils.BHLogger;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

final public class TwitterUtils {

    /*
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     *
     * @param context
     * @return
     */
    public static Twitter getTwitterInstance(@NonNull Context context, @Nullable Long userId) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (userId != null && hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context, userId));
        }
        return twitter;
    }

    public static TwitterStream getTwitterStreamInstance(@NonNull Context context, Long userId) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);
            AccessToken accessToken = loadAccessToken(context, userId);
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
    private static AccessToken loadAccessToken(@NonNull Context context, long userId) {
        UserToken tokenObject = getAccount(context, userId);
        if (tokenObject == null) {
            return null;
        }
        return new AccessToken(tokenObject.getToken(), tokenObject.getTokenSecret(), userId);
    }

    /***
     * アクセストークンが存在する場合はtrueを返します。
     * @return false or true
     */
    public static boolean hasAccessToken(@NonNull Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<UserToken> s = realm.where(UserToken.class).findAll();
        return s.size() > 0;
    }

    /* ---  database 操作 --- */
    public static void addAccount(@NonNull Context context, @NonNull final AccessToken accessToken) {
        final UserToken tokenObject = new UserToken();
        tokenObject.setId(accessToken.getUserId());
        tokenObject.setName(accessToken.getScreenName());
        tokenObject.setScreenName(accessToken.getScreenName());
        tokenObject.setToken(accessToken.getToken());
        tokenObject.setTokenSecret(accessToken.getTokenSecret());

        final Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tokenObject);
        realm.commitTransaction();
    }

    private static void deleteAccount(@NonNull Context context, long userId) {
        final Realm realm = Realm.getInstance(context);
        final UserToken result = realm.where(UserToken.class).equalTo("id", userId).findFirst();
        if (result == null) {
            return;
        }
        realm.beginTransaction();
        result.removeFromRealm();
        realm.commitTransaction();
    }

    public static List<UserToken> getAccounts(@NonNull Context context) {
        final Realm realm = Realm.getInstance(context);
        final List<UserToken> results = new ArrayList<>();
        for (UserToken token : realm.where(UserToken.class).findAll()) {
            results.add(token);
        }
        return results;
    }

    @Nullable
    public static UserToken getAccount(@NonNull Context context, long userId) {
        final Realm realm = Realm.getInstance(BlackHistoryController.get(context));
        return realm
                .where(UserToken.class)
                .equalTo("id", userId)
                .findFirst();

    }
}
