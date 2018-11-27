package com.argusapm.android.core.job.activity;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;
import com.argusapm.android.utils.LogX;

/**
 * 用于Activity的AOP方案
 *
 * @author ArgusAPM Team
 */
public class AH {
    public static final String SUB_TAG = "traceactivity";

    public static void invoke(Activity activity, long startTime, String lifeCycle, Object... extars) {
        boolean isRunning = isActivityTaskRunning();
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, lifeCycle + " isRunning : " + isRunning);
        }
        if (!isRunning) {
            return;
        }

        if (TextUtils.equals(lifeCycle, ActivityInfo.TYPE_STR_ONCREATE)) {
            ActivityCore.onCreateInfo(activity, startTime);
        } else {
            int lc = ActivityInfo.ofLifeCycleString(lifeCycle);
            if (lc <= ActivityInfo.TYPE_UNKNOWN || lc > ActivityInfo.TYPE_DESTROY) {
                return;
            }
            ActivityCore.saveActivityInfo(activity, ActivityInfo.HOT_START, System.currentTimeMillis() - startTime, lc);
        }
    }

    public static void applicationAttachBaseContext(Context context) {
        ActivityCore.appAttachTime = System.currentTimeMillis();
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "applicationAttachBaseContext time : " + ActivityCore.appAttachTime);
        }
    }

    public static void applicationOnCreate(Context context) {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "applicationOnCreate");
        }

    }

    public static boolean isActivityTaskRunning() {
        boolean useInstrumation = Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_COLLECT_ACTIVITY_AOP);
        return useInstrumation && ActivityCore.isActivityTaskRunning();
    }


}
