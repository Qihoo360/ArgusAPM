package com.argusapm.android.debug.utils;

import android.content.Intent;
import android.os.Bundle;

import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.debug.view.FloatWindowManager;
import com.argusapm.android.utils.ProcessUtils;

import java.util.Set;

/**
 * Debug模式，悬浮窗工具
 *
 * @author ArgusAPM Team
 */
public class DebugFloatWindowUtls {
    /**
     * 发送Broadcast消息
     *
     * @param aInfo
     */
    public static void sendBroadcast(IInfo aInfo) {
        sendBroadcast(aInfo, "");
    }

    /**
     * 发送Broadcast消息
     *
     * @param aInfo
     */
    public static void sendBroadcast(IInfo aInfo, String showText) {
        Intent intent = new Intent();
        intent.setAction(Manager.getContext().getPackageName() + FloatWindowManager.SUB_FLOAT_WIN_RECEIVER_ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", aInfo);
        bundle.putString("processName", ProcessUtils.getCurrentProcessName());
        bundle.putString("showText", showText);
        intent.putExtras(bundle);
        Manager.getContext().sendBroadcast(intent);
    }

    /**
     * 根据进程简写名称，查找进程全名显示
     *
     * @param processName
     * @param processSet
     * @return
     */
    public static String getRealProcessName(String processName, Set<String> processSet) {
        boolean isChildProcess = processName.contains(":");
        String realName = "";
        for (String pn : processSet) {
            if (isChildProcess && pn.contains(processName)) {
                realName = pn;
                break;
            } else if ((!isChildProcess) && pn.contains(processName) && (pn.contains(":") == false)) {
                realName = pn;
                break;
            }
        }
        return realName;
    }
}
