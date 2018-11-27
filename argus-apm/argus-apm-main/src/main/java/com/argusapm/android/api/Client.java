package com.argusapm.android.api;

import android.content.Context;

import com.argusapm.android.Env;
import com.argusapm.android.core.Config;
import com.argusapm.android.core.Manager;
import com.argusapm.android.debug.AnalyzeManager;
import com.argusapm.android.network.DebugConfig;
import com.argusapm.android.utils.LogX;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;
import static com.argusapm.android.Env.TAG_O;


/**
 * ArgusAPM外部调用接口（包含配置、初始化等调用）
 *
 * @author ArgusAPM Team
 */
public class Client {

    private static final String SUB_TAG = "Client";
    private static volatile boolean sIsStart = false; //防止重复初始化
    private static volatile boolean sIsAttached = false; //防止重复初始化

    /**
     * ArgusAPM初始化配置
     *
     * @param config
     */
    public static synchronized void attach(Config config) {
        if (sIsAttached) {
            LogX.o(TAG_O, SUB_TAG, "attach argus.apm.version(" + Env.getVersionName() + ") already attached");
            return;
        }
        sIsAttached = true;
        LogX.o(TAG_O, SUB_TAG, "attach argus.apm.version(" + Env.getVersionName() + ")");
        Manager.getInstance().setConfig(config);
        Manager.getInstance().init();
        setNetWorkDebugConfig();
    }

    private static void setNetWorkDebugConfig(){
        DebugConfig.DEBUG = Env.DEBUG;
        DebugConfig.TAG = Env.TAG;
        DebugConfig.TAG_O = Env.TAG_O;
    }

    /**
     * 启动ArgusAPM任务
     */
    public static synchronized void startWork() {
        if (sIsStart) {
            LogX.o(TAG_O, SUB_TAG, "attach argus.apm.version(" + Env.getVersionName() + ") already started");
            return;
        }
        LogX.o(TAG_O, SUB_TAG, "startwork");
        sIsStart = true;
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "APM开始任务:startWork");
        }
        Manager.getInstance().startWork();
    }

    public static void isDebugOpen(boolean isOpen) {
        AnalyzeManager.getInstance().setShowFloatWin(isOpen, "");
    }

    /**
     * 设置Debug模式是否开启，以及debug悬浮窗在哪个进程展示（供开发者使用）
     *
     * @param isOpen              是否开启
     * @param floatwinProcessName 悬浮窗所在进程
     */
    public static void isDebugOpen(boolean isOpen, String floatwinProcessName) {
        AnalyzeManager.getInstance().setShowFloatWin(isOpen, floatwinProcessName);
    }

    /**
     * 判断某个Task是否在工作
     *
     * @param taskName
     * @return
     */
    public static boolean isTaskRunning(String taskName) {
        return Manager.getInstance().getTaskManager().taskIsCanWork(taskName);
    }


    public static Context getContext() {
        return Manager.getContext();
    }
}