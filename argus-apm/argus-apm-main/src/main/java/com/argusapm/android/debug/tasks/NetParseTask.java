package com.argusapm.android.debug.tasks;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.debug.output.OutputProxy;
import com.argusapm.android.debug.utils.DebugFloatWindowUtls;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 网络分析类
 *
 * @author ArgusAPM Team
 */
public class NetParseTask implements IParser {
    @Override
    public boolean parse(IInfo info) {
        if (info != null && info instanceof NetInfo) {
            NetInfo aInfo = (NetInfo) info;
            if (aInfo.statusCode != 200) {
                String msg = String.format("网络错误，状态码:", aInfo.statusCode);
                DebugFloatWindowUtls.sendBroadcast(aInfo);
                try {
                    JSONObject obj = aInfo.toJson();
                    obj.put("taskName", ApmTask.TASK_NET);
                    OutputProxy.output(msg, obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
