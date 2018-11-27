package com.argusapm.android.core.job.appstart;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;

/**
 * 应用启动Task
 *
 * @author ArgusAPM Team
 */
public class AppStartTask extends BaseTask {

    @Override
    protected IStorage getStorage() {
        return new AppStartStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_APP_START;
    }
}