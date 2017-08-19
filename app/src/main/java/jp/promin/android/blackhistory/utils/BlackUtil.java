package jp.promin.android.blackhistory.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.ui.mainstream.MainStreamActivity;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;

public class BlackUtil {

    /*曜日の設定*/

    static Map<Integer, String> days = new HashMap<>();
    //////////////////////////
    //ListDataUtils
    private static String ListDataDelimiter = "-";
    private static Pattern ListDataFormat = Pattern.compile("(\\d+)" + ListDataDelimiter + "(.*)");

    static String getDayKind(int dayOfweek) {
        if (days.size() == 0) initialize();
        return days.get(dayOfweek);
    }

    static void initialize() {
        days.put(1, "日");
        days.put(2, "月");
        days.put(3, "火");
        days.put(4, "水");
        days.put(5, "木");
        days.put(6, "金");
        days.put(7, "土");
    }

    @SuppressLint("DefaultLocale")
    public static String getDateFormat(Date date) {
        Calendar datetime = Calendar.getInstance();
        datetime.setTime(date);

        int year = datetime.get(Calendar.YEAR);
        int month = datetime.get(Calendar.MONTH) + 1;
        int day = datetime.get(Calendar.DAY_OF_MONTH);
        String dayKind = BlackUtil.getDayKind(datetime.get(Calendar.DAY_OF_WEEK));
        int hour = datetime.get(Calendar.HOUR_OF_DAY);
        int minute = datetime.get(Calendar.MINUTE);
        int second = datetime.get(Calendar.SECOND);

        return String.format(
                "%04d年%02d月%02d日(%s) %02d:%02d:%02d",
                year, month, day, dayKind, hour, minute, second);

    }

    public static String getVia(String tweetSource) {
        String reg = "<(.*)>(.*)<(.*)>";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(tweetSource);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "";
    }

    @NonNull
    public static String genListDataString(@NonNull Long userId, @NonNull TimelineListType listType) {
        String txt = String.format("%s" + ListDataDelimiter + "%s", userId, listType.name());
        try {
            txt = ListDataFormat.matcher(txt).groupCount() > 1 ? txt : "";
        } catch (Exception e) {
            txt = "";
            e.printStackTrace();
        }
        if (txt.equals("")) {
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
        if (dat == null) {
            BHLogger.printlnDetail("Failed parse ListData", txt);
        } else {
            if (dat.first == null || dat.second == null) {
                BHLogger.printlnDetail("Failed parse ListData Regex", matcher.group());
            } else {
                BHLogger.printlnDetail("ぱーすいいです。");
            }
        }
        return dat;
    }

    public static void showNotification(Context context, @DrawableRes int image, String title, String text, int notificationId) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(image);

        Intent intent = new Intent(context, MainStreamActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setContentTitle(title);
        builder.setContentText(text);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(notificationId, builder.build());
    }

    public static ProgressDialog createProgressDialog(@NonNull Context context, @StringRes int resourceID, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(context.getString(resourceID));
        dialog.setCancelable(cancelable);
        return dialog;
    }

    public static boolean checkCameraPermissions(Activity context, int INTENT_CODE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (context.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                        context.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        context.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ShowToast.showToast("パーミッションが必要です");
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                } else {
                    context.requestPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            INTENT_CODE);
                }
                return false;
            }
        }
        return true;
    }

    public static boolean checkLocationPermissions(Activity context, int INTENT_CODE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (context.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        context.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Nothing
                } else {
                    context.requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            INTENT_CODE);
                }
                return false;
            }
        }
        return true;
    }

    /*-- Camera --*/
    public static Uri wakeUpCamera(Activity activity) {
        String folderPath = Environment.getExternalStorageDirectory() + "/BlackHistory/";
        File folder = new File(folderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            ShowToast.showToast("directory Not found.");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyMMddHHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(folder + File.separator + timeStamp + ".jpg");
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri bitmapUri = Uri.fromFile(mediaFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, bitmapUri); // 画像をmediaUriに書き込み
        activity.startActivityForResult(i, Const.INTENT_REQUEST.CAMERA);

        return bitmapUri;
    }

    public static void wakeUpGallery(Activity activity) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);

        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Pick"), Const.INTENT_REQUEST.GALLERY);
    }

    public static View getInflateView(@NonNull Context context, @LayoutRes int resourceID) {
        try {
            return LayoutInflater.from(context).inflate(resourceID, null);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static View getInflateView(@LayoutRes int resourceID) {
        Context context = BlackHistoryController.get().getApplicationContext();
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(resourceID, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static class Const {
        public class INTENT_REQUEST {
            public static final int PERMISSION_REQUIRE_CAMERA = 1000;
            public static final int PERMISSION_REQUIRE_GALLERY = 2000;
            public static final int CAMERA = 100;
            public static final int GALLERY = 200;
        }
    }
}
