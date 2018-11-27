package com.argusapm.android.debug.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.argusapm.android.R;
import com.argusapm.android.core.Manager;
import com.argusapm.android.debug.view.component.CircleView;

import java.lang.reflect.Field;

/**
 * 小悬浮窗视图
 *
 * @author ArgusAPM Team
 */
public class SmallFloatWindowView extends LinearLayout {
    private WindowManager windowManager;
    private ISmallCallback mCallback;
    //小悬浮窗的参数
    private WindowManager.LayoutParams mParams;
    //记录当前手指位置在屏幕上的横坐标值
    private float xMove;
    //记录当前手指位置在屏幕上的纵坐标值
    private float yMove;
    //记录手指按下时在屏幕上的横坐标的值
    private float xDown;
    //记录手指按下时在屏幕上的纵坐标的值
    private float yDown;
    //记录手指按下时在小悬浮窗的View上的横坐标的值
    private float xDownInView;
    //记录手指按下时在小悬浮窗的View上的纵坐标的值
    private float yDownInView;
    //记录系统状态栏的高度
    private static int statusBarHeight;
    private int viewSize = 30;
    private float mDipScale = 1;

    public SmallFloatWindowView(Context context, float dipScale) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDipScale = dipScale;
        init(context);
    }

    private void init(Context context) {
        CircleView view = new CircleView(Manager.getContext(), mDipScale);
        LayoutParams layoutParams = new LayoutParams((int) (viewSize * mDipScale), (int) (viewSize * mDipScale));
        this.addView(view, layoutParams);
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xMove - xDownInView);
        mParams.y = (int) (yMove - yDownInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setWindowsParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    public void setOnSmallCallback(ISmallCallback iSmallCallback) {
        mCallback = iSmallCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xDownInView = event.getX();
                yDownInView = event.getY();
                xDown = event.getRawX();
                yDown = event.getRawY() - getStatusBarHeight();
                xMove = event.getRawX();
                yMove = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                yMove = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if ((Math.abs(xDown - event.getRawX()) <= 2) && (Math.abs(yDown - (event.getRawY() - getStatusBarHeight())) <= 2)) {
                    mCallback.onSmallWindowClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 小悬浮窗回调事件接口
     */
    public interface ISmallCallback {
        void onSmallWindowClick();
    }


}
