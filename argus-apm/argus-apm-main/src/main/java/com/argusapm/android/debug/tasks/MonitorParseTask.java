package com.argusapm.android.debug.tasks;

import com.argusapm.android.core.IInfo;

/**
 * @author ArgusAPM Team
 */
public class MonitorParseTask implements IParser {
    @Override
    public boolean parse(IInfo info) {
//        if (info != null && info instanceof MonitorInfo) {
//            MonitorInfo mInfo = (MonitorInfo) info;
//            try {
//                JSONObject obj = mInfo.toJson();
//                obj.put("taskName", ApmTask.TASK_NET);
//                OutputProxy.output(msg, obj.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return false;
    }
}
