package com.nanami.android.blackhistory.utils;

import android.widget.Toast;

import com.nanami.android.blackhistory.AppController;

/**
 * Created by atsumi on 2016/01/12.
 */
public class BHLogger {
    public static void toast(String text) {
        Toast.makeText(AppController.get().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void println(Object text){
        System.out.println("[DEBUG-APP] " + text.toString());
    }
}
