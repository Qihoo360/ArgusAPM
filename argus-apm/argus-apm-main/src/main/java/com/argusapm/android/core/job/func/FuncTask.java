package com.argusapm.android.core.job.func;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;

/**
 * @author ArgusAPM Team
 */
public class FuncTask extends BaseTask {
    @Override
    protected IStorage getStorage() {
        return new FuncStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_FUNC;
    }
}
