package com.nanami.chikechike.testhistory;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.nanami.chikechike.myapplication.R;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by nanami on 2014/09/04.
 */
public class TweetActivity extends Activity{

    static  final int REQUEST_CAPTURE_IMAGE = 100;
    static final int REQUEST_SELECT_IMAGE = 120;

    boolean isReply = false;

    private EditText mInputText;
    private Twitter mTwitter;
    boolean isImage = false;
    Bitmap mBitmap = null;

    Status status;


    @Override
    protected void onCreate(Bundle savedInstanceStage) {
        super.onCreate(savedInstanceStage);
        setContentView(R.layout.activity_tweet);
        Serializable serializable = getIntent().getSerializableExtra("tweet");
        if(serializable == null){}
        else{
            status = ((TweetSerialize)serializable).getStatus();
            ((TextView)findViewById(R.id.tweet_taskbar)).setText("Reply");
            findViewById(R.id.reply_user_info).setVisibility(View.VISIBLE);
            findViewById(R.id.location_addaccount).setVisibility(View.GONE);
            ((SmartImageView)findViewById(R.id.expansion_icon)).setImageUrl(status.getUser().getProfileImageURL());
            ((TextView)findViewById(R.id.expansion_name)).setText(status.getUser().getName());
            ((TextView)findViewById(R.id.expansion_screen_name)).setText("@" + status.getUser().getScreenName());
            ((TextView)findViewById(R.id.expansion_text)).setText(status.getText());
            ((TextView)findViewById(R.id.expansion_time)).setText(BlackUtil.getDateFormat(status.getCreatedAt()));
            ((TextView)findViewById(R.id.expansion_via)).setText("via " + BlackUtil.getVia(status.getSource()));
            String str = status.getInReplyToScreenName();
            if(str == null || str.equals("null") || str.equals("")){
                str = status.getUser().getScreenName();
            }
            ((EditText)findViewById(R.id.input_text)).setText("@" + str + " ");
            isReply = true;
        }

        mTwitter = TwitterUtils.getTwitterInstance(this);
        mInputText = (EditText) findViewById(R.id.input_text);
        findViewById(R.id.tweet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweet();
                finish();
            }
        });

        findViewById(R.id.tweet_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
            }
        });

        findViewById(R.id.tweet_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final EditText editText = (EditText)findViewById(R.id.input_text);
                final int nowCount = editText.getText().length();
                final int defaultCount = getResources().getInteger(R.integer.default_tweet_count);
                final TextView textView = (TextView)findViewById(R.id.tweet_count);
                textView.setText(String.valueOf(defaultCount - nowCount));
            }




        });

    }

    @Override
    protected void onActivityResult(int requestCoda, int resultCode, Intent data) {
        if (REQUEST_CAPTURE_IMAGE == requestCoda && resultCode == Activity.RESULT_OK){
            mBitmap = (Bitmap) data.getExtras().get("data");
            isImage = true;
        }else if(REQUEST_SELECT_IMAGE == requestCoda){
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                mBitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();

                isImage = true;

            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            return;

        }
        ImageView teet_view_image = (ImageView)findViewById(R.id.teet_view_image);
        teet_view_image.setImageBitmap(mBitmap);
    }

    private void tweet() {

        showNotification(R.drawable.ic_launcher2,"送信中","送信しています...",1);

        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    Twitter twitter = TwitterUtils.getTwitterInstance(TweetActivity.this);

                    final StatusUpdate statusUpdate = new StatusUpdate(String.valueOf(params[0]));
                    if (isImage) {

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        // 第一引数　画像の形式
                        // 第二引数　多分圧縮
                        // ByteArrayOutputStream
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

                        statusUpdate.media("test", inputStream);
                    }
                    if (isReply) {
                        statusUpdate.setInReplyToStatusId(status.getInReplyToStatusId());
                    }
                    twitter.updateStatus(statusUpdate);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                } else {
                    showNotification(R.drawable.ic_launcher_error,"失敗","ツイートの送信に失敗しました。",1);
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }


    public void showNotification(int image, String title, String text, int id){
        Notification notification = new Notification();
        notification.icon = image;
        Intent intent = new Intent(getApplicationContext(), MainStreamActivity.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(TweetActivity.this, 0, intent, 0);

        notification.setLatestEventInfo(getApplicationContext(), title ,text ,pendingIntent );

        notification.tickerText = text;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);

    }
}
