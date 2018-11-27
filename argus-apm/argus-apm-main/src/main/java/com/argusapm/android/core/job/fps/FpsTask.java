package com.argusapm.android.core.job.fps;

import android.view.Choreographer;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.debug.AnalyzeManager;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.CommonUtils;
import com.argusapm.android.utils.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * fps收集task
 *
 * @author ArgusAPM Team
 */
public class FpsTask extends BaseTask implements Choreographer.FrameCallback {
    private final String SUB_TAG = ApmTask.TASK_FPS;

    private long mLastFrameTimeNanos = 0; //最后一次时间
    private long mFrameTimeNanos = 0; //本次的当前时间
    private int mCurrentCount = 0; //当前采集条数
    private int mFpsCount = 0;
    private FpsInfo fpsInfo = new FpsInfo();
    private JSONObject paramsJson = new JSONObject();
    //定时任务
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isCanWork()) {
                mCurrentCount = 0;
                return;
            }
            calculateFPS();
            mCurrentCount++;
            //实现分段采集
            if (mCurrentCount < ArgusApmConfigManager.getInstance().getArgusApmConfigData().onceMaxCount) {
                AsyncThreadTask.executeDelayed(runnable, TaskConfig.FPS_INTERVAL);
            } else {
                AsyncThreadTask.executeDelayed(runnable, ArgusApmConfigManager.getInstance().getArgusApmConfigData().pauseInterval > TaskConfig.FPS_INTERVAL ? ArgusApmConfigManager.getInstance().getArgusApmConfigData().pauseInterval : TaskConfig.FPS_INTERVAL);
                mCurrentCount = 0;
            }
        }
    };

    private void calculateFPS() {
        if (mLastFrameTimeNanos == 0) {
            mLastFrameTimeNanos = mFrameTimeNanos;
            return;
        }
        float costTime = (float) (mFrameTimeNanos - mLastFrameTimeNanos) / 1000000.0F;
        if (mFpsCount <= 0 && costTime <= 0.0F) {
            return;
        }
        int fpsResult = (int) (mFpsCount * 1000 / costTime);
        if (fpsResult < 0) {
            return;
        }
        if (fpsResult <= TaskConfig.DEFAULT_FPS_MIN_COUNT) {
            fpsInfo.setFps(fpsResult);
            try {
                paramsJson.put(FpsInfo.KEY_STACK, CommonUtils.getStack());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fpsInfo.setParams(paramsJson.toString());
            fpsInfo.setProcessName(ProcessUtils.getCurrentProcessName());
            save(fpsInfo);
        }
        if (AnalyzeManager.getInstance().isDebugMode()) {
            if (fpsResult > TaskConfig.DEFAULT_FPS_MIN_COUNT) {
                fpsInfo.setFps(fpsResult);
            }
            AnalyzeManager.getInstance().getParseTask(ApmTask.TASK_FPS).parse(fpsInfo);
        }
        mLastFrameTimeNanos = mFrameTimeNanos;
        mFpsCount = 0;
    }

    @Override
    protected IStorage getStorage() {
        return new FpsStorage();
    }

    @Override
    public void start() {
        super.start();
        AsyncThreadTask.executeDelayed(runnable, (int) (Math.round(Math.random() * TaskConfig.TASK_DELAY_RANDOM_INTERVAL)));
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_FPS;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        mFpsCount++;
        mFrameTimeNanos = frameTimeNanos;
        if (isCanWork()) {
            //注册下一帧回调
            Choreographer.getInstance().postFrameCallback(this);
        } else {
            mCurrentCount = 0;
        }
    }
}