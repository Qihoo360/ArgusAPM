package com.argusapm.android.cloudconfig.data;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;

/**
 * 云控数据g_core段
 *
 * @author ArgusAPM Team
 */
public class ArgusApmConfigCore {
    private long exp = Long.MAX_VALUE;
    private int flags = 0;// 采集默认为全关

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void setEnabled(int flag) {
        flags |= flag;
    }

    public void setDisabled(int flag) {
        flags &= (~flag);
    }

    public boolean isEnabled(String task) {
        if (TextUtils.isEmpty(task)) {
            return false;
        }
        Integer obj = ApmTask.getTaskMap().get(task);
        if (obj == null) {
            return false;
        }
        int flag = obj.intValue();
        boolean switchState = (flags & flag) == flag;
        long curTime = System.currentTimeMillis();
        boolean isValidTime = curTime < exp;
        return switchState && isValidTime;
    }
}