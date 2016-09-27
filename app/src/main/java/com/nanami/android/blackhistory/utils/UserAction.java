package com.nanami.android.blackhistory.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageButton;

import com.nanami.android.blackhistory.R;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

/**
 * Created by atsumi on 2016/01/19.
 */
public class UserAction {
    public static void favorite(@NonNull final Context context,
                                @NonNull final ImageButton favButton,
                                @NonNull final Long myUserId,
                                @NonNull final Status targetStatus,
                                final Callback callback) {
        if (myUserId.equals(targetStatus.getUser().getId())) {
            BHLogger.toast("自分のツイートにはいいねできません");
            return;
        }
        final Boolean favorited = targetStatus.isFavorited();

        RxWrap.create(createObservable(() -> {
            final Twitter twitter = TwitterUtils.getTwitterInstance(context, myUserId);
            if (favorited) {
                return twitter.destroyFavorite(targetStatus.getId());
            } else {
                return twitter.createFavorite(targetStatus.getId());
            }
        })).subscribe(status -> {
            if (favorited) {
                favButton.setImageResource(android.R.drawable.star_off);
            } else {
                favButton.setImageResource(android.R.drawable.star_on);
            }
            callback.result(status);
        }, Throwable::printStackTrace);
    }

    public static void retweet(@NonNull final Context context,
                               @NonNull final ImageButton rtButton,
                               @NonNull final Long myUserId,
                               @NonNull final Status targetStatus,
                               @NonNull final Callback callback) {
        if (myUserId.equals(targetStatus.getUser().getId())) {
            BHLogger.toast("自分のツイートはリツイートできません");
            return;
        }

        RxWrap.create(createObservable(() -> {
            final Twitter twitter = TwitterUtils.getTwitterInstance(context, myUserId);
            return twitter.retweetStatus(targetStatus.getId());
        })).doOnError(callback::error)
                .subscribe(status -> {
                    if (targetStatus.isRetweetedByMe()) {
                        rtButton.setImageResource(android.R.drawable.checkbox_off_background);
                    } else {
                        rtButton.setImageResource(android.R.drawable.checkbox_on_background);
                    }
                }, Throwable::printStackTrace);
    }


    public static void tweet(@NonNull Context context, @NonNull String tweetText, long userId, Callback finishCallback) {
        tweet(context, tweetText, userId, null, null, finishCallback);
    }

    /**
     * ツイートをします
     */
    public static void tweet(@NonNull Context context,
                             @NonNull String tweetText,
                             long userId,
                             Status replyTarget,
                             UploadMedia mediaCallback,
                             @NonNull Callback finishCallback) {
        /**
         * ツイートをしている旨を通知
         */
        RxWrap.create(createObservable(() -> {
            Twitter twitter = TwitterUtils.getTwitterInstance(context, userId);

            /**
             * テキストビューの中身をセット (params に入ってる)
             */
            final StatusUpdate statusUpdate = new StatusUpdate(tweetText);

            /**
             * 画像もアップロードするか
             */
            if (mediaCallback != null) {
                List<Long> temp = Observable.from(mediaCallback.medias())
                        .filter(uploadedMedia -> uploadedMedia != null)
                        .map(UploadedMedia::getMediaId)
                        .toList().toBlocking().single();

                long[] mediaIds = new long[temp.size()];
                for (int i = 0; i < temp.size(); i++) {
                    mediaIds[i] = temp.get(i);
                }
                if (mediaIds.length > 0) {
                    statusUpdate.setMediaIds(mediaIds);
                }
            }
            if (replyTarget != null) {
                statusUpdate.setInReplyToStatusId(replyTarget.getId());
            }
            return twitter.updateStatus(statusUpdate);
        }), BlackUtil.createProgressDialog(context, R.string.status_uploading_image, false))
                .doOnSubscribe(() -> BlackUtil.showNotification(context, R.drawable.ic_notification, "送信中", "送信しています...", 1))
                .doOnCompleted(() -> {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                })
                .doOnError(throwable -> {
                    BlackUtil.showNotification(context, R.drawable.ic_notification_error, "失敗", "ツイートの送信に失敗しました。", 1);
                    finishCallback.error(throwable);
                })
                .subscribe(finishCallback::result, Throwable::printStackTrace);
    }

    public interface UploadMedia {
        List<UploadedMedia> medias();
    }

    public interface Callback {
        void result(Status status);

        void error(Throwable error);
    }

    public interface Callable<T> {
        T call() throws TwitterException;
    }

    public static <T> Observable<T> createObservable(Callable<T> observable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(observable.call());
                    subscriber.onCompleted();
                } catch (TwitterException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
