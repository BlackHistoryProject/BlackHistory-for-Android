package jp.promin.android.blackhistory.ui.mainstream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.model.UserToken;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;
import jp.promin.android.blackhistory.ui.twitter.TwitterOAuthActivity;
import jp.promin.android.blackhistory.utils.twitter.TwitterUtils;

public class SelectAccountDialogFragment extends DialogFragment {
    public static final String SELECT_TYPE = "selectType";
    public static final String SELECT_TAB_POSITION = "selectTabPosition";
    List<UserToken> tokens;

    /***
     * 前の画面が、選択画面じゃない時や、　前の選択によって結果が影響されない時に使う。
     * ただのアカウント切り替えとか...
     * @param selectTypeID
     * @return
     */
    public static SelectAccountDialogFragment newInstance(int selectTypeID) {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECT_TYPE, selectTypeID);
        SelectAccountDialogFragment dialog = new SelectAccountDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    /***
     * 前の画面の選択で、結果が変わるやつ。
     * 追加タブ選択->アカウント選択->洗濯したアカウントの洗濯したタブ　とか...
     * @param selectTypeID
     * @param selectTabPosition
     * @return
     */
    public static SelectAccountDialogFragment newInstance(int selectTypeID, int selectTabPosition) {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECT_TYPE, selectTypeID);
        bundle.putInt(SELECT_TAB_POSITION, selectTabPosition);
        SelectAccountDialogFragment dialog = new SelectAccountDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.tokens = TwitterUtils.getAccounts(getContext());

        final Bundle bundle = getArguments(); //引数の取得

        ArrayList<String> menu = new ArrayList<>();

        for (UserToken tokenObject : tokens) {
            menu.add(tokenObject.getScreenName());
        }

        if (bundle.getInt(SELECT_TYPE) == R.string.SELECT_ACCOUNT_TYPE__CHANGE_ACCOUNT) {
            menu.add("アカウントの追加");
        }

        final CharSequence[] items = menu.toArray(new CharSequence[menu.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("アカウントを選びたまえ");
        builder.setIcon(R.drawable.ic_tweet_menu);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (bundle.getInt(SELECT_TYPE)) { //前の画面の種類
                    // タブ洗濯の画面から来たとき
                    case R.string.SELECT_ACCOUNT_TYPE__CREATE_TAB:
                        tabCreate(i, bundle.getInt(SELECT_TAB_POSITION));
                        break;
                    case R.string.SELECT_ACCOUNT_TYPE__CHANGE_ACCOUNT:
                        changeAccount(i);
                        break;
                    default:
                        break;
                }
            }
        });
        return builder.create();
    }

    public void tabCreate(int accountNum, int sel) {
        UserToken token = this.tokens.get(accountNum);

        ((MainStreamActivity) getActivity()).mAdapter.addTab(TimelineListType.kindOf(sel), token.getId());

        Toast.makeText(getActivity(), "タブを作成しました", Toast.LENGTH_SHORT).show();
    }

    public void changeAccount(int sel) {
        // アカウントの追加を押したときは、Twitter認証に飛ばす
        if (sel == this.tokens.size()) {
            startActivity(new Intent(getActivity(), TwitterOAuthActivity.class));
        }
    }
}
