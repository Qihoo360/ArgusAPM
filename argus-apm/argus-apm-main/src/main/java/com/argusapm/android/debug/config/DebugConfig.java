package com.argusapm.android.debug.config;

/**
 * APM Debug模块配置信息
 *
 * @author ArgusAPM Team
 */
public class DebugConfig {

    public static int NET_REQUEST_SUCCESS = 200;
    public static String OUTPUT_FILE = "apm_debug_warning.txt";//存储文件名称
    public static int TEXT_COLOR_DEFAULT = 0xFFffffff;//白色文本
    public static int BIG_WINDOW_BG_COLOR = 0x99000000; //大悬浮窗背景颜色
    public static int TEXT_COLOR_GREEN = 0xff29a600; //绿色文本
    public static int TEXT_COLOR_WARN = 0xffff0000; //警告文本颜色
    public static int DEFAULT_PADDING = 10;
    public static int DEFAULT_TEXT_SIZE = 10;

    //警报阈值
    public static int WARN_ACTIVITY_CREATE_VALUE = 300; //Activity生命周期oncreate警报时间间隔 单位：ms
    public static int WARN_ACTIVITY_FRAME_VALUE = 500; //Activity生命周期第一帧警报时间间隔 单位：ms
    public static int WARN_FPS_VALUE = 40;
    public static int WARN_CPU_VALUE = 10;

}
