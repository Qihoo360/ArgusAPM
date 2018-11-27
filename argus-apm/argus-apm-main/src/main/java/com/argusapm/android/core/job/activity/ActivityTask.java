package com.argusapm.android.core.job.activity;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.utils.LogX;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class ActivityTask extends BaseTask {

    @Override
    protected IStorage getStorage() {
        return new ActivityStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_ACTIVITY;
    }

    @Override
    public void start() {
        super.start();
        if (Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_COLLECT_ACTIVITY_INSTRUMENTATION) && !InstrumentationHooker.isHookSucceed()) {//hook失败
            if (DEBUG) {
                LogX.d(TAG, "ActivityTask", "canWork hook : hook失败");
            }
            mIsCanWork = false;
        }
    }

    @Override
    public boolean isCanWork() {
        return mIsCanWork;
    }
}