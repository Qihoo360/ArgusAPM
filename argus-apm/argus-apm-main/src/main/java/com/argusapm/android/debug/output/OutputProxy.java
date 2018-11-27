package com.argusapm.android.debug.output;

import android.text.TextUtils;

import com.argusapm.android.debug.AnalyzeManager;
import com.argusapm.android.debug.storage.StorageManager;

/**
 * Debug模块输出代理类
 *
 * @author ArgusAPM Team
 */
public class OutputProxy {
    /**
     * 警报信息输出
     *
     * @param showMsg
     */
    public static void output(String showMsg) {
        if (!AnalyzeManager.getInstance().isDebugMode()) {
            return;
        }
        if (TextUtils.isEmpty(showMsg)) {
            return;
        }
        //存储在本地
        StorageManager.saveToFile(showMsg);
    }

    /**
     * 警报信息输出
     *
     * @param showMsg toast展示输出
     * @param allMsg  所有信息
     */
    public static void output(String showMsg, String allMsg) {
        if (!AnalyzeManager.getInstance().isDebugMode()) {
            return;
        }
        if (!TextUtils.isEmpty(allMsg)) {
            //存储在本地
            StorageManager.saveToFile(allMsg);
        }
    }

}
