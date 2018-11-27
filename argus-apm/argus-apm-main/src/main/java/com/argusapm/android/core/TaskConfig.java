package com.argusapm.android.core;

/**
 * 任务相关配置
 *
 * @author ArgusAPM Team
 */
public class TaskConfig {
    public static final String BASE_DIR_PATH = "/360/Apm/"; //存储文件位置

    public static final int CPU_INTERVAL = 30 * 1000; //cpu采样间隔
    public static final int FPS_INTERVAL = 1000; //fps采样间隔
    public static final int DEFAULT_ANR_INTERVAL = 2 * 60 * 60 * 1000; // anr采样间隔
    public static final int MIN_ANR_INTERVAL = 30 * 60 * 1000; // anr最小采样间隔
    public static final int ANR_VALID_TIME = 2 * 24 * 60 * 60 * 1000; // anr文件有效期
    public static final int BATTERY_INTERVAL = 2 * 60 * 60 * 1000; //电量采样间隔
    public static final int FILE_INFO_INTERVAL = 12 * 60 * 60 * 1000; //文件采样间隔
    public static final int TEST_INTERVAL = 30 * 1000; // 测试用
    public static final int TASK_DELAY_RANDOM_INTERVAL = 2 * 1000; //定时任务启动随机延迟时间

    public static final int IO_INTERVAL = 30 * 1000; // IO轮训检查文件是否关闭
    public static final int IO_TIMEOUT_INTERVAL = 30 * 1000; // IO timeout

    public static final int DEBUG_PROCESS_LIVE_INTERVAL = 30 * 1000; // Debug模式下检测进程是否存在间隔

    public static int DEFAULT_PAUSE_INTERVAL = 2 * 60 * 60 * 1000; //默认高频任务采样暂停间隔
    public static int DEFAULT_ONCE_MAX_COUNT = 130; //默认高频任务采样，一次最大采样数量

    public static final long ONRECEIVE_MIN_TIME = 2000; // 大于ONRECEIVE_MIN_TIME的BroadcastReceiver才执行onReceive方法采执行收集
    public static final long THREAD_MIN_TIME = 2000; // 大于THREAD_MIN_TIME的线程的run方法才执行收集
    public static final long IO_MIN_TIME = 2000; // io时间大于IO_MIN_TIME的文件执行收集

    // 云控activity的采集方案
    // ACTIVITY_TYPE_NONE:不使用云控配置，以本地配置的采集方案优先
    public static final int ACTIVITY_TYPE_NONE = 0;
    public static final int ACTIVITY_TYPE_INSTRUMENTATION = 1;
    public static final int ACTIVITY_TYPE_AOP = 2;

    public static final String DATABASES = "databases"; //应用的数据库文件目录

    public static final int FILE_MIN_SIZE = 50 * 1024;       //    文件大小大于FILE_MIN_SIZE的才收集
    public static final int DEFAULT_ACTIVITY_FIRST_MIN_TIME = 300; //Activity生命周期第一帧最小数据收集时间间隔 单位：ms
    public static final int DEFAULT_ACTIVITY_LIFECYCLE_MIN_TIME = 100; // Activity生命周期最小数据收集时间间隔单位：ms
    public static final int DEFAULT_BLOCK_TIME = 4500; // block超时时间
    public static final int DEFAULT_FPS_MIN_COUNT = 30; //最小上报帧率
    public static final int MIN_MEMORY_DELAY_TIME = 10 * 1000; //最小内存采样延迟时间
    public static final int DEFAULT_MEMORY_DELAY_TIME = 10 * 1000; //默认内存收集延迟时间（进程启动后多久开始收集内存数据）
    public static final int DEFAULT_MEMORY_INTERVAL = 30 * 60 * 1000; //默认内存采样间隔
    public static final boolean RUN_TEST_CASE = false; //是否进行CPU测试，只用于本地测试
    public static final int DEFAULT_FILE_DEPTH = 3; // file的默认采样文件夹层级
    public static final int MIN_THREAD_CNT_DELAYTIME = 10 * 1000; //最小线程数采样延迟时间
    public static final int DEFAULT_THREAD_CNT_DELAY_TIME = 10 * 1000; //线程数采集的默认延迟时间
    public static final int DEFAULT_THREAD_CNT_INTERVAL_TIME = 30 * 60 * 1000; //线程数采集的默认间隔时间

    public static final long RANDOM_CONTROL_TIME = 10 * 60 * 1000;// 请求云控的随机时间
    public static final long MAX_READ_FILE_SIZE = 50 * 1024 * 1024;//文件读取的最大限制值
    public static final int DEFAULT_WATCH_DOG_DELAY_TIME = 10 * 1000; //watchDog采集默认延迟时间
    public static final int DEFAULT_WATCH_DOG_INTERVAL_TIME = 5 * 1000; //watchDog采集默认间隔时间
    public static final int DEFAULT_WATCH_DOG_MIN_TIME = 4500; //最小上报单帧耗时
}
