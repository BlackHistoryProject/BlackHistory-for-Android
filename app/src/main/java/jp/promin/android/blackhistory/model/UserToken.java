package jp.promin.android.blackhistory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserToken extends RealmObject {
    @PrimaryKey
    private long id;
    private String name;
    private String screenName;
    private String token;
    private String tokenSecret;

    public UserToken() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
}
