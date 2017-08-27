package jp.promin.android.blackhistory;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import jp.promin.android.blackhistory.model.OrmaDatabase;
import jp.promin.android.blackhistory.utils.picture.ImageManager;

public class BlackHistoryController extends Application {
    private static BlackHistoryController mInstance;

    public static synchronized BlackHistoryController get() {
        return mInstance;
    }

    @NonNull
    public static BlackHistoryController get(@NonNull Context context) {
        if (context instanceof BlackHistoryController) {
            return (BlackHistoryController) context;
        } else if (context.getApplicationContext() instanceof BlackHistoryController) {
            return (BlackHistoryController) context.getApplicationContext();
        }
        throw new RuntimeException("Context is not BlackHistoryController");
    }

    private OrmaDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ImageManager.Initialize(getApplicationContext());
        mDatabase = new OrmaDatabase.Builder(this).build();
    }

    public void postEvent(@NonNull Object event) {
        EventBus.getDefault().post(event);
    }

    public OrmaDatabase getDatabase() {
        return mDatabase;
    }
}
