package jp.promin.android.blackhistory.utils;

import android.widget.Toast;

import jp.promin.android.blackhistory.BlackHistoryController;

public class BHLogger {
    public static void toast(String text) {
        Toast.makeText(BlackHistoryController.get().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void println(Object text) {
        System.out.println(String.format("[DEBUG-APP] %s", String.valueOf(text)));
    }

    public static void println(String tag, Object text) {
        System.out.println(String.format("[DEBUG-APP %s] %s", tag, String.valueOf(text)));
    }

    public static void printlnDetail() {
        StackTraceElement trace = new Throwable().getStackTrace()[1];
        println(trace.getClassName() + " " + trace.getMethodName(), "");
    }

    public static void printlnDetail(Object text) {
        StackTraceElement trace = new Throwable().getStackTrace()[1];
        println(trace.getClassName() + " " + trace.getMethodName(), text);
    }

    public static void printlnDetail(String tag, Object text) {
        StackTraceElement trace = new Throwable().getStackTrace()[1];
        println(tag + " " + trace.getClassName() + " " + trace.getMethodName(), text);
    }
}
