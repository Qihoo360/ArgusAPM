package com.argusapm.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.PreferenceUtils;

/**
 * argus apm receiver管理类
 *
 * @author ArgusAPM Team
 */
public class ReceiverManager {
    public static final int UPDATE_READ_CONFIG_INTERVAL = 2 * 60 * 60 * 1000;
    private ScreenReceiver screenReceiver;

    public void init(Context context) {
        if (context == null) {
            return;
        }
        if (screenReceiver == null) {
            screenReceiver = new ScreenReceiver();
        }
        context.registerReceiver(screenReceiver, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        return filter;
    }

    //定时任务
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Manager.getInstance().reload();
            if (Manager.getContext() == null) {
                return;
            }
            PreferenceUtils.setLong(Manager.getContext(), PreferenceUtils.SP_KEY_UPDATE_READ_CONFIG_TIME, System.currentTimeMillis());
        }
    };

    class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Manager.getContext() == null) {
                return;
            }
            long lastTime = PreferenceUtils.getLong(Manager.getContext(), PreferenceUtils.SP_KEY_UPDATE_READ_CONFIG_TIME, 0);
            long cur = System.currentTimeMillis();
            if ((cur - lastTime) < UPDATE_READ_CONFIG_INTERVAL) {
                return;
            }
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {

            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                AsyncThreadTask.executeDelayedToUI(runnable, 5000 + (int) (Math.round(Math.random() * 1000)));
            }
        }
    }
}