package jp.promin.android.blackhistory.ui.twitter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.model.UserToken;
import jp.promin.android.blackhistory.ui.common.BaseActivity;
import jp.promin.android.blackhistory.ui.mainstream.MainStreamActivity;
import jp.promin.android.blackhistory.utils.rx.RxListener;
import jp.promin.android.blackhistory.utils.twitter.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public final class TwitterOAuthActivity extends BaseActivity {
    private String mCallBackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAuthorize();
    }

    /*
     * OAuth認証（厳密には認可）を開始します。
     *
     * @param listener
     */
    private void startAuthorize() {

        mCallBackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtils.getTwitterInstance(this, null);

        new RxListener<String>() {
            @Override
            public String result() throws Throwable {
                mTwitter.setOAuthAccessToken(null);
                mRequestToken = mTwitter.getOAuthRequestToken(mCallBackURL);
                return mRequestToken.getAuthorizationURL();
            }
        }
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s)));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null || !intent.getData().toString().startsWith(mCallBackURL)) {
            throw new RuntimeException("変なIntentですね。");
        }
        final String verifier = intent.getData().getQueryParameter("oauth_verifier");
        new RxListener<AccessToken>() {
            @Override
            public AccessToken result() throws Throwable {
                return mTwitter.getOAuthAccessToken(mRequestToken, verifier);
            }
        }.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<AccessToken>() {
                    @Override
                    public void accept(AccessToken accessToken) throws Exception {
                        successOAuth(accessToken);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void successOAuth(@NonNull AccessToken accessToken) {
        BlackHistoryController app = BlackHistoryController.get(this);
        if (app == null) return;

        UserToken token = new UserToken();
        token.setId(accessToken.getUserId());
        token.setScreenName(accessToken.getScreenName());
        token.setToken(accessToken.getToken());
        token.setTokenSecret(accessToken.getTokenSecret());

        app.getTokenManager().saveToken(token);
        MainStreamActivity.startActivity(this, token.getId());
        finish();
    }
}
