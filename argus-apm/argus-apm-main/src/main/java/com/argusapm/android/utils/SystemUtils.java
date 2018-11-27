package com.argusapm.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class SystemUtils {
    private static final String SUB_TAG = "SystemUtils";

    public static Integer getUidByPkgName(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        final PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> installedApplications = processInstalledApp(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (ApplicationInfo appInfo : installedApplications) {
            if (!TextUtils.isEmpty(appInfo.packageName) && pkgName.equalsIgnoreCase(appInfo.packageName)) {
                return appInfo.uid;
            }
        }
        return null;
    }

    /**
     * 处理安装的app，主要合并共享uid的app
     */
    private static List<ApplicationInfo> processInstalledApp(List<ApplicationInfo> inputApps) {
        List<Integer> uidList = new ArrayList<Integer>();
        List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();
        if (null == inputApps) {
            return result;
        }
        for (ApplicationInfo info : inputApps) {
            int uid = info.uid;
            if (!uidList.contains(uid)) {
                uidList.add(uid);
                result.add(info);
            }
        }
        inputApps.clear();
        uidList.clear();
        return result;
    }

    public static boolean isWifiConnected() {
        // 原有方法在android4.0上返回值总是为true，修改为以下方式
        boolean isWifiConnected = false;
        try {
            ConnectivityManager connecManager = (ConnectivityManager) Manager.getInstance().getConfig().appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = connecManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mNetworkInfo != null) {
                isWifiConnected = mNetworkInfo.isConnected();
            }
        } catch (Exception e) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, e.toString());
            }
        }
        return isWifiConnected;
    }

    /*
     * android 4.0
     */
    public static final boolean isIceCreamAboveVersion() {
        return android.os.Build.VERSION.SDK_INT >= 14;
    }

    /*
     * android 4.1
     */
    public static final boolean isJellyBeanAboveVersion() {
        return android.os.Build.VERSION.SDK_INT >= 16;
    }

    /*
     * android 5.0
     */
    public static final boolean isLollipopAboveVersion() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    /*
     * android 5.1
     */
    public static final boolean isLollipopOneAboveVersion() {
        return android.os.Build.VERSION.SDK_INT >= 22;
    }

    /*
     * android 6.0
     */
    public static final boolean isMarshmallowAboveVersion() {
        return android.os.Build.VERSION.SDK_INT >= 23;

    }

    /*
     * android 7.0
     */
    public static final boolean isNougatAboveVersion() {
        return android.os.Build.VERSION.SDK_INT >= 24;

    }

    /*
     * android 7.1
     */
    public static final boolean isUnderMaxSuportVersion() {
        return android.os.Build.VERSION.SDK_INT <= 24;

    }

    public static boolean isRooted() {

        File file = new File("/system/xbin/su");
        if (!file.exists()) {
            file = new File("/system/bin/su");
        }

        if (!file.exists()) {
            return false;
        }

        boolean isRooted = true;
        try {
            Runtime runtime = Runtime.getRuntime();

            runtime.exec("su");
        } catch (IOException e) {
            e.printStackTrace();
            isRooted = false;
        }

        return isRooted;
    }

    public static boolean isQiKuUI() {
        String value = getSystemProperty("ro.build.uiversion");
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return value.contains("360UI");
    }

    public static String getSystemProperty(String propertyKey) {
        String propertyValue = null;
        try {
            Object obj = invokeStaticMethod("android.os.SystemProperties", "get", new Class[]{String.class}, new Object[]{propertyKey});
            if (obj != null && obj instanceof String) {
                propertyValue = (String) obj;
            }
        } catch (Exception e) {
            //ignore
        }

        return propertyValue;
    }

    public static Object invokeStaticMethod(String clzName, String methodName, Class<?>[] methodParamTypes, Object... methodParamValues) {
        try {
            Class clz = Class.forName(clzName);
            if (clz != null) {
                Method med = clz.getMethod(methodName, methodParamTypes);
                if (med != null) {
                    med.setAccessible(true);
                    Object retObj = med.invoke(null, methodParamValues);
                    return retObj;
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "invokeStaticMethod got Exception:", e);
            }
        }
        return null;
    }

    public static final String sdcardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean checkPermission(String permission) {
        if (TextUtils.isEmpty(permission)) return false;

        Context context = Manager.getContext();
        if (null == context) return false;

        String packageName = context.getPackageName();
        if (TextUtils.isEmpty(packageName)) return false;

        PackageManager pm = context.getPackageManager();
        if (null == pm) return false;

        int permissionState = PackageManager.PERMISSION_DENIED;

        try {
            permissionState = pm.checkPermission(permission, packageName);
        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, e.toString());
        }

        return PackageManager.PERMISSION_GRANTED == permissionState ? true : false;
    }
}
