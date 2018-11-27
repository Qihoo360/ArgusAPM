package com.argusapm.android.utils;

import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.debug.storage.TraceWriter;

/**
 * 日志类
 *
 * @author ArgusAPM Team
 */
public class LogX {
    private static final String LOG_FORMATTER = "❖ %s/%s  ❖  %s";

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void d(String tag, String subTag, String msg) {
        Log.d(tag, String.format(LOG_FORMATTER, ProcessUtils.getCurrentProcessName(), subTag, msg));
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void w(String tag, String subTag, String msg) {
        Log.w(tag, String.format(LOG_FORMATTER, ProcessUtils.getCurrentProcessName(), subTag, msg));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(String tag, String subTag, String msg) {
        Log.i(tag, String.format(LOG_FORMATTER, ProcessUtils.getCurrentProcessName(), subTag, msg));
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String subTag, String msg) {
        Log.e(tag, String.format(LOG_FORMATTER, ProcessUtils.getCurrentProcessName(), subTag, msg));
    }

    public static void trace(String tag, String subTag, String msg) {
        TraceWriter.log(tag, String.format(LOG_FORMATTER, ProcessUtils.getCurrentProcessName(), subTag, msg));
    }

    public static void o(String tag, String subTag, String msg) {
        Log.d(tag, String.format(LOG_FORMATTER, ProcessUtils.getCurrentProcessName(), subTag, msg));
    }

    public static void o(String msg) {
        Log.d(Env.TAG_O, msg);
    }
}
