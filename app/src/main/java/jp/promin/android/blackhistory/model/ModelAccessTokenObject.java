package jp.promin.android.blackhistory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@SuppressWarnings("unchecked")
public class ModelAccessTokenObject extends RealmObject {

    @PrimaryKey
    private Long userId;

    private String userName;
    private String userScreenName;
    private String userToken;
    private String userTokenSecret;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserTokenSecret() {
        return userTokenSecret;
    }

    public void setUserTokenSecret(String userTokenSecret) {
        this.userTokenSecret = userTokenSecret;
    }
}
