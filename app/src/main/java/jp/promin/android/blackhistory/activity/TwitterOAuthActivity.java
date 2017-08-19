package jp.promin.android.blackhistory.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.base.BaseActivity;
import jp.promin.android.blackhistory.utils.RxWrap;
import jp.promin.android.blackhistory.utils.ShowToast;
import jp.promin.android.blackhistory.utils.TwitterUtils;
import jp.promin.android.blackhistory.utils.UserAction;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends BaseActivity {
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

        RxWrap
                .create(UserAction.createObservable(() -> {
                    mTwitter.setOAuthAccessToken(null);
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallBackURL);
                    return mRequestToken.getAuthorizationURL();
                }))
                .subscribe(uri -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri))), Throwable::printStackTrace);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallBackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");
        RxWrap.create(UserAction.createObservable(() -> mTwitter.getOAuthAccessToken(mRequestToken, verifier)))
                .subscribe(this::successOAuth, throwable -> {
                    ShowToast.showToast(throwable.getLocalizedMessage());
                });
    }

    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.addAccount(this, accessToken);
        MainStreamActivity.startActivity(this, accessToken.getUserId());
        finish();
    }
}
