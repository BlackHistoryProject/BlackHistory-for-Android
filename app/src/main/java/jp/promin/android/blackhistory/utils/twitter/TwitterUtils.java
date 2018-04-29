package jp.promin.android.blackhistory.utils.twitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.model.UserToken;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public final class TwitterUtils {
    @Nullable
    public static Twitter getTwitterInstance(@NonNull Context context, @Nullable UserToken userToken) {
        if (userToken == null) return null;

        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken accessToken = new AccessToken(userToken.getToken(), userToken.getTokenSecret(), userToken.getId());
        twitter.setOAuthAccessToken(accessToken);
        return twitter;
    }

    @Nullable
    public static TwitterStream getTwitterStreamInstance(@NonNull Context context, @Nullable UserToken userToken) {
        if (userToken == null) return null;

        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);
            AccessToken accessToken = new AccessToken(userToken.getToken(), userToken.getTokenSecret(), userToken.getId());
            builder.setOAuthAccessToken(accessToken.getToken());
            builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
        }
        return new TwitterStreamFactory(builder.build()).getInstance();
    }
}
