package com.nanami.android.blackhistory.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ImageButton;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by atsumi on 2016/01/19.
 */
public class UserAction {
    public static void favorite(@NonNull final Activity context,@NonNull  final ImageButton favButton,@NonNull  final Long myUserId,@NonNull  final Status targetStatus, final Callback callback) {
        if (myUserId.equals(targetStatus.getUser().getId())) {
            BHLogger.toast("自分のツイートにはいいねできません");
            return;
        }
        final Twitter twitter = TwitterUtils.getTwitterInstance(context, myUserId);
        final Boolean favorited = targetStatus.isFavorited();
        action(context, new Task() {
            @Override
            public Status call() throws TwitterException {
                if (favorited) {
                    return twitter.destroyFavorite(targetStatus.getId());
                } else {
                    return twitter.createFavorite(targetStatus.getId());
                }
            }

            @Override
            public void response(final Status result) {
                if (result == null) {
                    BHLogger.toast("いいねに失敗しました");
                    return;
                }
                context.runOnUiThread(() -> {
                    /**
                     * true : いいね解除
                     */
                    if (favorited) {
                        favButton.setImageResource(android.R.drawable.star_off);
                    } else {
                        favButton.setImageResource(android.R.drawable.star_on);
                    }

                    callback.finish(result);
                });
            }
        });
    }
    public static void retweet(@NonNull final Activity context,@NonNull  final ImageButton rtButton,@NonNull final Long myUserId,@NonNull  final Status targetStatus, final Callback callback){
        if (myUserId.equals(targetStatus.getUser().getId())){
            BHLogger.toast("自分のツイートはリツイートできません");
            return;
        }
        final Twitter twitter = TwitterUtils.getTwitterInstance(context, myUserId);
        final Boolean retweeted = targetStatus.isRetweetedByMe();
        action(context, new Task() {
            @Override
            public Status call() throws TwitterException {
                return twitter.retweetStatus(targetStatus.getId());
            }

            @Override
            public void response(final Status result) {
                if (result == null){
                    BHLogger.toast("リツイートに失敗しました");
                    return;
                }
                context.runOnUiThread(() -> {
                    /**
                     * true : RT解除
                     */
                    if (retweeted) {
                        rtButton.setImageResource(android.R.drawable.checkbox_off_background);
                    } else {
                        rtButton.setImageResource(android.R.drawable.checkbox_on_background);
                    }
                    callback.finish(result);
                });
            }
        });
    }

    private static void action(final Context context, final Task task){
        AsyncTask<Void, Void, Status> _task = new AsyncTask<Void, Void, Status>() {
            @Override
            protected twitter4j.Status doInBackground(Void... params) {
                try {
                    return task.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(twitter4j.Status result) {
                task.response(result);
            }
        };
        _task.execute();
    }

    private interface Task {
        Status call() throws TwitterException;
        void response(Status result);
    }

    public interface Callback{
        void finish(Status status);
    }
}
