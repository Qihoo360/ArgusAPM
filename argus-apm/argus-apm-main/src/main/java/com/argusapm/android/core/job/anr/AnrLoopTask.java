package com.argusapm.android.core.job.anr;

import android.content.Context;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.CommonUtils;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.SystemUtils;

import java.io.File;

/**
 * 定期采样的方式
 *
 * @author ArgusAPM Team
 */
public class AnrLoopTask extends AnrTask {
    public static final String SUB_TAG = "AnrLoopTask";

    public AnrLoopTask(Context c) {
        super(c);
    }

    @Override
    protected IStorage getStorage() {
        return null;
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_ANR;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isCanWork()) {
                return;
            }
            if (CommonUtils.isWiFiConnected(mContext)) {
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "anr start obtain");
                }

                readAnrFiles();
            }
            if (Env.DEBUG) {
                AsyncThreadTask.getInstance().executeDelayed(runnable, TaskConfig.TEST_INTERVAL);
            } else {
                AsyncThreadTask.getInstance().executeDelayed(runnable, ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.getAnrIntervalTime());
            }
        }
    };

    private void readAnrFiles() {
        File anrDirF = new File(ANR_DIR);
        if (anrDirF.exists() && anrDirF.isDirectory()) {
            File[] anrFiles = anrDirF.listFiles();
            if (anrFiles != null && anrFiles.length > 0) {
                for (File f : anrFiles) {
                    String fileName = f.getName();
                    if (fileName.contains("trace") && (System.currentTimeMillis() - f.lastModified() < TaskConfig.ANR_VALID_TIME) && (f.length() <= TaskConfig.MAX_READ_FILE_SIZE) && parser != null) {
                        handle(f.getPath());
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        super.start();
        AsyncThreadTask.executeDelayed(runnable, (int) (Math.round(Math.random() * 1000)));
    }
}
