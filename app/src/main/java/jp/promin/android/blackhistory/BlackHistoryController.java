package jp.promin.android.blackhistory;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import jp.promin.android.blackhistory.manager.TokenManager;
import jp.promin.android.blackhistory.utils.picture.ImageManager;

public final class BlackHistoryController extends Application {

    private TokenManager mTokenManager;

    @Nullable
    public static BlackHistoryController get(@Nullable Context context) {
        if (context == null) return null;

        if (context instanceof BlackHistoryController) {
            return (BlackHistoryController) context;
        }

        if (context.getApplicationContext() instanceof BlackHistoryController) {
            return (BlackHistoryController) context.getApplicationContext();
        }

        if (context instanceof Activity) {
            if (((Activity) context).getApplication() instanceof BlackHistoryController) {
                return (BlackHistoryController) ((Activity) context).getApplication();
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImageManager.Initialize(getApplicationContext());

        mTokenManager = new TokenManager(this);
    }

    public void postEvent(@NonNull Object event) {
        EventBus.getDefault().post(event);
    }

    public TokenManager getTokenManager() {
        return mTokenManager;
    }
}
