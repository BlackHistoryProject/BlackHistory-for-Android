package jp.promin.android.blackhistory.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkText {
    public static SpannableString setLinkText(final Context context, String text, final OnClickLinkListener listener) {
        SpannableString builder = new SpannableString(text);

        // URL
        Pattern pattern = Pattern.compile("https?://[\\w/:%#\\$&\\?\\(\\)~\\.=\\+\\-]+");
        final Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            int start = urlMatcher.start();
            int end = urlMatcher.end();
            final String uri = urlMatcher.group(0);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    listener.onClick(widget, true, uri);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // TAG
        pattern = Pattern.compile("[#＃][Ａ–Ｚａ-ｚA-Za-z一-鿆0-9０-９ぁ-ヶｦ-ﾟー_]+");
        Matcher tagMatcher = pattern.matcher(text);
        while (tagMatcher.find()) {
            int start = tagMatcher.start();
            int end = tagMatcher.end();
            final String tag = tagMatcher.group(0).substring(1);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    listener.onClick(widget, false, tag);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return builder;
    }

    public interface OnClickLinkListener {
        void onClick(View widget, boolean isUrl, String clickText);
    }
}
