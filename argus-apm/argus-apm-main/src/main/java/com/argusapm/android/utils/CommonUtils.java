package com.argusapm.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class CommonUtils {

    private static final String SUB_TAG = "CommonUtils";
    private static final String DEFAULT_IMEI = "360_DEFAULT_IMEI";
    private static String sImei = DEFAULT_IMEI;

    public static String getStack() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new Throwable().printStackTrace(pw);
        String stacks = sw.toString();
        if (!TextUtils.isEmpty(stacks)) {
            String[] lines = stacks.split("\n\tat");
            StringBuilder sb = new StringBuilder();

            final int start = 4;
            final int end = Math.min(start + 10, lines.length);
            // 前4行没有意义，都是aop带来的额外开销，最多只取10行
            for (int i = start; i < end; i++) {
                sb.append(lines[i]).append("\n\tat");
            }
            return sb.toString();
        }

        return "";
    }

    public static String getDefaultImsi(Context context) {
        String imsi = "no-permission";

        if (context != null) {
            try {
                TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService("phone");
                if (telephonyMgr != null) {
                    imsi = telephonyMgr.getSubscriberId();
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return imsi;
    }

    public static String getMid2(Context context) {
        String imei = getImei(context);
        String androidId = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
        String serialNo = getDeviceSerialForMid2();
        String m2 = getMD5(imei + androidId + serialNo);
        return m2;
    }

    public static String getMD5(String input) {
        return input == null ? "" : getMD5(input.getBytes());
    }

    public static String getMD5(byte[] input) {
        return bytesToHexString(MD5(input));
    }

    public static byte[] MD5(byte[] input) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        if (md != null) {
            md.update(input);
            return md.digest();
        } else {
            return null;
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            String table = "0123456789abcdef";
            StringBuilder ret = new StringBuilder(2 * bytes.length);
            byte[] var6 = bytes;
            int var5 = bytes.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                byte c = var6[var4];
                int b = 15 & c >> 4;
                ret.append(table.charAt(b));
                b = 15 & c;
                ret.append(table.charAt(b));
            }

            return ret.toString();
        }
    }

    private static String getDeviceSerialForMid2() {
        String serial = "";
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", new Class[]{String.class});
            serial = (String) get.invoke(c, new Object[]{"ro.serialno"});
        } catch (Exception localException) {
        }
        return serial;
    }

    public static String getImei(Context ctx) {
        if (!TextUtils.equals(sImei, DEFAULT_IMEI)) {
            return sImei;
        }

        if (ctx != null) {
            getDeviceId(ctx);
        }

        return sImei;
    }

    private static void getDeviceId(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService("phone");

        try {
            if (tm != null && tm.getDeviceId() != null) {
                sImei = tm.getDeviceId();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private static NetworkInfo getWiFiNetworkInfo(Context ct) {
        NetworkInfo networkInfo = null;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, e.toString());
        }

        return networkInfo;
    }

    public static boolean isWiFiConnected(Context ct) {
        NetworkInfo networkInfo = getWiFiNetworkInfo(ct);
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String getVersionName(Context c) {
        if (c == null) {
            return null;
        }
        PackageInfo pi = null;
        try {
            pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (pi != null ? pi.versionName : null);
    }
}