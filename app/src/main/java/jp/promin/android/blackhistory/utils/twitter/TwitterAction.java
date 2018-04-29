package jp.promin.android.blackhistory.utils.twitter;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageButton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.event.FavoriteFailureEvent;
import jp.promin.android.blackhistory.event.FavoriteSuccessEvent;
import jp.promin.android.blackhistory.event.ReTweetFailureEvent;
import jp.promin.android.blackhistory.event.ReTweetSuccessEvent;
import jp.promin.android.blackhistory.event.TweetFailureEvent;
import jp.promin.android.blackhistory.event.TweetSuccessEvent;
import jp.promin.android.blackhistory.model.UserToken;
import jp.promin.android.blackhistory.utils.BlackUtil;
import jp.promin.android.blackhistory.utils.rx.RxListener;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

@SuppressWarnings({"Anonymous2MethodRef", "Convert2Lambda"})
public class TwitterAction {
    public static void favorite(@NonNull final Context context,
                                @NonNull final ImageButton favButton,
                                final long myId,
                                @NonNull final Status targetStatus) {
        BlackHistoryController app = BlackHistoryController.get(context);
        if (app == null) return;

        UserToken token = app.getTokenManager().getToken(myId);
        if (token == null) return;

        if (targetStatus.getUser().getId() == myId) {
            return;
        }
        final Boolean favorited = targetStatus.isFavorited();

        final Twitter twitter = TwitterUtils.getTwitterInstance(context, token);

        new RxListener<Status>() {
            @Override
            public Status result() throws Throwable {
                if (favorited) {
                    return twitter.destroyFavorite(targetStatus.getId());
                } else {
                    return twitter.createFavorite(targetStatus.getId());
                }
            }
        }.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        if (favorited) {
                            favButton.setImageResource(R.drawable.ic_favorite_off);
                        } else {
                            favButton.setImageResource(R.drawable.ic_favorite_on);
                        }
                        BlackHistoryController.get(context).postEvent(new FavoriteSuccessEvent(status));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        BlackHistoryController.get(context).postEvent(new FavoriteFailureEvent(throwable));
                    }
                });
    }

    public static void reTweet(@NonNull final Context context,
                               @NonNull final ImageButton rtButton,
                               @NonNull final long userId,
                               @NonNull final Status targetStatus) {
        BlackHistoryController app = BlackHistoryController.get(context);
        if (app == null) return;

        UserToken token = app.getTokenManager().getToken(userId);
        if (token == null) return;

        if (userId == targetStatus.getUser().getId()) {
            return;
        }

        final Twitter twitter = TwitterUtils.getTwitterInstance(context, token);
        new RxListener<Status>() {
            @Override
            public Status result() throws Throwable {
                return twitter.retweetStatus(targetStatus.getId());
            }
        }.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        if (targetStatus.isRetweetedByMe()) {
                            rtButton.setImageResource(R.drawable.ic_retweet_off);
                        } else {
                            rtButton.setImageResource(R.drawable.ic_retweet_on);
                        }
                        BlackHistoryController.get(context).postEvent(new ReTweetSuccessEvent(status));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        BlackHistoryController.get(context).postEvent(new ReTweetFailureEvent(throwable));
                    }
                });
    }

    public static void tweet(@NonNull Context context, @NonNull String tweetText, long userId) {
        tweetImpl(context, tweetText, userId, null, new long[]{});
    }

    public static void tweetWithMedia(@NonNull Context context, @NonNull String tweetText, long userId, long[] mediaIds) {
        tweetImpl(context, tweetText, userId, null, mediaIds);
    }

    public static void reply(@NonNull Context context, @NonNull String tweetText, long userId, @NonNull Status replyTarget) {
        tweetImpl(context, tweetText, userId, replyTarget, new long[]{});
    }

    public static void replyWithMedia(@NonNull Context context, @NonNull String tweetText, long userId,
                                      @NonNull Status replyTarget, long[] mediaIds) {
        tweetImpl(context, tweetText, userId, replyTarget, mediaIds);
    }

    /**
     * Tweet 処理
     *
     * @param context     context
     * @param tweetText   ツイート内容
     * @param userId      ツイートをするユーザーのId
     * @param replyTarget リプライするツイート
     * @param mediaIds    アップロードする写真
     */
    private static void tweetImpl(@NonNull final Context context,
                                  @NonNull String tweetText,
                                  long userId,
                                  @Nullable Status replyTarget,
                                  long[] mediaIds) {
        BlackHistoryController app = BlackHistoryController.get(context);
        if (app == null) return;

        UserToken token = app.getTokenManager().getToken(userId);
        if (token == null) return;

        final StatusUpdate statusUpdate = new StatusUpdate(tweetText);

        // reply
        if (replyTarget != null) {
            statusUpdate.setInReplyToStatusId(replyTarget.getId());
        }

        // upload media
        if (mediaIds.length > 0) {
            statusUpdate.setMediaIds(mediaIds);
        }

        // dialog
        final ProgressDialog dialog = new ProgressDialog(context) {{
            setProgressStyle(ProgressDialog.STYLE_SPINNER);
            setMessage(context.getString(R.string.status_uploading_image));
            setCancelable(false);
        }};

        final Twitter twitter = TwitterUtils.getTwitterInstance(context, token);

        new RxListener<Status>() {
            @Override
            public Status result() throws Throwable {
                return twitter.updateStatus(statusUpdate);
            }
        }.toObservable(dialog)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        BlackUtil.showNotification(context, R.drawable.ic_notification, "送信中", "送信しています...", 1);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(1);
                    }
                })
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        BlackHistoryController.get(context).postEvent(new TweetSuccessEvent(status.getId()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        BlackUtil.showNotification(context, R.drawable.ic_notification_error, "失敗", "ツイートの送信に失敗しました。", 1);
                        BlackHistoryController.get(context).postEvent(new TweetFailureEvent(throwable));
                    }
                });
    }
}
