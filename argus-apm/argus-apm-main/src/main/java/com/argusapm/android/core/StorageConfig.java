package com.argusapm.android.core;

/**
 * 存储相关配置
 *
 * @author ArgusAPM Team
 */
public class StorageConfig {
    public static final String CONTENT_PATH_PREFIX = "content://";
    public static final String AUTHORITY_SUFFIX = "apm.storage";
    public static final String DB_NAME = "apm.db"; //数据库名称
    public static final int DB_VERSION = 14; //数据库版本号,字段新增或改变后，必须升级此版本号

    public static final int SAVE_DB_INTERVAL = 15 * 1000;// 写入数据库最短时间间隔，防止频繁IO
    public static final int SAVE_DB_MAX_COUNT = 100;// 写入数据库最大缓存条数


    /*****************数据清理相关配置************************/
    public static final long DATA_CLEAR_INTERVAL_TIME = 2 * 60 * 60 * 1000; // 两小时执行一次即可
    public static final long DATA_CLEAR_INTERVAL_MIN_TIME = 10 * 60 * 1000; // 触发清理的最短时间是10分钟
    public static final int DATA_OVER_DAY = 2; //日志过期时间(天)

}
