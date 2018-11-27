package com.argusapm.android.core;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cleaner.DataCleaner;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.tasks.ITask;
import com.argusapm.android.core.tasks.TaskManager;
import com.argusapm.android.debug.AnalyzeManager;
import com.argusapm.android.network.UploadManager;
import com.argusapm.android.utils.FileUtils;
import com.argusapm.android.utils.LogX;

import java.util.List;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;
import static com.argusapm.android.Env.TAG_O;

/**
 * ArgusAPM管理类
 *
 * @author ArgusAPM Team
 */
public class Manager {
    private final String SUB_TAG = "Manager";
    private static Manager instance;
    private boolean mWorkFlag = false; //标志位，APM是否在工作
    private Config mConfig; // 统一管理apm的配置参数：属于固定配置
    private DataCleaner mDataCleaner; // 控制数据清理逻辑
    private ReceiverManager mReceiverManager;

    private Manager() {
    }

    public static Manager getInstance() {
        if (null == instance) {
            synchronized (Manager.class) {
                if (null == instance) {
                    instance = new Manager();
                }
            }
        }
        return instance;
    }

    public void init() {
        TaskManager.getInstance().registerTask();
        // 这部分只与本地配置有关，云控即使更改，也需要执行这部分
        initDataConfig();
        if (mReceiverManager == null) {
            mReceiverManager = new ReceiverManager();
        }
        mReceiverManager.init(Manager.getContext());
    }

    /**
     * apm开始工作
     * 必须在主线程调用
     */
    public void startWork() {
        if (mWorkFlag) {
            return;
        }
        mWorkFlag = true;
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, " startWork");
        }
        if (mConfig == null) {
            throw new NullPointerException("mConfig == null, please call method of Client.attach(Config config)");
        }
        if (!TaskManager.getInstance().isApmEnable()) {
            LogX.o(TAG_O, SUB_TAG, "startWork ：apm.disable");
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("startWork is must run in MainThread");
        }
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "是否是主线程: " + (Looper.myLooper() == Looper.getMainLooper()));
        }
        AnalyzeManager.getInstance().setIsDebugMode(ArgusApmConfigManager.getInstance().getArgusApmConfigData().debug);
        //开始任务
        TaskManager.getInstance().startWorkTasks();
    }

    public void stopWork() {
        TaskManager.getInstance().stopWorkTasks();
    }

    private void initDataConfig() {
        ArgusApmConfigManager.getInstance().initArgusApmData(mConfig.appContext, mConfig.mRuleRequest);
        // 主要针对多进程的场景
        // 初始化时候确定是否执行数据清理逻辑
        if (mConfig.isEnabled(ApmTask.FLAG_DATA_CLEAN)) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "DataCleaner create");
            }
            mDataCleaner = new DataCleaner(mConfig.appContext);
            mDataCleaner.create();
        }
        // 主要针对多进程的场景
        if (mConfig.isEnabled(ApmTask.FLAG_DATA_UPLOAD)) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "UploadManager init");
            }
            UploadManager.getInstance().init(Manager.getContext(), mConfig.mCollectDataUpload);
        }
    }

    public void setTaskCanNotWork() {
        if (!TaskManager.getInstance().isApmEnable()) {
            return;
        }
        List<ITask> taskList = getTaskManager().getAllTask();
        for (ITask task : taskList) {
            task.setCanWork(false);
        }
    }

    /**
     * 重新加载apm配置文件，更新apm开关
     */
    public void reload() {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "start reloadConfig");
        }
        LogX.o(TAG_O, SUB_TAG, "start reloadConfig");
        mWorkFlag = false;
        stopWork();
        ArgusApmConfigManager.getInstance().initLocalData();
        startWork();
    }

    public TaskManager getTaskManager() {
        return TaskManager.getInstance();
    }

    public Config getConfig() {
        return mConfig;
    }

    public ArgusApmConfigManager getCloudConfig() {
        return ArgusApmConfigManager.getInstance();
    }

//    public ActivityCounter getActivityCounter() {
//        return mActivityCounter;
//    }

    /**
     * 设置配置参数
     *
     * @param mConfig
     */
    public void setConfig(Config mConfig) {
        this.mConfig = mConfig;
    }

    public boolean hasInit() {
        return mWorkFlag;
    }

    public static Context getContext() {
        Config config = getInstance().getConfig();
        return config != null ? config.appContext : null;
    }

    /**
     * 获取sd卡apm根路径
     *
     * @return
     */
    public String getBasePath() {
        if (TextUtils.isEmpty(FileUtils.getSDPath())) {
            return "";
        }
        return FileUtils.getSDPath() + TaskConfig.BASE_DIR_PATH;
    }
}
