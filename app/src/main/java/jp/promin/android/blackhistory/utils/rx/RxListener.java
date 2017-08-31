package jp.promin.android.blackhistory.utils.rx;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public abstract class RxListener<T> {
    public abstract T result() throws Throwable;

    public final Observable<T> toObservable() {
        return observableImpl(null);
    }

    public final Observable<T> toObservable(@NonNull ProgressDialog dialog) {
        return observableImpl(dialog);
    }

    private Observable<T> observableImpl(@Nullable final ProgressDialog dialog) {
        final Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(result());
                    emitter.onComplete();
                } catch (Throwable e) {
                    emitter.onError(e);
                }
            }
        });
        if (dialog != null) {
            observable
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            dialog.show();
                        }
                    })
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            dialog.dismiss();
                        }
                    });
        }
        return observable;
    }
}
