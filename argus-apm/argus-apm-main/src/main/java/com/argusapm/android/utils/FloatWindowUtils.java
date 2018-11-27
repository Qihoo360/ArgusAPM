package com.argusapm.android.utils;

import android.os.Build;
import android.view.WindowManager;

/**
 * 悬浮窗口工具类
 *
 * @author ArgusAPM Team
 */
public class FloatWindowUtils {

    /**
     * 选择悬浮窗类型
     */
    public static int getType() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            //7.1.1以上需要动态申请TYPE_APPLICATION_OVERLAY权限
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            //7.1.1 需要动态申请TYPE_SYSTEM_ALERT权限
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }
}