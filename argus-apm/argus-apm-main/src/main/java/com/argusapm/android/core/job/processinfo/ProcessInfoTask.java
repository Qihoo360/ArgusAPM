package com.argusapm.android.core.job.processinfo;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.core.tasks.ITask;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.LogX;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 进程信息任务类
 *
 * @author ArgusAPM Team
 */
public class ProcessInfoTask extends BaseTask {

    @Override
    public void start() {
        super.start();
        saveProcessInfo();
    }

    /**
     * 保存进程相关信息
     */
    private void saveProcessInfo() {
        AsyncThreadTask.executeDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isCanWork()) {
                    return;
                }
                ProcessInfo info = new ProcessInfo();
                ITask task = Manager.getInstance().getTaskManager().getTask(ApmTask.TASK_PROCESS_INFO);
                if (task != null) {
                    task.save(info);
                } else {
                    if (DEBUG) {
                        LogX.d(TAG, "Client", "ProcessInfo task == null");
                    }
                }
            }
        }, 2000 + (int) (Math.round(Math.random() * 1000)));
    }

    @Override
    protected IStorage getStorage() {
        return new ProcessInfoStorage();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_PROCESS_INFO;
    }
}
