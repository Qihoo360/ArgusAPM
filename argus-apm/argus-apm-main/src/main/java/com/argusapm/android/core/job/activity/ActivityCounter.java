package com.argusapm.android.core.job.activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 目前用于判断当前app是否在前台
 *
 * @author ArgusAPM Team
 */
public class ActivityCounter {

    private List<UIListener> mListener = new ArrayList<UIListener>();

    private int mCounter = 0;
    private String mCurActivity = "";

    /**
     * Activity onResume 调用
     * 记录当前显示的activity
     */
    public void inc(String activityName) {
        mCurActivity = activityName;
        mCounter++;

        if (isVisible()) {
            dispacthOnVisible();
        }
    }

    /**
     * Activity onPause 调用
     */
    public void dec() {
        mCurActivity = "";
        mCounter--;
    }

    /**
     * 判断当前是否有activity在前台
     *
     * @return
     */
    public boolean isVisible() {
        return mCounter > 0;
    }

    /**
     * 获取当前显示的activity的名字
     *
     * @return
     */
    public String getCurActivity() {
        return mCurActivity;
    }

    public void reset() {
        mCounter = 0;
    }

    public void register(UIListener listener) {
        if (listener == null) {
            return;
        }

        if (mListener.contains(listener)) {
            return;
        }

        mListener.add(listener);

    }

    public void unRegister(UIListener listener) {
        if (listener == null) {
            return;
        }
        if (mListener.contains(listener)) {
            mListener.remove(listener);
        }
    }

    private void dispacthOnVisible() {
        if (mListener.isEmpty()) {
            return;
        }
        for (UIListener l : mListener) {
            l.onVisible();
        }
    }

    public interface UIListener {
        void onVisible();
    }
}
