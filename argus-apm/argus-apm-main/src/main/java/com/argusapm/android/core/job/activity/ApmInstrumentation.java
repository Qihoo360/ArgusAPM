package com.argusapm.android.core.job.activity;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;
import com.argusapm.android.utils.LogX;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 对activity启动各个阶段的消耗的时间进行监控
 *
 * @author ArgusAPM Team
 */
public class ApmInstrumentation extends Instrumentation {
    private static final String SUB_TAG = "traceactivity";

    private Instrumentation mOldInstrumentation = null;

    public ApmInstrumentation(Instrumentation oldInstrumentation) {
        if (oldInstrumentation instanceof Instrumentation) {
            mOldInstrumentation = oldInstrumentation;
        }
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        ActivityCore.appAttachTime = System.currentTimeMillis();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callApplicationOnCreate time" + ActivityCore.appAttachTime);
        }
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callApplicationOnCreate(app);
        } else {
            super.callApplicationOnCreate(app);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (!isActivityTaskRunning()) {
            if (mOldInstrumentation != null) {
                mOldInstrumentation.callActivityOnCreate(activity, icicle);
            } else {
                super.callActivityOnCreate(activity, icicle);
            }
            return;
        }
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callActivityOnCreate");
        }
        long startTime = System.currentTimeMillis();
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callActivityOnCreate(activity, icicle);
        } else {
            super.callActivityOnCreate(activity, icicle);
        }
        ActivityCore.startType = ActivityCore.isFirst ? ActivityInfo.COLD_START : ActivityInfo.HOT_START;

        ActivityCore.onCreateInfo(activity, startTime);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        if (!isActivityTaskRunning()) {
            if (mOldInstrumentation != null) {
                mOldInstrumentation.callActivityOnStart(activity);
            } else {
                super.callActivityOnStart(activity);
            }
            return;
        }
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callActivityOnStart: ");
        }
        long startTime = System.currentTimeMillis();
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callActivityOnStart(activity);
        } else {
            super.callActivityOnStart(activity);
        }
        ActivityCore.saveActivityInfo(activity, ActivityInfo.HOT_START, System.currentTimeMillis() - startTime, ActivityInfo.TYPE_START);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        if (!isActivityTaskRunning()) {
            if (mOldInstrumentation != null) {
                mOldInstrumentation.callActivityOnResume(activity);
            } else {
                super.callActivityOnResume(activity);
            }
            return;
        }
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callActivityOnResume: ");
        }

        long startTime = System.currentTimeMillis();
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callActivityOnResume(activity);
        } else {
            super.callActivityOnResume(activity);
        }

        //记录数据
        ActivityCore.saveActivityInfo(activity, ActivityInfo.HOT_START, System.currentTimeMillis() - startTime, ActivityInfo.TYPE_RESUME);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        if (!isActivityTaskRunning()) {
            if (mOldInstrumentation != null) {
                mOldInstrumentation.callActivityOnStop(activity);
            } else {
                super.callActivityOnStop(activity);
            }
            return;
        }
        long startTime = System.currentTimeMillis();
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callActivityOnStop(activity);
        } else {
            super.callActivityOnStop(activity);
        }
        //记录数据
        ActivityCore.saveActivityInfo(activity, ActivityInfo.HOT_START,
                System.currentTimeMillis() - startTime,
                ActivityInfo.TYPE_STOP);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callActivityOnStop: ");
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        if (!isActivityTaskRunning()) {
            if (mOldInstrumentation != null) {
                mOldInstrumentation.callActivityOnPause(activity);
            } else {
                super.callActivityOnPause(activity);
            }
            return;
        }
        long startTime = System.currentTimeMillis();
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callActivityOnPause(activity);
        } else {
            super.callActivityOnPause(activity);
        }
        //记录数据
        ActivityCore.saveActivityInfo(activity, ActivityInfo.HOT_START,
                System.currentTimeMillis() - startTime,
                ActivityInfo.TYPE_PAUSE);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callActivityOnPause: ");
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        if (!isActivityTaskRunning()) {
            if (mOldInstrumentation != null) {
                mOldInstrumentation.callActivityOnDestroy(activity);
            } else {
                super.callActivityOnDestroy(activity);
            }
            return;
        }
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "callActivityOnDestroy: ");
        }
        long startTime = System.currentTimeMillis();
        if (mOldInstrumentation != null) {
            mOldInstrumentation.callActivityOnDestroy(activity);
        } else {
            super.callActivityOnDestroy(activity);
        }
        //记录数据
        ActivityCore.saveActivityInfo(activity, ActivityInfo.HOT_START, System.currentTimeMillis() - startTime, ActivityInfo.TYPE_DESTROY);
    }

    private boolean isActivityTaskRunning() {
        boolean useInstrumation = Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_COLLECT_ACTIVITY_INSTRUMENTATION);
        return useInstrumation && ActivityCore.isActivityTaskRunning();
    }
}