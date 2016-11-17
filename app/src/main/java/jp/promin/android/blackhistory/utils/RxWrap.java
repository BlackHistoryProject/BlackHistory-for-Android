package jp.promin.android.blackhistory.utils;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

/**
 * Created by atsumi on 2016/09/27.
 */

public class RxWrap {
    public static <T> Observable<T> create(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    public static <T> Observable<T> create(Observable<T> observable, ProgressDialog progressDialog) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(progressDialog::show)
                .doOnCompleted(progressDialog::dismiss)
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    public static <T> T head(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return Observable.from(list).toBlocking().first();
    }

    public static <T> List<T> tail(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return Observable.from(list).skip(1).toList().toBlocking().single();
    }

    public static <T> boolean indexOf(List<T> list, @NonNull T target) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        //TODO not better...
        return Observable.from(list)
                .map(t -> t.equals(target))
                .filter(aBoolean -> aBoolean)
                .toBlocking().single();
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

    public static <T> Observable<T> createObservable(UserAction.Callable<T> observable) {
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
