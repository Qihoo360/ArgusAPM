package com.argusapm.android;

/**
 * APM全局配置文件
 *
 * @author ArgusAPM Team
 */
public class Env {
    public static final boolean DEBUG = BuildConfig.IS_DEBUG;

    public static final String VERSION = BuildConfig.VERSION;
    public static final String BUILD = BuildConfig.BUILD;

    public static final String getVersionName() {
        return VERSION + "." + BUILD;
    }

    /**
     * apm日志输出TAG
     */
    public static final String TAG = "apm_debug";

    /**
     * apm对外输出的日志TAG
     */
    public static final String TAG_O = "argus_apm";

    /**
     * 是否将数据库生成到sdcard方便调试
     */
    public static final boolean DB_IN_SDCARD = BuildConfig.DB_IN_SDCARD;
}