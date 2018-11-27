package com.argusapm.android.core.job.activity;

import android.app.Activity;
import android.content.Context;

import com.argusapm.android.aop.IActivityHelper;

/**
 * 用于AOP
 *
 * @author ArgusAPM Team
 */
public class ActivityHelper implements IActivityHelper {
    @Override
    public void applicationAttachBaseContext(Context context) {
        AH.applicationAttachBaseContext(context);
    }


    @Override
    public void invoke(Activity activity, long startTime, String lifeCycle, Object... args) {
        AH.invoke(activity, startTime, lifeCycle, args);
    }

    @Override
    public void applicationOnCreate(Context context) {
        AH.applicationOnCreate(context);
    }

    @Override
    public Object parse(Object... args) {
        return null;
    }
}
