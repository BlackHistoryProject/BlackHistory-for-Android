package jp.promin.android.blackhistory.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.promin.android.blackhistory.model.UserToken;

public final class TokenManager {
    private static final String KEY_TOKEN = "user_token";

    private final Context mContext;

    public TokenManager(@NonNull Context context) {
        mContext = context;
    }

    private String getUserKey(long userId) {
        return KEY_TOKEN + "_" + userId;
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences("Token", Context.MODE_PRIVATE);
    }

    public List<UserToken> getAllToken() {
        List<UserToken> list = new ArrayList<>();
        for (Map.Entry<String, ?> entry : getPreferences().getAll().entrySet()) {
            try {
                Object obj = entry.getValue();
                if (obj instanceof String) {
                    final String json = (String) obj;
                    list.add(new Gson().fromJson(json, UserToken.class));
                }
            } catch (Exception ignore) {
            }
        }
        return list;
    }

    @Nullable
    public UserToken getToken(long userId) {
        String userKey = getPreferences().getString(getUserKey(userId), "");
        if (TextUtils.isEmpty(userKey)) {
            return null;
        }
        return new Gson().fromJson(userKey, UserToken.class);
    }

    public void saveToken(UserToken token) {
        String userKey = new Gson().toJson(token);
        getPreferences().edit()
                .putString(getUserKey(token.getId()), userKey)
                .apply();
    }

    public void deleteToken(long userId) {
        final String userKey = getUserKey(userId);
        if (!getPreferences().contains(userKey)) return;

        getPreferences().edit()
                .remove(userKey)
                .apply();
    }

    public boolean hasToken() {
        return getPreferences().getAll().size() > 0;
    }
}
