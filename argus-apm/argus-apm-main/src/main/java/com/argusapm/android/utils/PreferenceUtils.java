package com.argusapm.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author ArgusAPM Team
 */
public class PreferenceUtils {
    public static final String SP_NAME = "sp_apm_sdk";
    public static final String SP_KEY_DISPOSE_ITEM = "sp_key_dispose_time";

    public static final String SP_KEY_LAST_CLEAN_TIME = "sp_key_last_clean_time";

    public static final String SP_KEY_LAST_UPDATE_TIME = "sp_key_last_update_time";

    public static final String SP_KEY_RECENT_PIDS = "sp_key_recent_pids";

    public static final String SP_KEY_UPDATE_READ_CONFIG_TIME = "sp_key_update_read_config_time";

    public static final String SP_KEY_CONFIG_TIMESTAMP = "sp_key_config_timestamp";

    public static final String SP_KEY_LAST_FILE_INFO_TIME = "sp_key_last_file_info_time"; //fileinfo save time

    public static final String SP_KEY_LAST_MEMORY_TIME = "sp_key_last_memory_time"; //memory save time

    public static final String SP_KEY_LAST_THREAD_CNT_TIME = "sp_key_last_thread_cnt_time"; //memory save time

    public static void setLong(final Context context, final String key, final Long value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }

    public static long getLong(final Context context, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    public static void setString(final Context context, final String key, final String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(final Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(defaultValue)) {
            return sp.getString(key, "");
        } else {
            return sp.getString(key, "");
        }
    }

    public static void setInt(final Context context, final String key, final int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(final Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static boolean getBoolean(final Context context, String key, boolean defaultV) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultV);
    }

    public static void setBoolean(final Context context, final String key, final boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static void setFloat(final Context context, final String key, final float value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putFloat(key, value).commit();
    }

    public static float getFloat(final Context context, String key, float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }
}
