package jp.promin.android.blackhistory.ui.tweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.event.TweetFailureEvent;
import jp.promin.android.blackhistory.event.TweetSuccessEvent;
import jp.promin.android.blackhistory.ui.common.BaseActivity;
import jp.promin.android.blackhistory.utils.BlackUtil;
import jp.promin.android.blackhistory.utils.ShowToast;
import jp.promin.android.blackhistory.utils.picture.ImageManager;
import jp.promin.android.blackhistory.utils.rx.RxListener;
import jp.promin.android.blackhistory.utils.twitter.TwitterAction;
import jp.promin.android.blackhistory.utils.twitter.TwitterUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.UploadedMedia;

@SuppressWarnings({"Anonymous2MethodRef", "Convert2Lambda"})
public final class TweetActivity extends BaseActivity {
    private static final String EXTRA_USER_ID = "extra_user_id";
    private static final String EXTRA_REPLY_STATUS = "extra_reply_status";

    private final static int REQUEST_CAPTURE_IMAGE = 100;
    private final static int REQUEST_SELECT_IMAGE = 120;
    @Bind(R.id.tweet_taskbar)
    TextView textTaskBar;
    @Bind(R.id.reply_user_info)
    RelativeLayout layoutReplayInfo;
    @Bind(R.id.expansion_icon)
    ImageView imageIconView;
    @Bind(R.id.expansion_name)
    TextView textName;
    @Bind(R.id.expansion_screen_name)
    TextView textScreenName;
    @Bind(R.id.expansion_text)
    TextView textText;
    @Bind(R.id.expansion_time)
    TextView textTime;
    @Bind(R.id.expansion_via)
    TextView textVia;
    @Bind(R.id.input_text)
    EditText editText;
    @Bind(R.id.upload_image_1)
    ImageButton imageBtnUpload1;
    @Bind(R.id.upload_image_2)
    ImageButton imageBtnUpload2;
    @Bind(R.id.upload_image_3)
    ImageButton imageBtnUpload3;
    @Bind(R.id.upload_image_4)
    ImageButton imageBtnUpload4;

    public static void startActivity(@NonNull Context context, long tweetUserId) {
        final Intent intent = new Intent(context, TweetActivity.class);
        intent.putExtra(EXTRA_USER_ID, tweetUserId);
        context.startActivity(intent);
    }

    public static void startActivity(@NonNull Context context, long tweetUserId, @NonNull Status replyStatus) {
        final Intent intent = new Intent(context, TweetActivity.class);
        intent.putExtra(EXTRA_USER_ID, tweetUserId);
        intent.putExtra(EXTRA_REPLY_STATUS, replyStatus);
        context.startActivity(intent);
    }

    @OnClick(R.id.tweet_button)
    void OnClickTweet() {
        tweet();
    }

    @OnClick(R.id.tweet_camera)
    void OnClickTweetCamera() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAPTURE_IMAGE);
    }

    @OnClick(R.id.tweet_picture)
    void OnClickTweetPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private long getTweetUserId() {
        return getIntent().getLongExtra(EXTRA_USER_ID, -1);
    }

    @Nullable
    private Status getReplyStatus() {
        final Serializable status = getIntent().getSerializableExtra(EXTRA_REPLY_STATUS);
        if (status != null) {
            return (Status) status;
        } else {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceStage) {
        super.onCreate(savedInstanceStage);
        setContentView(R.layout.activity_tweet);                                                    //  指定したIDのレイアウトを読み込んでいる

        final Status replyStatus = getReplyStatus();
        if (replyStatus == null) {
            // リプライ対象がセットされていないから普通のツイート
            textTaskBar.setText("Tweet");
            layoutReplayInfo.setVisibility(View.GONE);
        } else {
            // リプライ対象がセットされているからリプライ
            textTaskBar.setText("Reply");
            layoutReplayInfo.setVisibility(View.VISIBLE);

            ImageManager.getPicasso().load(replyStatus.getUser().getProfileImageURL()).into(imageIconView);

            textName.setText(replyStatus.getUser().getName());
            textScreenName.setText("@" + replyStatus.getUser().getScreenName());
            textText.setText(replyStatus.getText());
            textTime.setText(BlackUtil.getDateFormat(replyStatus.getCreatedAt()));
            textVia.setText("via " + BlackUtil.getVia(replyStatus.getSource()));

            String str = replyStatus.getInReplyToScreenName();
            if (str == null || str.equals("null") || str.equals("")) {
                str = replyStatus.getUser().getScreenName();
            }
            editText.setText("@" + str + " ");                                                      // ()の中のstrはReplyの相手のScreenName(相手の@以下のID)
            editText.setSelection(editText.getText().length());                                     //　setSelectionの中には数字が入る（今回は今入っている（）の中身が数字を持ってきてくれている
            editText.getText().length();                                                            // lengthは"長さ"であって、物の長さではなくて文字の長さ(サイズ)だそうで、プログラミングではそう呼ばれている
        }
    }

    @Override
    protected void onActivityResult(int requestCoda, int resultCode, Intent data) {
        Bitmap mBitmap = null;
        if (REQUEST_CAPTURE_IMAGE == requestCoda && resultCode == Activity.RESULT_OK) {

            mBitmap = (Bitmap) data.getExtras().get("data");

        } else if (REQUEST_SELECT_IMAGE == requestCoda) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                mBitmap = BitmapFactory.decodeStream(in, null, options);
                if (in != null) in.close();

            } catch (Exception e) {
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
        final Twitter twitter = TwitterUtils.getTwitterInstance(TweetActivity.this, getTweetUserId());

        final List<Bitmap> images = getImages();
        final long[] mediaIds;
        if (!images.isEmpty()) {
            final List<Long> uploadedMediaIds = Observable
                    .fromIterable(getImages())
                    .flatMap(new Function<Bitmap, ObservableSource<UploadedMedia>>() {
                        @Override
                        public ObservableSource<UploadedMedia> apply(@NonNull Bitmap bitmap) throws Exception {
                            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            final InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
                            return
                                    new RxListener<UploadedMedia>() {
                                        @Override
                                        public UploadedMedia result() throws Throwable {
                                            return twitter.uploadMedia("media", inputStream);
                                        }
                                    }
                                            .toObservable()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .doOnComplete(new Action() {
                                                @Override
                                                public void run() throws Exception {
                                                    inputStream.close();
                                                    bos.close();
                                                }
                                            });
                        }
                    })
                    .map(new Function<UploadedMedia, Long>() {
                        @Override
                        public Long apply(@NonNull UploadedMedia media) throws Exception {
                            return media.getMediaId();
                        }
                    })
                    .toList().blockingGet();
            mediaIds = new long[uploadedMediaIds.size()];
            for (int i = 0; i < uploadedMediaIds.size(); i++) {
                mediaIds[i] = uploadedMediaIds.get(i);
            }
        } else {
            mediaIds = new long[]{};
        }

        // tweetText
        final String tweetText = editText.getText().toString();

        // tweet
        if (mediaIds.length == 0) {
            final Status replyStatus = getReplyStatus();
            if (replyStatus == null) {
                TwitterAction.tweet(this, tweetText, getTweetUserId());
            } else {
                TwitterAction.reply(this, tweetText, getTweetUserId(), replyStatus);
            }
        } else {
            final Status replyStatus = getReplyStatus();
            if (replyStatus == null) {
                TwitterAction.tweetWithMedia(this, tweetText, getTweetUserId(), mediaIds);
            } else {
                TwitterAction.replyWithMedia(this, tweetText, getTweetUserId(), replyStatus, mediaIds);
            }
        }
    }

    @Override
    protected boolean shouldUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTweetSuccess(TweetSuccessEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTweetFailure(TweetFailureEvent event) {
        ShowToast.showToast(event.getThrowable().getLocalizedMessage());
    }

    /**
     * 空のイメージボタンを返す
     *
     * @return null だったら、全部埋まってる。
     */
    @Nullable
    private ImageButton getEmptyImageButton() {
        if (this.imageBtnUpload1.getDrawable() == null) return this.imageBtnUpload1;
        if (this.imageBtnUpload2.getDrawable() == null) return this.imageBtnUpload2;
        if (this.imageBtnUpload3.getDrawable() == null) return this.imageBtnUpload3;
        if (this.imageBtnUpload4.getDrawable() == null) return this.imageBtnUpload4;
        return null;
    }

    /**
     * セットされてる画像たちを返す
     *
     * @return count == 0 だったら、何もセットされてない
     */
    private List<Bitmap> getImages() {
        List<Bitmap> res = new ArrayList<>();
        if (this.imageBtnUpload1.getDrawable() != null)
            res.add(((BitmapDrawable) this.imageBtnUpload1.getDrawable()).getBitmap());
        if (this.imageBtnUpload2.getDrawable() != null)
            res.add(((BitmapDrawable) this.imageBtnUpload2.getDrawable()).getBitmap());
        if (this.imageBtnUpload3.getDrawable() != null)
            res.add(((BitmapDrawable) this.imageBtnUpload3.getDrawable()).getBitmap());
        if (this.imageBtnUpload4.getDrawable() != null)
            res.add(((BitmapDrawable) this.imageBtnUpload4.getDrawable()).getBitmap());
        return res;
    }
}
