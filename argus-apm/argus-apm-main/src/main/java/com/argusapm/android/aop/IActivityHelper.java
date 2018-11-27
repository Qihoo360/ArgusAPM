package com.argusapm.android.aop;

import android.app.Activity;
import android.content.Context;

/**
 * @author ArgusAPM Team
 */
public interface IActivityHelper {
    // activity的生命周期处理
    void invoke(Activity activity, long startTime, String lifeCycle, Object... args);

    // application的onCreate处理
    void applicationOnCreate(Context context);

    // application的attachBaseContext处理
    void applicationAttachBaseContext(Context context);

    // 扩展用
    Object parse(Object... args);
}
