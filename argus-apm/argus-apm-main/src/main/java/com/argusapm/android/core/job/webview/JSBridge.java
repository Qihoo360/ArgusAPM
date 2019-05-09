package com.argusapm.android.core.job.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class JSBridge {
    private static final String TAG = "JSBridge";
    public static final String JS_INTERFACE_NAME = "android_apm";
    public static final String JS_MONITOR = "javascript:%s.sendResource(JSON.stringify(window.performance.timing));";
    private String mThisUrl;

    public JSBridge(String url) {
        mThisUrl = url;
    }


    /**
     * 用于收集Timing信息
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void sendResource(String jsonStr) {
        dispatchToAopWebTrace(jsonStr, mThisUrl);
    }

    /**
     * 用于收集js的执行错误
     *
     * @param msg
     */
    @JavascriptInterface
    public void sendError(String msg) {

    }


    private void dispatchToAopWebTrace(String jsonStr, String url) {
        try {
            JSONObject source = new JSONObject(jsonStr);
            final JSONObject destObject = new JSONObject();
            destObject.put("ns", source.getLong("navigationStart"));
            destObject.put("pt", System.currentTimeMillis());
            destObject.put("rs", source.getLong("responseStart"));
            destObject.put("u", url);
            AopWebTrace.dispatch(destObject);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Method: addJavascriptInterface()" + e.getMessage());
        }
    }
}
