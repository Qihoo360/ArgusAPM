package com.argusapm.android.cloudconfig;

/**
 * 云控逻辑常量配置类
 *
 * @author ArgusAPM Team
 */
public class Constant {
    // 配置请求的最小时间间隔
    public static final long INTERVAL = 1 * 60 * 60 * 1000;
    public static final long APP_START_CLOUD_MAX_DELAY_TIME = 10 * 1000;//APP（进程）启动候，云控请求最大延迟
    public static final long CLOUD_MIN_INTERVAL = 5 * 60 * 1000; //云规则请求的最小时间间隔
    public static final String CLOUD_RULE_UPDATE_ACTION = "com.apm.mobile.action.cloud.rule.update";
}
