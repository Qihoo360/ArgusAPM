package com.argusapm.android.core.tasks;

import android.os.Build;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.core.job.activity.ActivityTask;
import com.argusapm.android.core.job.activity.InstrumentationHooker;
import com.argusapm.android.core.job.anr.AnrLoopTask;
import com.argusapm.android.core.job.appstart.AppStartTask;
import com.argusapm.android.core.job.block.BlockTask;
import com.argusapm.android.core.job.fileinfo.FileInfoTask;
import com.argusapm.android.core.job.fps.FpsTask;
import com.argusapm.android.core.job.memory.MemoryTask;
import com.argusapm.android.core.job.net.NetTask;
import com.argusapm.android.core.job.processinfo.ProcessInfoTask;
import com.argusapm.android.core.job.watchDog.WatchDogTask;
import com.argusapm.android.utils.LogX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.argusapm.android.Env.DEBUG;

/**
 * ArgusAPM任务管理类
 *
 * @author ArgusAPM Team
 */
public class TaskManager {
    private final String SUB_TAG = "TaskManager";
    private static TaskManager mInstance;
    private Map<String, ITask> taskMap;

    public static TaskManager getInstance() {
        if (null == mInstance) {
            synchronized (TaskManager.class) {
                if (null == mInstance) {
                    mInstance = new TaskManager();
                }
            }
        }
        return mInstance;
    }

    public TaskManager() {
        taskMap = new HashMap<String, ITask>();
    }

    public void onDestroy() {
        if (null != taskMap) {
            taskMap.clear();
            taskMap = null;
        }
    }

    /**
     * 获取所有任务
     *
     * @return
     */
    public List<ITask> getAllTask() {
        List<ITask> taskList = new ArrayList<ITask>();
        if (null == taskMap) return taskList;
        for (Map.Entry<String, ITask> entry : taskMap.entrySet()) {
            taskList.add(entry.getValue());
        }
        return taskList;
    }

    /**
     * 通过任务名称获取任务
     *
     * @param taskName
     * @return
     */
    public ITask getTask(String taskName) {
        if (TextUtils.isEmpty(taskName)) {
            return null;
        }
        return null == taskMap ? null : taskMap.get(taskName);
    }

    /**
     * 通过任务名称更新任务开关
     */
    public void updateTaskSwitchByTaskName(String taskName, boolean value) {
        if (TextUtils.isEmpty(taskName) || (taskMap.get(taskName) == null)) {
            return;
        }
        if (Manager.getInstance().getConfig().isEnabled(ApmTask.getTaskMap().get(taskName)) && value) {
            taskMap.get(taskName).setCanWork(true);
        } else {
            taskMap.get(taskName).setCanWork(false);
        }
    }

    /**
     * 获取任务是否可以工作
     */
    public boolean taskIsCanWork(String taskName) {
        synchronized (this) {
            if (TextUtils.isEmpty(taskName)) {
                return false;
            }
            if (taskMap.get(taskName) == null) {
                return false;
            }
            return taskMap.get(taskName).isCanWork();
        }
    }

    /**
     * 获取apm是否可以工作
     */
    public boolean isApmEnable() {
        if (taskMap == null) {
            LogX.d(Env.TAG, SUB_TAG, "taskMap is null ");
            return false;
        }
        for (ITask task : taskMap.values()) {
            if (task.isCanWork()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开始任务
     */
    public void startWorkTasks() {
        if (taskMap == null) {
            LogX.d(Env.TAG, SUB_TAG, "taskMap is null ");
            return;
        }
        if (taskMap.get(ApmTask.TASK_ACTIVITY).isCanWork()) {
            // 云控为TaskConfig.ACTIVITY_TYPE_NONE，则本地开关优先
            int type = ArgusApmConfigManager.getInstance().getArgusApmConfigData().controlActivity;
            if (type == TaskConfig.ACTIVITY_TYPE_NONE) {
                if (Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_COLLECT_ACTIVITY_INSTRUMENTATION)) {
                    LogX.o("activity local INSTRUMENTATION");
                    InstrumentationHooker.doHook();
                } else {
                    LogX.o("activity local aop");
                }
            } else if (type == TaskConfig.ACTIVITY_TYPE_INSTRUMENTATION) {
                LogX.o("activity cloud INSTRUMENTATION");
                InstrumentationHooker.doHook();
            } else {
                LogX.o("activity cloud type(" + type + ")");
            }

        }
        List<ITask> taskList = getAllTask();
        for (ITask task : taskList) {
            if (!task.isCanWork()) {
                continue;
            }
            if (DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "start task " + task.getTaskName());
            }
            task.start();
        }
    }

    public void stopWorkTasks() {
        List<ITask> taskList = getAllTask();
        for (ITask task : taskList) {
            if (DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "stop task " + task.getTaskName());
            }
            task.stop();
        }
    }

    /**
     * 注册 task:每添加一个task都要进行注册
     */
    public void registerTask() {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, "TaskManager", "registerTask " + getClass().getClassLoader());
        }
        if (Build.VERSION.SDK_INT >= 16) {
            taskMap.put(ApmTask.TASK_FPS, new FpsTask());
        }
        taskMap.put(ApmTask.TASK_MEM, new MemoryTask());
        taskMap.put(ApmTask.TASK_ACTIVITY, new ActivityTask());
        taskMap.put(ApmTask.TASK_NET, new NetTask());
        taskMap.put(ApmTask.TASK_APP_START, new AppStartTask());
        taskMap.put(ApmTask.TASK_ANR, new AnrLoopTask(Manager.getContext()));
        taskMap.put(ApmTask.TASK_FILE_INFO, new FileInfoTask());
        taskMap.put(ApmTask.TASK_PROCESS_INFO, new ProcessInfoTask());
        taskMap.put(ApmTask.TASK_BLOCK, new BlockTask());
        taskMap.put(ApmTask.TASK_WATCHDOG, new WatchDogTask());
    }
}