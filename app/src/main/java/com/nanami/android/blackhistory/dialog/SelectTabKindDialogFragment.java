package com.nanami.android.blackhistory.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nanami.android.blackhistory.R;

/**
 * Created by Telneko on 2015/01/17.
 */
public class SelectTabKindDialogFragment extends DialogFragment {
    public static SelectTabKindDialogFragment newInstance(){
        return  new SelectTabKindDialogFragment();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final CharSequence[] items = getActivity().getResources().getStringArray(R.array.tab_kind);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("タブの種類を選ぶのじゃ");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_compose);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*
                switch (i){
                    case 0: //Home
                        break;
                    case 1: //Notifications
                        break;
                    case 2: //Mentions
                        break;
                    case 3: //Favorites
                        break;
                    case 4: //Lists
                        break;
                    case 5: //Search
                        break;
                    case 6: //Followers
                        break;
                    case 7: //Messages
                        break;
                    case 8: //User
                        break;
                    default:
                        break;
                }
                */
                openFragment(i);
            }
        });
        return builder.create();
    }

    public void openFragment(int pos){
        SelectAccountDialogFragment fragment = SelectAccountDialogFragment.newInstance(R.string.SELECT_ACCOUNT_TYPE__CREATE_TAB, pos);
        fragment.show(getFragmentManager(), "ext_menu");
    }
}
