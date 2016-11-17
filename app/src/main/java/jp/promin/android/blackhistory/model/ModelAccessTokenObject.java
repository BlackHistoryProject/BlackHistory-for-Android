package jp.promin.android.blackhistory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by atsumi on 2016/01/10.
 */
@SuppressWarnings("unchecked")
public class ModelAccessTokenObject extends RealmObject {

    @PrimaryKey
    private Long userId;

    private String userName;
    private String userScreenName;
    private String userToken;
    private String userTokenSecret;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setUserTokenSecret(String userTokenSecret) {
        this.userTokenSecret = userTokenSecret;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserTokenSecret() {
        return userTokenSecret;
    }
}
