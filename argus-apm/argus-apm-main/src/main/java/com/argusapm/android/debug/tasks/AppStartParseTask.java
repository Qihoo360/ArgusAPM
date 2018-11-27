package com.argusapm.android.debug.tasks;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.job.appstart.AppStartInfo;
import com.argusapm.android.debug.output.OutputProxy;
import com.argusapm.android.debug.utils.DebugFloatWindowUtls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Debug模块 应用启动时间分析类
 *
 * @author ArgusAPM Team
 */
public class AppStartParseTask implements IParser {
    /**
     * app启动
     *
     * @param info
     */
    @Override
    public boolean parse(IInfo info) {
        if (info instanceof AppStartInfo) {
            AppStartInfo aInfo = (AppStartInfo) info;
            if (aInfo == null) {
                return false;
            }
            try {
                JSONObject obj = aInfo.toJson();
                obj.put("taskName", ApmTask.TASK_APP_START);
                OutputProxy.output("启动时间:" + aInfo.getStartTime(), obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DebugFloatWindowUtls.sendBroadcast(aInfo);
        }
        return true;
    }
}
