package com.argusapm.android.cloudconfig.data;

import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.Constant;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.StorageConfig;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.network.UploadConfig;
import com.argusapm.android.utils.LogX;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Argus Apm 控制数据
 *
 * @author ArgusAPM Team
 */
public class ArgusApmConfigData {
    public static final String SUB_TAG = "ArgusApmConfigData";
    public long uploadInterval = UploadConfig.UPLOAD_INTERVAL;
    public int cleanExp = StorageConfig.DATA_OVER_DAY; // 以天为单位
    public long cleanInterval = StorageConfig.DATA_CLEAR_INTERVAL_TIME;
    public long cloudInterval = Constant.INTERVAL;// 云控更新的时间间隔
    public boolean debug = false;// 是否开启线上debug开关，但是不开启悬浮窗
    public long pauseInterval = TaskConfig.DEFAULT_PAUSE_INTERVAL; //高频采集点，休息间隔
    public int onceMaxCount = TaskConfig.DEFAULT_ONCE_MAX_COUNT; //单次最多采集条数
    public int controlActivity = TaskConfig.ACTIVITY_TYPE_NONE; // 默认云端不控制activity的收集方案。0.不控制；1.使用instrumentation；2.使用aop方式
    public long randomControlTime = TaskConfig.RANDOM_CONTROL_TIME; //云控随机请求的时间
    public ArgusApmConfigCore configCore = new ArgusApmConfigCore();
    public ArgusApmConfigFuncControl funcControl = new ArgusApmConfigFuncControl();
    public List<String> anrFilter = new ArrayList<String>();
    public List<String> fileDataDirs = new ArrayList<String>();
    public List<String> fileSdDirs = new ArrayList<String>();

    public void parseData(String config) {
        if (TextUtils.isEmpty(config)) {
            return;
        }
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "parseData : " + config);
        }
        try {
            JSONObject root = new JSONObject(config);
            if (root.has("g_core")) {
                JSONObject g_core = root.getJSONObject("g_core");
                parseCore(g_core);
            }
            if (root.has("func_control")) {
                JSONObject func_control = root.getJSONObject("func_control");
                parseFuncControl(func_control);
            }
            if (root.has("upload_interval")) {
                long upload_interval = root.getLong("upload_interval");
                uploadInterval = Math.max(upload_interval, UploadConfig.UPLOAD_MIN_INTERVAL);
            }
            if (root.has("clean_exp")) {
                int clean_exp = root.getInt("clean_exp");
                if (clean_exp > 0) {
                    cleanExp = clean_exp;
                }
            }
            if (root.has("clean_interval")) {
                long clean_interval = root.getLong("clean_interval");
                cleanInterval = Math.max(clean_interval, StorageConfig.DATA_CLEAR_INTERVAL_MIN_TIME);
            }
            if (root.has("pause_interval")) {
                pauseInterval = root.getLong("pause_interval");
            }
            if (root.has("once_max_count")) {
                onceMaxCount = root.getInt("once_max_count");
            }
            if (root.has("cloud_interval")) {
                long cloud_interval = root.getLong("cloud_interval");
                cloudInterval = Math.max(cloud_interval, Constant.CLOUD_MIN_INTERVAL);
            }
            if (root.has("debug")) {
                debug = root.getBoolean("debug");
            }
            if (root.has("control_activity")) {
                controlActivity = root.getInt("control_activity");
            }
            if (root.has("random_control_time")) {
                randomControlTime = root.getLong("random_control_time");
            }
            if (root.has("anr_filter")) {
                String anr_filter = root.getString("anr_filter");
                List<String> filters = new ArrayList<String>();
                String[] pkgs = anr_filter.split(",");
                if (pkgs != null && pkgs.length > 0) {
                    for (String pkg : pkgs) {
                        filters.add(pkg.trim());
                    }
                }
                anrFilter = filters;
            }
            if (root.has("file_data_dirs")) {
                String file_data_dirs = root.getString("file_data_dirs");
                List<String> list = new ArrayList<String>();
                String[] arr = file_data_dirs.split(",");
                if (arr != null && arr.length > 0) {
                    for (String item : arr) {
                        list.add(item.trim());
                    }
                }
                fileDataDirs = list;
            }
            if (root.has("file_sd_dirs")) {
                String file_sd_dirs = root.getString("file_sd_dirs");
                List<String> list = new ArrayList<String>();
                String[] arr = file_sd_dirs.split(",");
                if (arr != null && arr.length > 0) {
                    for (String item : arr) {
                        list.add(item.trim());
                    }
                }
                fileSdDirs = list;
            }
        } catch (Exception e) {
            LogX.d(Env.TAG, SUB_TAG, "parseData ex: " + e);
        }
    }

    private void parseFuncControl(JSONObject func_control) throws JSONException {
        ArgusApmConfigFuncControl fc = new ArgusApmConfigFuncControl();
        if (func_control.has("onreceive_min_time")) {
            fc.onreceiveMinTime = func_control.getLong("onreceive_min_time");
        }
        if (func_control.has("thread_min_time")) {
            fc.threadMinTime = func_control.getLong("thread_min_time");
        }
        if (func_control.has("io_min_time")) {
            fc.ioMinTime = func_control.getLong("io_min_time");
        }
        if (func_control.has("min_file_size")) {
            fc.minFileSize = func_control.getLong("min_file_size");
        }
        if (func_control.has("activity_first_min_time")) {
            fc.activityFirstMinTime = func_control.getLong("activity_first_min_time");
        }
        if (func_control.has("activity_lifecycle_min_time")) {
            fc.activityLifecycleMinTime = func_control.getLong("activity_lifecycle_min_time");
        }
        if (func_control.has("block_min_time")) {
            fc.blockMinTime = func_control.getInt("block_min_time");
        }
        if (func_control.has("memory_delay_time")) {
            fc.setMemoryDelayTime(func_control.getInt("memory_delay_time"));
        }
        if (func_control.has("memory_interval_time")) {
            fc.setMemoryIntervalTime(func_control.getInt("memory_interval_time"));
        }
        if (func_control.has("anr_interval_time")) {
            fc.setAnrIntervalTime(func_control.getInt("anr_interval_time"));
        }
        if (func_control.has("file_dir_depth")) {
            fc.setFileDepth(func_control.getInt("file_dir_depth"));
        }

        if (func_control.has("thread_cnt_delay_time")) {
            fc.setThreadCntDelayTime(func_control.getInt("thread_cnt_delay_time"));
        }
        if (func_control.has("thread_cnt_interval_time")) {
            fc.setThreadCntIntervalTime(func_control.getInt("thread_cnt_interval_time"));
        }

        if (func_control.has("watchdog_delay_time")) {
            fc.setWatchDogDelayTime(func_control.getInt("watchdog_delay_time"));
        }
        if (func_control.has("watchdog_interval_time")) {
            fc.setWatchDogIntervalTime(func_control.getInt("watchdog_interval_time"));
        }

        if (func_control.has("watchdog_min_time")) {
            fc.setWatchDogMinTime(func_control.getInt("watchdog_min_time"));
        }
        funcControl = fc;
    }

    private void parseCore(JSONObject g_core) throws JSONException {
        Collection<String> samples = ApmTask.getTaskMap().keySet();
        Map<String, Integer> map = ApmTask.getTaskMap();
        ArgusApmConfigCore argusApmConfigCore = new ArgusApmConfigCore();
        for (String sample : samples) {
            boolean state = false;
            if (g_core.has(sample)) {
                state = g_core.getBoolean(sample);
            }
            Integer obj = map.get(sample);
            if (obj == null) {
                continue;
            }
            int taskFlag = obj.intValue();
            if (state) {
                argusApmConfigCore.setEnabled(taskFlag);
            } else {
                argusApmConfigCore.setDisabled(taskFlag);
            }
            Manager.getInstance().getTaskManager().updateTaskSwitchByTaskName(sample, state); //更新任务开关
        }
        if (g_core.has("exp")) {
            argusApmConfigCore.setExp(g_core.getLong("exp"));
        }
        configCore = argusApmConfigCore;
    }
}
