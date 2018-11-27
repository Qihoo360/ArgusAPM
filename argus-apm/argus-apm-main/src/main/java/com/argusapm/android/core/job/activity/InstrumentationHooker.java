package com.argusapm.android.core.job.activity;

import android.app.Instrumentation;

import com.argusapm.android.utils.LogX;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * Instrumentation Hook类
 *
 * @author ArgusAPM Team
 */
public class InstrumentationHooker {
    private static boolean isHookSucceed = false;//是否已经hook成功

    public static void doHook() {
        try {
            hookInstrumentation();
            isHookSucceed = true;
        } catch (Exception e) {
            if (DEBUG) {
                LogX.e(TAG, "InstrumentationHooker", e.toString());
            }
        }
    }

    static boolean isHookSucceed() {
        return isHookSucceed;
    }

    private static void hookInstrumentation() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class<?> c = Class.forName("android.app.ActivityThread");
        Method currentActivityThread = c.getDeclaredMethod("currentActivityThread");
        boolean acc = currentActivityThread.isAccessible();
        if (!acc) {
            currentActivityThread.setAccessible(true);
        }
        Object o = currentActivityThread.invoke(null);
        if (!acc) {
            currentActivityThread.setAccessible(acc);
        }
        Field f = c.getDeclaredField("mInstrumentation");
        acc = f.isAccessible();
        if (!acc) {
            f.setAccessible(true);
        }
        Instrumentation currentInstrumentation = (Instrumentation) f.get(o);
        Instrumentation ins = new ApmInstrumentation(currentInstrumentation);
        f.set(o, ins);
        if (!acc) {
            f.setAccessible(acc);
        }
    }
}