package com.argusapm.android.api;

import com.argusapm.android.core.job.activity.ActivityStorage;
import com.argusapm.android.core.job.activity.ActivityTable;
import com.argusapm.android.core.job.appstart.AppStartStorage;
import com.argusapm.android.core.job.appstart.AppStartTable;
import com.argusapm.android.core.job.block.BlockStorage;
import com.argusapm.android.core.job.block.BlockTable;
import com.argusapm.android.core.job.fileinfo.FileInfoStorage;
import com.argusapm.android.core.job.fileinfo.FileTable;
import com.argusapm.android.core.job.fps.FpsStorage;
import com.argusapm.android.core.job.fps.FpsTable;
import com.argusapm.android.core.job.func.FuncStorage;
import com.argusapm.android.core.job.func.FuncTable;
import com.argusapm.android.core.job.memory.MemStorage;
import com.argusapm.android.core.job.memory.MemoryTable;
import com.argusapm.android.core.job.net.NetStorage;
import com.argusapm.android.core.job.net.NetTable;
import com.argusapm.android.core.job.processinfo.ProcessInfoStorage;
import com.argusapm.android.core.job.processinfo.ProgessInfoTable;
import com.argusapm.android.core.job.watchDog.WatchDogInfoStorage;
import com.argusapm.android.core.job.watchDog.WatchDogInfoTable;
import com.argusapm.android.core.job.webview.WebStorage;
import com.argusapm.android.core.job.webview.WebTable;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.storage.ITable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块、任务名称等相关配置
 *
 * @author ArgusAPM Team
 */
public class ApmTask {
    public static final String APM_CONFIG_FILE = "argus_apm_sdk_config.json";
    /***************Argus APM 任务名称***************/
    public static final String TASK_ACTIVITY = "activity";//
    public static final String TASK_NET = "net";
    public static final String TASK_MEM = "memory";
    public static final String TASK_FPS = "fps";//
    public static final String TASK_APP_START = "appstart";//
    public static final String TASK_FILE_INFO = "fileinfo";//
    public static final String TASK_ANR = "anr";//
    public static final String TASK_PROCESS_INFO = "processinfo";//
    public static final String TASK_BLOCK = "block";
    public static final String TASK_WATCHDOG = "watchdog";
    public static final String TASK_FUNC = "func";
    public static final String TASK_WEBVIEW = "webview";

    /*******以下常量为进程启动时，各个模块是否生效的开关。多进程应用在初始化时需要进行配置*********/
    //本地数据清理开关
    public static final int FLAG_DATA_CLEAN = 0x00000001;
    //数据上传开关
    public static final int FLAG_DATA_UPLOAD = 0x00000002;
    //argusApm配置文件下发开关
    public static final int FLAG_CLOUD_UPDATE = 0x00000004;
    //本地Debug调试开关
    public static final int FLAG_LOCAL_DEBUG = 0x00000008;
    //以下为任务采集模块开关
    public static final int FLAG_COLLECT_FPS = 0x00000020;
    public static final int FLAG_COLLECT_APPSTART = 0x00000100;
    public static final int FLAG_COLLECT_ACTIVITY = 0x00000200;
    public static final int FLAG_COLLECT_MEM = 0x00000400;
    public static final int FLAG_COLLECT_NET = 0x00001000;
    public static final int FLAG_COLLECT_FILE_INFO = 0x00002000;
    public static final int FLAG_COLLECT_ANR = 0x00004000;
    public static final int FLAG_COLLECT_ACTIVITY_AOP = 0x00008000;
    public static final int FLAG_COLLECT_ACTIVITY_INSTRUMENTATION = 0x00010000;
    public static final int FLAG_COLLECT_PROCESS_INFO = 0x00020000;
    public static final int FLAG_COLLECT_FUNC = 0x00080000;
    public static final int FLAG_COLLECT_BLOCK = 0x00100000;
    public static final int FLAG_COLLECT_WEBVIEW = 0x00400000;
    public static final int FLAG_COLLECT_WATCHDOG = 0x00800000;

    //每个任务模块为key，模块静态开关为value的Map
    private static Map<String, Integer> sTaskMap;

    public static Map<String, Integer> getTaskMap() {
        if (sTaskMap == null) {
            sTaskMap = new HashMap<String, Integer>();
            sTaskMap.put(TASK_ACTIVITY, FLAG_COLLECT_ACTIVITY);
            sTaskMap.put(TASK_APP_START, FLAG_COLLECT_APPSTART);
            sTaskMap.put(TASK_FPS, FLAG_COLLECT_FPS);
            sTaskMap.put(TASK_MEM, FLAG_COLLECT_MEM);
            sTaskMap.put(TASK_NET, FLAG_COLLECT_NET);
            sTaskMap.put(TASK_FILE_INFO, FLAG_COLLECT_FILE_INFO);
            sTaskMap.put(TASK_ANR, FLAG_COLLECT_ANR);
            sTaskMap.put(TASK_PROCESS_INFO, FLAG_COLLECT_PROCESS_INFO);
            sTaskMap.put(TASK_FUNC, FLAG_COLLECT_FUNC);
            sTaskMap.put(TASK_BLOCK, FLAG_COLLECT_BLOCK);
            sTaskMap.put(TASK_WEBVIEW, FLAG_COLLECT_WEBVIEW);
            sTaskMap.put(TASK_WATCHDOG, FLAG_COLLECT_WATCHDOG);
        }
        return sTaskMap;
    }

    /**
     * Argus APM  数据库查询表名称
     */
    public static final String[] sTableNameList = {
            ApmTask.TASK_FPS,
            ApmTask.TASK_MEM,
            ApmTask.TASK_ACTIVITY,
            ApmTask.TASK_NET,
            ApmTask.TASK_APP_START,
            ApmTask.TASK_FILE_INFO,
            ApmTask.TASK_PROCESS_INFO,
            ApmTask.TASK_FUNC,
            ApmTask.TASK_BLOCK,
            ApmTask.TASK_WEBVIEW,
            ApmTask.TASK_WATCHDOG
    };
    /**
     * 数据库查询表Table对象列表
     */
    public static ITable[] sTableList = {
            new FpsTable(),
            new MemoryTable(),
            new ActivityTable(),
            new NetTable(),
            new AppStartTable(),
            new FileTable(),
            new ProgessInfoTable(),
            new FuncTable(),
            new BlockTable(),
            new WebTable(),
            new WatchDogInfoTable()
    };
    /**
     * 数据库查询表Storage对象列表
     */
    public static List<IStorage> sAllStorage = Arrays.<IStorage>asList(
            new NetStorage(),
            new ActivityStorage(),
            new MemStorage(),
            new FpsStorage(),
            new AppStartStorage(),
            new FileInfoStorage(),
            new ProcessInfoStorage(),
            new FuncStorage(),
            new BlockStorage(),
            new WebStorage(),
            new WatchDogInfoStorage());
}