package com.nanami.android.blackhistory.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.nanami.android.blackhistory.fragment.list.TimelineListType;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nanami on 2014/09/06.
 */
public class BlackUtil {

    /*曜日の設定*/

    static Map<Integer, String> days = new HashMap<>();

    static String getDayKind(int dayOfweek) {
        if(days.size() == 0) initialize();
        return days.get(dayOfweek);
   }

   static void initialize(){
       days.put(1, "日");
       days.put(2, "月");
       days.put(3, "火");
       days.put(4, "水");
       days.put(5, "木");
       days.put(6, "金");
       days.put(7, "土");
   }

    static String convertInt(int val) {
        return (10 > val)?"0" + String.valueOf(val):String.valueOf(val);
    }

    @SuppressLint("DefaultLocale")
    public static String getDateFormat(Date date) {
        Calendar datetime = Calendar.getInstance();
        datetime.setTime(date);

        int year = datetime.get(Calendar.YEAR);
        int month = datetime.get(Calendar.MONTH)+1;
        int day = datetime.get(Calendar.DAY_OF_MONTH);
        String dayKind = BlackUtil.getDayKind(datetime.get(Calendar.DAY_OF_WEEK));
        int hour = datetime.get(Calendar.HOUR_OF_DAY);
        int minute = datetime.get(Calendar.MINUTE);
        int second = datetime.get(Calendar.SECOND);

        return String.format(
                "%04d年%02d月%02d日(%s) %02d:%02d:%02d",
                year,month,day,dayKind, hour,minute,second);

        }
    public static String getVia(String tweetSource){
        String reg = "<(.*)>(.*)<(.*)>";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(tweetSource);
        if (matcher.find()){
            return matcher.group(2);
        }
        return "";
    }

    //////////////////////////
    //ListDataUtils
    private static String ListDataDelimiter = "-";
    private static Pattern ListDataFormat = Pattern.compile("(\\d+)" + ListDataDelimiter + "(.*)");

    @NonNull
    public static String genListDataString(@NonNull Long userId, @NonNull TimelineListType listType){
        String txt = String.format("%s" + ListDataDelimiter + "%s", userId, listType.name());
        try {
            txt = ListDataFormat.matcher(txt).groupCount() > 1 ? txt : "";
        } catch (Exception e){
            txt = "";
            e.printStackTrace();
        }
        if (txt.equals("")){
            BHLogger.printlnDetail("Failed generate ListData", txt);
        }
        return txt;
    }

    @Nullable
    public static Pair<Long, TimelineListType> genListData(@NonNull String txt) {
        Pair<Long, TimelineListType> dat = null;
        Matcher matcher = ListDataFormat.matcher(txt);
        try {
            if (matcher.find()) {
                BHLogger.printlnDetail("みつかりはした。");
                String val = matcher.group(1);
                String type = matcher.group(2);
                dat = new Pair<>(Long.parseLong(val), TimelineListType.getType(type));
            } else {
                BHLogger.printlnDetail("ぱーすだめです。");
            }
        } catch (Exception e) {
            BHLogger.printlnDetail(e.toString());
        }
        if (dat == null){
            BHLogger.printlnDetail("Failed parse ListData", txt);
        }else {
            if (dat.first == null || dat.second == null) {
                BHLogger.printlnDetail("Failed parse ListData Regex", matcher.group());
            } else {
                BHLogger.printlnDetail("ぱーすいいです。");
            }
        }
        return dat;
    }
}
