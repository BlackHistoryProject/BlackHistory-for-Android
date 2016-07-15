package com.nanami.android.blackhistory.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanami.android.blackhistory.base.BaseActivity;
import com.nanami.android.blackhistory.component.PicassoImageView;
import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.utils.BHLogger;
import com.nanami.android.blackhistory.utils.BlackUtil;
import com.nanami.android.blackhistory.utils.TwitterUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

/**
 * Created by nanami on 2014/09/04.
 */
public class TweetActivity extends BaseActivity {

    private final static String EXTRA_USER_ID = "extra_user_id";
    private final static String EXTRA_SERIALIZE = "extra_serialize";
    private final static String EXTRA_FROM_REPLY = "extra_from_reply";

    private final static int REQUEST_CAPTURE_IMAGE = 100;
    private final static int REQUEST_SELECT_IMAGE = 120;

    @Nullable private Status status;
    @NonNull private Long userId;
    @NonNull private Boolean fromReply;

    @Bind(R.id.tweet_taskbar) TextView textTaskBar;
    @Bind(R.id.reply_user_info) RelativeLayout layoutReplayInfo;

    @Bind(R.id.expansion_icon) PicassoImageView imageIconView;
    @Bind(R.id.expansion_name) TextView textName;
    @Bind(R.id.expansion_screen_name) TextView textScreenName;
    @Bind(R.id.expansion_text) TextView textText;
    @Bind(R.id.expansion_time) TextView textTime;
    @Bind(R.id.expansion_via) TextView textVia;

    @Bind(R.id.input_text) EditText editText;

    @Bind(R.id.upload_image_1) ImageButton imageBtnUpload1;
    @Bind(R.id.upload_image_2) ImageButton imageBtnUpload2;
    @Bind(R.id.upload_image_3) ImageButton imageBtnUpload3;
    @Bind(R.id.upload_image_4) ImageButton imageBtnUpload4;

    @OnClick(R.id.tweet_button) void OnClickTweet(){
        tweet();
        finish();
    }

    @OnClick(R.id.tweet_camera) void OnClickTweetCamera(){
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAPTURE_IMAGE);
    }

    @OnClick(R.id.tweet_picture) void OnClickTweetPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    public static void startActivity(Context context, Long userId){
        startActivity(context, userId, null, false);
    }

    public static void startActivity(Context context, Long userId, Status tweet, Boolean isReply){
        Intent intent = new Intent(context, TweetActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_SERIALIZE, tweet);
        intent.putExtra(EXTRA_FROM_REPLY, isReply);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceStage) {
        super.onCreate(savedInstanceStage);
        setContentView(R.layout.activity_tweet);                                                    //  指定したIDのレイアウトを読み込んでいる

        this.userId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
        this.status = (Status) getIntent().getSerializableExtra(EXTRA_SERIALIZE);
        this.fromReply = getIntent().getBooleanExtra(EXTRA_FROM_REPLY, false);

        if (userId == -1L || (fromReply && status == null)) {
            BHLogger.toast("ツイートの読み込みに失敗しました");
            finish();
            return;
        }

        if (fromReply) {
            textTaskBar.setText("Reply");
            layoutReplayInfo.setVisibility(View.VISIBLE);

            imageIconView.loadImage(status.getUser().getProfileImageURL());
            textName.setText(status.getUser().getName());
            textScreenName.setText("@" + status.getUser().getScreenName());
            textText.setText(status.getText());
            textTime.setText(BlackUtil.getDateFormat(status.getCreatedAt()));
            textVia.setText("via " + BlackUtil.getVia(status.getSource()));

            String str = status.getInReplyToScreenName();
            if (str == null || str.equals("null") || str.equals("")) {
                str = status.getUser().getScreenName();
            }
            editText.setText("@" + str + " ");                                                      // ()の中のstrはReplyの相手のScreenName(相手の@以下のID)
            editText.setSelection(editText.getText().length());                                     //　setSelectionの中には数字が入る（今回は今入っている（）の中身が数字を持ってきてくれている
            editText.getText().length();                                                            // lengthは"長さ"であって、物の長さではなくて文字の長さ(サイズ)だそうで、プログラミングではそう呼ばれている

        } else {
            textTaskBar.setText("Tweet");
            layoutReplayInfo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCoda, int resultCode, Intent data) {
        Bitmap mBitmap = null;
        if (REQUEST_CAPTURE_IMAGE == requestCoda && resultCode == Activity.RESULT_OK){

            mBitmap = (Bitmap) data.getExtras().get("data");

        }else if(REQUEST_SELECT_IMAGE == requestCoda){
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                mBitmap = BitmapFactory.decodeStream(in, null, options);
                if (in != null) in.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (mBitmap == null) return;

        ImageButton btn = this.getEmptyImageButton();
        if (btn != null) btn.setImageBitmap(mBitmap);
        else System.out.println("いっぱいだよ。");
    }

    /**
     * ツイートをします
     */
    private void tweet() {
        /**
         * ツイートをしている旨を通知
         */
        showNotification(R.drawable.ic_launcher2,"送信中","送信しています...",1);
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    Twitter twitter = TwitterUtils.getTwitterInstance(TweetActivity.this, userId);

                    /**
                     * テキストビューの中身をセット (params に入ってる)
                     */
                    final StatusUpdate statusUpdate = new StatusUpdate(String.valueOf(params[0]));

                    /**
                     * 画像もアップロードするか
                     */
                    if (setedImage()) {

                        /**
                         * セットされている枚数分だけ、ツイートのインスタンスにセットする
                         */
                        UploadedMedia[] medias = new UploadedMedia[4];
                        int i = 0;
                        for (Bitmap bitmap : getImages()){
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
                            medias[i] = twitter.uploadMedia("test" + i, inputStream);
                            inputStream.close();
                            bos.close();
                            i++;
                        }

                        statusUpdate.setMediaIds(getMediaIDs(medias));
                    }
                    if (fromReply && status != null) {
                        statusUpdate.setInReplyToStatusId(status.getId());
                    }
                    twitter.updateStatus(statusUpdate);
                    return true;
                } catch (TwitterException | IOException e) {
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
        task.execute(editText.getText().toString());
    }

    public long[] getMediaIDs(UploadedMedia[] medias){
        ArrayList<Long> temp = new ArrayList<>();
        for (UploadedMedia media : medias) {
            if (media == null) break;
            temp.add(media.getMediaId());
        }

        long[] ret = new long[temp.size()];
        for (int i = 0; i < temp.size(); i++){
            ret[i] = temp.get(i);
        }
        return ret;
    }

    public void showNotification(int image, String title, String text, int id){
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(image);

        Intent intent = new Intent(getApplicationContext(), MainStreamActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(TweetActivity.this, 0, intent, 0);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setContentTitle(title);
        builder.setContentText(text);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(id, builder.build());
    }

    /**
     * 空のイメージボタンを返す
     * @return null だったら、全部埋まってる。
     */
    @Nullable private ImageButton getEmptyImageButton(){
        if (this.imageBtnUpload1.getDrawable() == null) return this.imageBtnUpload1;
        if (this.imageBtnUpload2.getDrawable() == null) return this.imageBtnUpload2;
        if (this.imageBtnUpload3.getDrawable() == null) return this.imageBtnUpload3;
        if (this.imageBtnUpload4.getDrawable() == null) return this.imageBtnUpload4;
        return null;
    }

    /**
     * セットされてる画像たちを返す
     * @return count == 0 だったら、何もセットされてない
     */
    private ArrayList<Bitmap> getImages(){
        ArrayList<Bitmap> res = new ArrayList<>();
        if (this.imageBtnUpload1.getDrawable() != null) res.add(((BitmapDrawable)this.imageBtnUpload1.getDrawable()).getBitmap());
        if (this.imageBtnUpload2.getDrawable() != null) res.add(((BitmapDrawable)this.imageBtnUpload2.getDrawable()).getBitmap());
        if (this.imageBtnUpload3.getDrawable() != null) res.add(((BitmapDrawable)this.imageBtnUpload3.getDrawable()).getBitmap());
        if (this.imageBtnUpload4.getDrawable() != null) res.add(((BitmapDrawable)this.imageBtnUpload4.getDrawable()).getBitmap());
        return res;
    }

    public boolean setedImage(){
        return
                this.imageBtnUpload1.getDrawable() != null ||
                this.imageBtnUpload2.getDrawable() != null ||
                this.imageBtnUpload3.getDrawable() != null ||
                this.imageBtnUpload4.getDrawable() != null;
    }
}
