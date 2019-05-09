package com.argusapm.android.core.job.webview;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.tasks.ITask;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ArgusAPM Team
 */
public class WebTrace {
    public static final String SUB_TAG = "traceweb";

    public static void dispatch(JSONObject jsonObject) {
        if (!Manager.getInstance().getTaskManager().taskIsCanWork(ApmTask.TASK_WEBVIEW)) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "webview task is not work");
            }
            return;
        }
        ITask task = Manager.getInstance().getTaskManager().getTask(ApmTask.TASK_WEBVIEW);
        if (task != null && jsonObject != null) {
            try {
                WebInfo webInfo = new WebInfo();
                webInfo.url = jsonObject.getString(WebInfo.DBKey.KEY_URL);
                webInfo.isWifi = SystemUtils.isWifiConnected();
                webInfo.navigationStart = jsonObject.getLong(WebInfo.DBKey.KEY_NAVIGATION_START);
                webInfo.responseStart = jsonObject.getLong(WebInfo.DBKey.KEY_RESPONSE_START);
                webInfo.pageTime = jsonObject.getLong(WebInfo.DBKey.KEY_PAGE_TIME);
                task.save(webInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
