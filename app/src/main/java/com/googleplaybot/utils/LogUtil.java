package com.googleplaybot.utils;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import com.github.pwittchen.kirai.library.Formatter;
import com.github.pwittchen.kirai.library.Kirai;
import com.github.pwittchen.kirai.library.html.HtmlPiece;
import com.googleplaybot.events.ui.LogEvent;

import java.text.SimpleDateFormat;

import timber.log.Timber;

public final class LogUtil {

    public static final String GREEN = "#4CAF50";
    public static final String ORANGE = "#FFC107";
    public static final String RED = "#F44336";
    public static final String BLUE = "#2196F3";
    public static final String GRAY = "#616161";

    @SuppressWarnings("all")
    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
    private static final Formatter formatter = new Formatter() {
        @Override
        public CharSequence format(String input) {
            return fromHtml(input);
        }
    };

    private static Timber.Tree tag(String tag) {
        return Timber.tag(tag);
    }

    public static void d(Class<?> clss, @Nullable String text) {
        tag(clss.getSimpleName()).d(text);
        log(GRAY, text);
    }

    public static void w(Class<?> clss, @Nullable String text) {
        tag(clss.getSimpleName()).w(text);
        log(ORANGE, text);
    }

    public static void e(Class<?> clss, @Nullable String text) {
        tag(clss.getSimpleName()).e(text);
        log(RED, text);
    }

    public static void i(Class<?> clss, @Nullable String text) {
        tag(clss.getSimpleName()).i(text);
    }

    private static void log(String color, @Nullable String text) {
        if (text == null) {
            return;
        }
        EventUtil.post(new LogEvent(Kirai.from("{time} {text}")
                    .put(HtmlPiece.put("time", getCurrentTime())
                            .bold().color(GREEN))
                    .put(HtmlPiece.put("text", text.trim()).color(color))
                    .format(formatter)));
    }

    public static void cmd(String user, String cmd) {
        EventUtil.post(new LogEvent(Kirai.from("{time} {cmd}")
                .put(HtmlPiece.put("time", getCurrentTime() + " @" + user + ":~$")
                        .bold().color(BLUE))
                .put(HtmlPiece.put("cmd", cmd.trim())
                        .bold().color(BLUE))
                .format(formatter)));
    }

    public static void print(CharSequence text) {
        EventUtil.post(new LogEvent(text));
    }

    public static boolean isDuplicatedLog(CharSequence log, CharSequence text) {
        return log.toString().trim().length() >= 22 && text.toString().trim()
                .endsWith(log.toString().trim().substring(21));
    }

    private static String getCurrentTime() {
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
