package jp.promin.android.blackhistory.ui.tweet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.ui.common.BaseActivity;
import jp.promin.android.blackhistory.utils.BlackUtil;
import jp.promin.android.blackhistory.utils.picture.ImageManager;
import twitter4j.Status;

public class TweetExpansionTweetActivity extends BaseActivity {
    final private static String EXTRA_STATUS = "extra_status";

    @BindView(R.id.expansion_icon)
    ImageView imageUserIcon;
    @BindView(R.id.expansion_name)
    TextView textUserName;
    @BindView(R.id.expansion_screen_name)
    TextView textUserScreenName;
    @BindView(R.id.expansion_text)
    TextView textUserTweet;
    @BindView(R.id.expansion_time)
    TextView textUserTime;
    @BindView(R.id.expansion_via)
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
