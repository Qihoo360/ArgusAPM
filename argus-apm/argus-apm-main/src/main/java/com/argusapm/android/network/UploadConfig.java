package com.argusapm.android.network;

/**
 * ArgusAPM相关配置
 *
 * @author ArgusAPM Team
 */
public class UploadConfig {
    public static final long UPLOAD_INTERVAL = 1 * 60 * 60 * 1000; //数据上传时间间隔
    public static final long UPLOAD_MIN_INTERVAL = 5 * 60 * 1000; //数据上传最小时间间隔
    public static final int RETRY_COUNT = 3; //上传失败重试次数
}
