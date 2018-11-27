package com.argusapm.android.core.job.watchDog;

import android.os.Handler;
import android.util.Log;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.core.tasks.ITask;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.LogX;

import static com.argusapm.android.Env.DEBUG;

/**
 * @author ArgusAPM Team
 */
public class WatchDogTask extends BaseTask {

    private static final String TAG = "WatchDogTask";

    private static final int TICK_INIT_VALUE = 0;

    private volatile int mTick = TICK_INIT_VALUE;

    private static final int DELAY_TIME = ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.watchDogMinTime;

    private Handler mHandler = new Handler(Manager.getInstance().getContext().getMainLooper());

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null == mHandler) {
                Log.e(TAG, "handler is null");
                return;
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTick++;
                }
            });

            try {
                Thread.sleep(DELAY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (TICK_INIT_VALUE == mTick) {
                String stack = captureStacktrace();
                saveWatchdogInfo(stack);
            } else {
                mTick = TICK_INIT_VALUE;
            }

            AsyncThreadTask.getInstance().executeDelayed(runnable, ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.getWatchDogIntervalTime());
        }
    };

    @Override
    public void start() {
        AsyncThreadTask.getInstance().executeDelayed(runnable, ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.getWatchDogDelayTime());
    }

    @Override
    protected IStorage getStorage() {
        return new WatchDogInfoStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_WATCHDOG;
    }

    private String captureStacktrace() {
        StringBuilder stackStr = new StringBuilder();
        StackTraceElement[] stackTraces = mHandler.getLooper().getThread().getStackTrace();

        for (StackTraceElement stackTraceElement : stackTraces) {
            stackStr.append(stackTraceElement.toString()).append("\r\n");
        }

        return stackStr.toString();
    }

    /**
     * 保存卡顿相关信息
     */
    private void saveWatchdogInfo(final String stack) {
        AsyncThreadTask.execute(new Runnable() {
            @Override
            public void run() {
                WatchDogInfo info = new WatchDogInfo();
                info.blockStack = stack;
                info.blockTime = ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.watchDogMinTime;
                ITask task = Manager.getInstance().getTaskManager().getTask(ApmTask.TASK_WATCHDOG);
                if (task != null) {
                    task.save(info);
                } else {
                    if (DEBUG) {
                        LogX.d(TAG, "Client", "BlockInfo task == null");
                    }
                }
            }
        });
    }
}
