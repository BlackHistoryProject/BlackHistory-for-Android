package com.nanami.android.blackhistory.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by atsumi on 2016/01/11.
 */
public class TwiStreamUtils {
    public static String getVia(String tweetSource){
        String reg = "<(.*)>(.*)<(.*)>";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(tweetSource);
        if (matcher.find()){
            return matcher.group(2);
        }
        return "";
    }
}
