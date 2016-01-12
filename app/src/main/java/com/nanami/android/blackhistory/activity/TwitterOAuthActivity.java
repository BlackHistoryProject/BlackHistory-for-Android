package com.nanami.android.blackhistory.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.utils.TwitterUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by nanami on 2014/09/03.
 */
public class TwitterOAuthActivity extends CommonActivityAbstract{
    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallBackURL)){
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");
        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return  mTwitter.getOAuthAccessToken(mRequestToken,params[0]);
                } catch (TwitterException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null){
                    // 認証成功！
                    showToast("認証成功！");
                    successOAuth(accessToken);
                } else {
                    // 認証失敗
                    showToast("認証失敗！");
                }
            }
        };
        task.execute(verifier);

    }

    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.addAccount(this, accessToken);
        Intent intent = new Intent(this, MainStreamActivity.class);
        startActivity(intent);
        finish();
    }
}
