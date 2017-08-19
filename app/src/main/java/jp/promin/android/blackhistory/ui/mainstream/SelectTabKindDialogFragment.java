package jp.promin.android.blackhistory.ui.mainstream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.promin.android.blackhistory.R;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;

public class SelectTabKindDialogFragment extends DialogFragment {
    public static SelectTabKindDialogFragment newInstance() {
        return new SelectTabKindDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] items = TimelineListType.getValues();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("タブの種類を選ぶのじゃ");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_compose);
        builder.setItems(items, (dialogInterface, i) -> {
            openFragment(i);
        });
        return builder.create();
    }

    public void openFragment(int pos) {
        SelectAccountDialogFragment fragment = SelectAccountDialogFragment
                .newInstance(R.string.SELECT_ACCOUNT_TYPE__CREATE_TAB, pos);
        fragment.show(getFragmentManager(), "ext_menu");
    }
}
