package com.argusapm.android.cleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.storage.DataHelper;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.PreferenceUtils;

/**
 * 数据清理管理类
 *
 * @author ArgusAPM Team
 */
public class DataCleaner {
    private Context mContext;
    private static final String SUB_TAG = "DataCleaner";

    public DataCleaner(Context c) {
        mContext = c;
    }

    public void create() {
        try {
            mContext.registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
        } catch (Exception e) {
            LogX.d(Env.TAG, SUB_TAG, "create ex : " + Log.getStackTraceString(e));
        }
    }

    public void destroy() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            LogX.d(Env.TAG, SUB_TAG, "destroy ex : " + Log.getStackTraceString(e));
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Intent.ACTION_USER_PRESENT)) {
                // 清理逻辑，每天只需要执行几次，不需要每次收到解锁屏幕广播都执行
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "recv ACTION_USER_PRESENT");
                }
                long cur = System.currentTimeMillis();
                long inter = cur - getLastTime();
                long need = ArgusApmConfigManager.getInstance().getArgusApmConfigData().cleanInterval;
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "inter = " + inter + " cur = " + cur + " | last = " + getLastTime() + " | need = " + need);
                }
                if (inter >= need) {
                    if (Env.DEBUG) {
                        LogX.d(Env.TAG, SUB_TAG, "inter = " + inter + " clean db");
                    }
                    cleanDb();
                    setLastTime(cur);
                }
            }
        }
    };

    private void setLastTime(long cur) {
        PreferenceUtils.setLong(mContext, PreferenceUtils.SP_KEY_LAST_CLEAN_TIME, cur);
    }

    private long getLastTime() {
        return PreferenceUtils.getLong(mContext, PreferenceUtils.SP_KEY_LAST_CLEAN_TIME, 0);
    }

    private void cleanDb() {
        AsyncThreadTask.executeDelayed(new Runnable() {
            @Override
            public void run() {
                DataHelper.deleteOld();
            }
        }, 5 * 1000);
    }

}
