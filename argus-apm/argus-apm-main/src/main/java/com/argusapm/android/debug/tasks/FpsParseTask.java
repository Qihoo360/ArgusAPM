package com.argusapm.android.debug.tasks;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.job.fps.FpsInfo;
import com.argusapm.android.debug.config.DebugConfig;
import com.argusapm.android.debug.output.OutputProxy;
import com.argusapm.android.debug.utils.DebugFloatWindowUtls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 分析帧率数据
 *
 * @author ArgusAPM Team
 */
public class FpsParseTask implements IParser {
    private JSONObject updateInfo(IInfo info, String taskName) {
        if (info == null || TextUtils.isEmpty(taskName)) {
            return null;
        }

        JSONObject json = null;
        try {
            json = info.toJson();
            if (json != null) {
                json.put("taskName", taskName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public boolean parse(IInfo info) {
        if (info != null) {
            if (info instanceof FpsInfo) {
                FpsInfo fInfo = (FpsInfo) info;
                int fps = fInfo.getFps();
                // fps次数越接近40，用户能感知到卡顿
                // 20以下卡顿更明显
                // 收集40以下的分析
                if (fps < DebugConfig.WARN_FPS_VALUE) {
                    JSONObject obj = updateInfo(fInfo, ApmTask.TASK_FPS);
                    OutputProxy.output(fInfo.toString(), obj.toString());
                }
                DebugFloatWindowUtls.sendBroadcast(fInfo);
            } else {
                OutputProxy.output("what");
            }
        } else {
            OutputProxy.output("info == null");
        }
        return false;
    }
}
