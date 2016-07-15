package com.nanami.android.blackhistory.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;

import java.util.Arrays;

/**
 * Created by Telneko on 2015/01/17.
 */
public class SelectTabKindDialogFragment extends DialogFragment {
    public static SelectTabKindDialogFragment newInstance(){
        return new SelectTabKindDialogFragment();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
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

    public void openFragment(int pos){
        SelectAccountDialogFragment fragment = SelectAccountDialogFragment
                .newInstance(R.string.SELECT_ACCOUNT_TYPE__CREATE_TAB, pos);
        fragment.show(getFragmentManager(), "ext_menu");
    }
}
