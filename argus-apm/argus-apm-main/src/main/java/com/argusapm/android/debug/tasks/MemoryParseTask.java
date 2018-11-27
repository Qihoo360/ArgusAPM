package com.argusapm.android.debug.tasks;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.job.memory.MemoryInfo;
import com.argusapm.android.debug.output.OutputProxy;
import com.argusapm.android.debug.utils.DebugFloatWindowUtls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * debug模式内存task
 *
 * @author ArgusAPM Team
 */
public class MemoryParseTask implements IParser {
    @Override
    public boolean parse(IInfo info) {
        if (info instanceof MemoryInfo) {
            MemoryInfo aInfo = (MemoryInfo) info;
            if (aInfo == null) {
                return false;
            }
            try {
                JSONObject obj = aInfo.toJson();
                obj.put("taskName", ApmTask.TASK_MEM);
                OutputProxy.output("", obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DebugFloatWindowUtls.sendBroadcast(aInfo);
        }
        return true;
    }
}
