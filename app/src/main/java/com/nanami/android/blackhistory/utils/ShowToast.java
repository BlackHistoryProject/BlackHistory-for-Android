package com.nanami.android.blackhistory.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.nanami.android.blackhistory.AppController;
import com.nanami.android.blackhistory.R;

/**
 * Created by atsumi on 2016/09/27.
 */

public class ShowToast {
    public static void showToast(String text, Integer duration){
        Context context = AppController.get().getApplicationContext();
        try{
            TextView view = (TextView) BlackUtil.getInflateView(R.layout.item_textview_corner_rect);
            if(view == null) throw new IllegalArgumentException();
            view.setGravity(Gravity.CENTER_HORIZONTAL);
            view.setText(text);
            Toast toast = new Toast(context);
            toast.setView(view);
            toast.setDuration(duration);
            toast.show();
        }catch (Exception e){
            try{
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }catch (Exception ignore){
            }
        }
    }
    public static void showToast(Integer textID){
        Context context = AppController.get().getApplicationContext();
        showToast(context.getString(textID), Toast.LENGTH_SHORT);
    }
    public static void showToast(String text){
        showToast(text, Toast.LENGTH_SHORT);
    }
}
