package jp.promin.android.blackhistory.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.base.BaseActivity;
import jp.promin.android.blackhistory.utils.BHLogger;
import jp.promin.android.blackhistory.utils.BlackUtil;
import jp.promin.android.blackhistory.utils.ImageManager;
import twitter4j.Status;

public class TweetExpansionTweetActivity extends BaseActivity {
    final private static String EXTRA_STATUS = "extra_status";

    @Bind(R.id.expansion_icon)
    ImageView imageUserIcon;
    @Bind(R.id.expansion_name)
    TextView textUserName;
    @Bind(R.id.expansion_screen_name)
    TextView textUserScreenName;
    @Bind(R.id.expansion_text)
    TextView textUserTweet;
    @Bind(R.id.expansion_time)
    TextView textUserTime;
    @Bind(R.id.expansion_via)
    TextView textUserVia;

    public static void createIntent(Context context, Status status) {
        Intent intent = new Intent(context, TweetExpansionTweetActivity.class);
        intent.putExtra(EXTRA_STATUS, status);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.onetap_expansion_tweet);

        Status status = (Status) getIntent().getSerializableExtra(EXTRA_STATUS);
        if (status == null) {
            BHLogger.toast("ツイートの読み込みに失敗しました");
            finish();
            return;
        }

        ImageManager.getPicasso().load(status.getUser().getProfileImageURL()).into(this.imageUserIcon);

        this.textUserName.setText(status.getUser().getName());
        this.textUserScreenName.setText("@" + status.getUser().getScreenName());
        this.textUserTweet.setText(status.getText());
        this.textUserTime.setText(BlackUtil.getDateFormat(status.getCreatedAt()));
        this.textUserVia.setText("via " + BlackUtil.getVia(status.getSource()));
    }

}
