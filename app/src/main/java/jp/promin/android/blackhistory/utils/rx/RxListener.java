package jp.promin.android.blackhistory.utils.rx;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;

public abstract class RxListener<T> {
    public abstract T result() throws Throwable;

    public final Observable<T> toObservable() {
        return observableImpl(null);
    }

    public final Observable<T> toObservable(@NonNull ProgressDialog dialog) {
        return observableImpl(dialog);
    }

    private Observable<T> observableImpl(@Nullable ProgressDialog dialog) {
        final Observable<T> observable = Observable.create(emitter -> {
            if (Thread.currentThread().getName().equals("main")) {
                throw new RuntimeException("メインスレッドで動いてる! 直し忘れ。");
            }
            try {
                emitter.onNext(result());
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
        if (dialog != null) {
            observable
                    .doOnSubscribe(disposable -> dialog.show())
                    .doOnComplete(dialog::dismiss);
        }
        return observable;
    }
}
