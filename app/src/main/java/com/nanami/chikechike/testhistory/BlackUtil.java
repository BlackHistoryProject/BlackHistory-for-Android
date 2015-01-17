package com.nanami.chikechike.testhistory;

import android.content.Intent;

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

    static Map<Integer, String> days = new HashMap<Integer, String>();

    static String getDayKind(int dayOfWweek) {
        if(days.size() == 0) initialize();
        return days.get(dayOfWweek);

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

    static String converrInt(int val) {
        return (10 > val)?"0" + String.valueOf(val):String.valueOf(val);
    }

    public static String getDateFormat(Date date) {
        Calendar datetime = Calendar.getInstance();
        datetime.setTime(date);
        String dateString = "";

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

}
