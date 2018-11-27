package com.argusapm.android.core.tasks;

import com.argusapm.android.core.IInfo;

/**
 * ArgusAPM任务接口
 *
 * @author ArgusAPM Team
 */
public interface ITask {
    String getTaskName();

    void start();

    boolean isCanWork();

    void setCanWork(boolean value);

    boolean save(IInfo info);

    void stop();
}