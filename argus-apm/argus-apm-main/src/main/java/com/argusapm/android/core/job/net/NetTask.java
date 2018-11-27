package com.argusapm.android.core.job.net;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;

/**
 * @author ArgusAPM Team
 */
public class NetTask extends BaseTask {

    @Override
    protected IStorage getStorage() {
        return new NetStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_NET;
    }
}
