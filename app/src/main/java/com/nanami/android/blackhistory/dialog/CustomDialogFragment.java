package com.nanami.android.blackhistory.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import com.nanami.android.blackhistory.R;
import com.nanami.android.blackhistory.fragment.list.TimelineListType;

import java.io.Serializable;

/**
 * Created by Telneko on 2015/01/17.
 */
public class CustomDialogFragment extends DialogFragment {
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_STRING_RES = "extra_string_res";
    public static final String EXTRA_DIALOG = "extra_dialog";
    public static CustomDialogFragment newInstance(String title, @ArrayRes int menuRes, DialogListener listener){
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putInt(EXTRA_STRING_RES, menuRes);
        bundle.putSerializable(EXTRA_DIALOG, listener);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final String title = getArguments().getString(EXTRA_TITLE);
        final int menuRes = getArguments().getInt(EXTRA_STRING_RES, -1);
        final DialogListener listener = (DialogListener) getArguments().getSerializable(EXTRA_DIALOG);

        final CharSequence[] items = getActivity().getResources().getStringArray(menuRes);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_compose);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onClick((String[]) items, i);
                }
            }
        });
        return builder.create();
    }

    public interface DialogListener extends Serializable {
        void onClick(String[] menuRes, int position);
    }
}
