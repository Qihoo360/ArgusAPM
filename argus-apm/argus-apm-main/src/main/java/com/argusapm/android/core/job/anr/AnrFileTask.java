package com.argusapm.android.core.job.anr;

import android.content.Context;
import android.os.FileObserver;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.utils.LogX;

/**
 * anr数据解析:采用FileObserver的方式
 *
 * @author ArgusAPM Team
 */
public class AnrFileTask extends AnrTask {
    public static final String SUB_TAG = "AnrFileTask";

    public AnrFileTask(Context c) {
        super(c);
    }

    FileObserver fileObserver = new FileObserver(ANR_DIR, FileObserver.CLOSE_WRITE) {
        @Override
        public void onEvent(int event, String simplePath) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "anr happen : event " + event + " | path " + simplePath);
            }
            if (TextUtils.isEmpty(simplePath)) {
                return;
            }
            String path = ANR_DIR + simplePath;

            if (!path.contains("trace")) {
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, path + " is not anr file");
                }
                return;
            }
            handle(path);
        }
    };

    @Override
    public void start() {
        super.start();
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "startWatching");
        }
        fileObserver.startWatching();
    }

    @Override
    public void stop() {
        super.stop();
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "stopWatching");
        }
        fileObserver.stopWatching();
    }
}
