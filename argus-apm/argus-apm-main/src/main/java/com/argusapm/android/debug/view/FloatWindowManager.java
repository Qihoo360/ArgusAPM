package com.argusapm.android.debug.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.utils.FloatWindowUtils;
import com.argusapm.android.utils.LogX;

import static android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * debug模式悬浮窗控制类
 *
 * @author ArgusAPM Team
 */
public class FloatWindowManager implements SmallFloatWindowView.ISmallCallback, BigFloatWindowView.IBigCallback {
    private static final String SUB_TAG = "FloatWindowManager";
    private static FloatWindowManager instance;
    private WindowManager mWindowManager;
    private SmallFloatWindowView smallView;
    private BigFloatWindowView bigView;
    private WindowManager.LayoutParams smallParams;
    private WindowManager.LayoutParams bigParams;
    private DisplayMetrics dm;
    private int xPosition = 10;
    private int yPosition = 0;
    private int currentState = FloatWindowState.NON_WINDOW;
    public static final String SUB_FLOAT_WIN_RECEIVER_ACTION = "_float_win_receiver_action";

    private static class FloatWindowState {
        public static final int NON_WINDOW = 0;
        public static final int SMALL_WINDOW = 1;
        public static final int BIG_WINDOW = 2;
    }

    public static FloatWindowManager getInstance() {
        if (null == instance) {
            synchronized (Manager.class) {
                if (null == instance) {
                    instance = new FloatWindowManager();
                }
            }
        }
        return instance;
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData(intent);
        }

    };

    public FloatWindowManager() {
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Manager.getContext().getPackageName() + SUB_FLOAT_WIN_RECEIVER_ACTION);
        try {
            Manager.getContext().registerReceiver(myReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示小悬浮窗
     */
    public void showSmallFloatWin() {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "showSmallFloatWin：创建悬浮窗口");
        }
        if (smallView == null) {
            smallView = new SmallFloatWindowView(Manager.getContext(), dm.density);
            smallParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    FloatWindowUtils.getType(),
                    FLAG_NOT_FOCUSABLE | FLAG_ALT_FOCUSABLE_IM, PixelFormat.TRANSLUCENT);
            smallParams.gravity = Gravity.LEFT | Gravity.TOP;
            smallParams.x = (int) xPosition;
            smallParams.y = (int) (dm.heightPixels / 4 - yPosition);
            smallView.setWindowsParams(smallParams);
            smallView.setOnSmallCallback(this);
        }
        removeOldFloatWindow();
        getWindowManager().addView(smallView, smallParams);
        currentState = FloatWindowState.SMALL_WINDOW;
    }

    /**
     * 显示大悬浮窗
     */
    public void showBigWindow() {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "showBigWindow：创建悬浮窗口");
        }
        if (bigView == null) {
            bigView = new BigFloatWindowView(Manager.getContext(), dm.density);
            bigParams = new WindowManager.LayoutParams();
            bigParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    FloatWindowUtils.getType(),
                    FLAG_NOT_FOCUSABLE | FLAG_ALT_FOCUSABLE_IM, PixelFormat.TRANSLUCENT);
            bigParams.gravity = Gravity.LEFT | Gravity.TOP;
            bigParams.x = (int) xPosition;
            bigParams.y = (int) (dm.heightPixels / 4 - yPosition);
            bigView.setOnBigCallback(this);
        }
        removeOldFloatWindow();
        getWindowManager().addView(bigView, bigParams);
        currentState = FloatWindowState.BIG_WINDOW;
    }

    /**
     * 删除旧悬浮窗口
     */
    private void removeOldFloatWindow() {
        switch (currentState) {
            case FloatWindowState.SMALL_WINDOW:
                if (smallView == null) {
                    return;
                }
                getWindowManager().removeView(smallView);
                break;
            case FloatWindowState.BIG_WINDOW:
                if (bigView == null) {
                    return;
                }
                getWindowManager().removeView(bigView);
                break;
        }
    }

    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) Manager.getContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 更新悬浮窗口数据
     *
     * @param intent
     */
    public void updateData(Intent intent) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "updateData：更新悬浮窗数据");
        }
        Message msg = sHander.obtainMessage();
        msg.obj = intent;
        sHander.sendMessage(msg);
    }

    private Handler sHander = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (currentState == FloatWindowState.BIG_WINDOW) {
                Intent intent = (Intent) msg.obj;
                if (intent == null) {
                    return;
                }
                try {
                    IInfo iInfo = (IInfo) intent.getSerializableExtra("info");
                    String processName = intent.getStringExtra("processName");
                    String showText = intent.getStringExtra("showText");
                    bigView.updateShowData(processName, iInfo, showText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 点击小悬浮窗口
     */
    @Override
    public void onSmallWindowClick() {
        showBigWindow();
    }

    /**
     * 点击大悬浮窗口
     */
    @Override
    public void onBigWindowClick() {
        showSmallFloatWin();
    }
}
