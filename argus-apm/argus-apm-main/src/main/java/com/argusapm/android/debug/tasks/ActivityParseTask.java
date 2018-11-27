package com.argusapm.android.debug.tasks;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.job.activity.ActivityInfo;
import com.argusapm.android.debug.config.DebugConfig;
import com.argusapm.android.debug.output.OutputProxy;
import com.argusapm.android.debug.utils.DebugFloatWindowUtls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Debug模块 Activity分析类
 *
 * @author ArgusAPM Team
 */
public class ActivityParseTask implements IParser {
    /**
     * 生命周期所用时间
     *
     * @param info
     */
    @Override
    public boolean parse(IInfo info) {
        if (info instanceof ActivityInfo) {
            ActivityInfo aInfo = (ActivityInfo) info;
            if (aInfo == null) {
                return false;
            }
            if (aInfo.lifeCycle == ActivityInfo.TYPE_FIRST_FRAME) {
                saveWarningInfo(aInfo, DebugConfig.WARN_ACTIVITY_FRAME_VALUE);
                DebugFloatWindowUtls.sendBroadcast(aInfo);
            } else if (aInfo.lifeCycle == ActivityInfo.TYPE_CREATE) {
                saveWarningInfo(aInfo, DebugConfig.WARN_ACTIVITY_CREATE_VALUE);
                DebugFloatWindowUtls.sendBroadcast(aInfo);
            } else if (aInfo.lifeCycle == ActivityInfo.TYPE_RESUME) {
                saveWarningInfo(aInfo, DebugConfig.WARN_ACTIVITY_CREATE_VALUE);
                DebugFloatWindowUtls.sendBroadcast(aInfo);
            } else {
                saveWarningInfo(aInfo, DebugConfig.WARN_ACTIVITY_CREATE_VALUE);
            }
        }
        return true;
    }

    private void saveWarningInfo(ActivityInfo aInfo, int warningTime) {
        if (aInfo.time < warningTime) {
            return;
        }
        try {
            JSONObject obj = aInfo.toJson();
            obj.put("taskName", ApmTask.TASK_ACTIVITY);
            OutputProxy.output("LifeCycle:" + aInfo.getLifeCycleString() + ",cost time:" + aInfo.time, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
