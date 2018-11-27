package com.argusapm.android.debug.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.core.job.activity.ActivityInfo;
import com.argusapm.android.core.job.appstart.AppStartInfo;
import com.argusapm.android.core.job.fps.FpsInfo;
import com.argusapm.android.core.job.memory.MemoryInfo;
import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.debug.config.DebugConfig;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.LogX;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 大悬浮窗视图
 *
 * @author ArgusAPM Team
 */
public class BigFloatWindowView extends LinearLayout implements View.OnClickListener {
    private static final String SUB_TAG = "BigFloatWindowView";
    private Context mContext;

    private IBigCallback mCallback;
    private float mDipScale = 1;

    private HashMap<String, HashMap<String, TextView>> mTextViews;
    private HashMap<String, LinearLayout> mContainerViews;
    private ConcurrentHashMap<String, Boolean> mLiveProcess;
    //定时任务
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (Map.Entry<String, Boolean> entry : mLiveProcess.entrySet()) {
                entry.getKey();
                entry.getValue();
                if (entry.getValue()) {
                    entry.setValue(false);
                } else {
                    deleteProcessview(entry.getKey());
                }
            }
            AsyncThreadTask.executeDelayedToUI(runnable, TaskConfig.DEBUG_PROCESS_LIVE_INTERVAL);
        }
    };

    public BigFloatWindowView(Context context, float dipScale) {
        super(context);
        mDipScale = dipScale;
        mContext = context;
        init();

        AsyncThreadTask.executeDelayedToUI(runnable, TaskConfig.DEBUG_PROCESS_LIVE_INTERVAL);
    }

    private void init() {
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(DebugConfig.BIG_WINDOW_BG_COLOR);
        int padding = (int) (mDipScale * DebugConfig.DEFAULT_PADDING);
        this.setPadding(padding, padding, padding, padding);
        mContainerViews = new HashMap<String, LinearLayout>();
        mTextViews = new HashMap<String, HashMap<String, TextView>>();
        mLiveProcess = new ConcurrentHashMap<String, Boolean>();
        this.setOnClickListener(this);
    }

    private LinearLayout addProcessView(String processName) {
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) (mDipScale * 10), 0, 0);
        TextView textView = new TextView(mContext);
        textView.setTextColor(DebugConfig.TEXT_COLOR_GREEN);
        textView.setTextSize(DebugConfig.DEFAULT_TEXT_SIZE);
        textView.setText("进程：" + processName);
        container.addView(textView);
        this.addView(container, params);
        mContainerViews.put(processName, container);
        return container;
    }

    private void deleteProcessview(String processName) {
        LinearLayout container = mContainerViews.get(processName);
        this.removeView(container);
        mContainerViews.remove(processName);
        mTextViews.remove(processName);
        mLiveProcess.remove(processName);
    }

    public void updateShowData(String processName, IInfo info, String showText) {
        if (TextUtils.isEmpty(processName) || "null".equals(processName)) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "updateShowData -> processName为空：" + processName);
            }
            return;
        }
        updateLiveProcess(processName);
        if (info instanceof MemoryInfo) {
            TextView checkView = checkView(processName, ApmTask.TASK_MEM);
            MemoryInfo memoryInfo = (MemoryInfo) info;
            checkView.setText("内存：" + "total-" + (memoryInfo.totalPss / 1024) + "M"
                    + " dalvik-" + (memoryInfo.dalvikPss / 1024) + "M"
                    + " native-" + (memoryInfo.nativePss / 1024) + "M"
                    + " other-" + (memoryInfo.otherPss / 1024) + "M");
        } else if (info instanceof ActivityInfo) {
            ActivityInfo activityInfo = (ActivityInfo) info;
            if (activityInfo.lifeCycle == ActivityInfo.TYPE_FIRST_FRAME) {
                showActivityInfo(processName, activityInfo, DebugConfig.WARN_ACTIVITY_FRAME_VALUE);
            } else if (activityInfo.lifeCycle == ActivityInfo.TYPE_CREATE) {
                showActivityInfo(processName, activityInfo, DebugConfig.WARN_ACTIVITY_CREATE_VALUE);
            } else if (activityInfo.lifeCycle == ActivityInfo.TYPE_RESUME) {
                showActivityInfo(processName, activityInfo, DebugConfig.WARN_ACTIVITY_CREATE_VALUE);
            }
        } else if (info instanceof FpsInfo) {
            TextView checkView = checkView(processName, ApmTask.TASK_FPS);
            FpsInfo fpsInfo = (FpsInfo) info;
            if (fpsInfo.getFps() <= DebugConfig.WARN_FPS_VALUE) {
                checkView.setTextColor(DebugConfig.TEXT_COLOR_WARN);
            } else {
                checkView.setTextColor(DebugConfig.TEXT_COLOR_DEFAULT);
            }
            checkView.setText("帧率:" + fpsInfo.getFps());
        }else if (info instanceof NetInfo) {
            TextView checkView = checkView(processName, ApmTask.TASK_NET);
            NetInfo netInfo = (NetInfo) info;
            checkView.setTextColor(DebugConfig.TEXT_COLOR_WARN);
            checkView.setText("net error code:" + netInfo.statusCode);
        } else if (info instanceof AppStartInfo) {
            TextView checkView = checkView(processName, ApmTask.TASK_APP_START);
            AppStartInfo appStartInfo = (AppStartInfo) info;
            if (appStartInfo.getStartTime() >= DebugConfig.WARN_ACTIVITY_FRAME_VALUE) {
                checkView.setTextColor(DebugConfig.TEXT_COLOR_WARN);
            } else {
                checkView.setTextColor(DebugConfig.TEXT_COLOR_DEFAULT);
            }
            checkView.setText("appstart:" + appStartInfo.getStartTime());
        }
    }

    private void showActivityInfo(String processName, ActivityInfo activityInfo, int warningTime) {
        TextView checkView = checkView(processName, activityInfo.getLifeCycleString());
        if (activityInfo.time >= warningTime) {
            checkView.setTextColor(DebugConfig.TEXT_COLOR_WARN);
        } else {
            checkView.setTextColor(DebugConfig.TEXT_COLOR_DEFAULT);
        }
        checkView.setText(activityInfo.activityName + " :" + activityInfo.getLifeCycleString() + " " + activityInfo.time);
    }

    private void updateLiveProcess(String processName) {
        mLiveProcess.put(processName, true);
    }

    private TextView checkView(String processName, String textName) {
        LinearLayout container = mContainerViews.get(processName);
        if (null == container) {
            container = addProcessView(processName);
            mContainerViews.put(processName, container);
            mTextViews.put(processName, new HashMap<String, TextView>());
        }
        TextView textView = mTextViews.get(processName).get(textName);
        if (null == textView) {
            textView = new TextView(mContext);
            textView.setTextColor(DebugConfig.TEXT_COLOR_DEFAULT);
            textView.setTextSize(DebugConfig.DEFAULT_TEXT_SIZE);
            container.addView(textView);
            mTextViews.get(processName).put(textName, textView);
        }
        return textView;
    }

    public void setOnBigCallback(IBigCallback bigCallback) {
        mCallback = bigCallback;
    }

    @Override
    public void onClick(View v) {
        mCallback.onBigWindowClick();
    }

    public interface IBigCallback {
        void onBigWindowClick();
    }
}
