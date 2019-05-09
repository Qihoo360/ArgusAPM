package com.argusapm.android.core.job.webview;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;

/**
 * @author ArgusAPM Team
 */
public class WebTask extends BaseTask {
    @Override
    protected IStorage getStorage() {
        return new WebStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_WEBVIEW;
    }
}
