package com.nanami.android.blackhistory.utils;

import android.widget.Toast;

/**
 * Created by atsumi on 2016/01/12.
 */
public class BHLogger {
    final public static void toast(String text) {
        // TODO: this -> getApplicationContext
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
