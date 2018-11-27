package com.argusapm.android.cloudconfig.data;

import com.argusapm.android.core.TaskConfig;

/**
 * @author ArgusAPM Team
 */
public class ArgusApmConfigFuncControl {
    public long onreceiveMinTime = TaskConfig.ONRECEIVE_MIN_TIME;
    public long threadMinTime = TaskConfig.THREAD_MIN_TIME;
    public long ioMinTime = TaskConfig.IO_MIN_TIME;
    public long minFileSize = TaskConfig.FILE_MIN_SIZE;
    public long activityFirstMinTime = TaskConfig.DEFAULT_ACTIVITY_FIRST_MIN_TIME;
    public long activityLifecycleMinTime = TaskConfig.DEFAULT_ACTIVITY_LIFECYCLE_MIN_TIME;
    public int blockMinTime = TaskConfig.DEFAULT_BLOCK_TIME;
    private int mMemoryDelayTime = TaskConfig.DEFAULT_MEMORY_DELAY_TIME;
    private int mMemoryIntervalTime = TaskConfig.DEFAULT_MEMORY_INTERVAL;
    private int anrIntervalTime = TaskConfig.DEFAULT_ANR_INTERVAL;
    private int fileDepth = TaskConfig.DEFAULT_FILE_DEPTH;
    private int mThreadCntDelayTime = TaskConfig.DEFAULT_THREAD_CNT_DELAY_TIME;
    private int mThreadCntIntervalTime = TaskConfig.DEFAULT_THREAD_CNT_INTERVAL_TIME;
    private int mWatchDogDelayTime = TaskConfig.DEFAULT_WATCH_DOG_DELAY_TIME;
    private int mWatchDogIntervalTime = TaskConfig.DEFAULT_WATCH_DOG_INTERVAL_TIME;
    public int watchDogMinTime = TaskConfig.DEFAULT_WATCH_DOG_MIN_TIME;

    public void setWatchDogMinTime(int watchDogMinTime) {
        if (watchDogMinTime < TaskConfig.DEFAULT_WATCH_DOG_MIN_TIME) {
            this.watchDogMinTime = TaskConfig.DEFAULT_WATCH_DOG_MIN_TIME;
        } else {
            this.watchDogMinTime = watchDogMinTime;
        }

    }


    public int getMemoryDelayTime() {
        return mMemoryDelayTime;
    }

    public void setMemoryDelayTime(int memoryDelayTime) {
        if (memoryDelayTime >= TaskConfig.MIN_MEMORY_DELAY_TIME) {
            this.mMemoryDelayTime = memoryDelayTime;
        } else {
            this.mMemoryDelayTime = TaskConfig.MIN_MEMORY_DELAY_TIME;
        }
    }

    public int getMemoryIntervalTime() {
        return mMemoryIntervalTime;
    }

    public void setMemoryIntervalTime(int memoryIntervalTime) {
        if (memoryIntervalTime >= TaskConfig.DEFAULT_MEMORY_INTERVAL) {
            this.mMemoryIntervalTime = memoryIntervalTime;
        } else {
            this.mMemoryIntervalTime = TaskConfig.DEFAULT_MEMORY_INTERVAL;
        }
    }

    public int getAnrIntervalTime() {
        return anrIntervalTime;
    }

    public void setAnrIntervalTime(int anrIntervalTime) {
        if (anrIntervalTime >= TaskConfig.MIN_ANR_INTERVAL) {
            this.anrIntervalTime = anrIntervalTime;
        } else {
            this.anrIntervalTime = TaskConfig.MIN_ANR_INTERVAL;
        }
    }

    public int getFileDepth() {
        return fileDepth;
    }

    public void setFileDepth(int fileDepth) {
        if (fileDepth > 0) {
            this.fileDepth = fileDepth;
        } else {
            this.fileDepth = TaskConfig.DEFAULT_FILE_DEPTH;
        }
    }

    public int getThreadCntDelayTime() {
        return mThreadCntDelayTime;
    }

    public void setThreadCntDelayTime(int threadCntDelayTime) {
        if (threadCntDelayTime >= TaskConfig.MIN_THREAD_CNT_DELAYTIME) {
            this.mThreadCntDelayTime = threadCntDelayTime;
        } else {
            this.mThreadCntDelayTime = TaskConfig.MIN_THREAD_CNT_DELAYTIME;
        }
    }

    public int getThreadCntIntervalTime() {
        return mThreadCntIntervalTime;
    }

    public void setThreadCntIntervalTime(int threadCntIntervalTime) {
        if (threadCntIntervalTime >= TaskConfig.DEFAULT_THREAD_CNT_INTERVAL_TIME) {
            this.mThreadCntIntervalTime = threadCntIntervalTime;
        } else {
            this.mThreadCntIntervalTime = TaskConfig.DEFAULT_THREAD_CNT_INTERVAL_TIME;
        }
    }

    public int getWatchDogDelayTime() {
        return mWatchDogDelayTime;
    }

    public void setWatchDogDelayTime(int watchDogDelayTime) {
        if (watchDogDelayTime < TaskConfig.DEFAULT_WATCH_DOG_DELAY_TIME) {
            this.mWatchDogDelayTime = TaskConfig.DEFAULT_WATCH_DOG_DELAY_TIME;
        } else {
            this.mWatchDogDelayTime = watchDogDelayTime;
        }

    }

    public int getWatchDogIntervalTime() {
        return mWatchDogIntervalTime;
    }

    public void setWatchDogIntervalTime(int watchDogIntervalTime) {
        if (watchDogIntervalTime < TaskConfig.DEFAULT_WATCH_DOG_INTERVAL_TIME) {
            this.mWatchDogIntervalTime = TaskConfig.DEFAULT_WATCH_DOG_INTERVAL_TIME;
        } else {
            this.mWatchDogIntervalTime = watchDogIntervalTime;
        }
    }

}
