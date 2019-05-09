package com.argusapm.android.core.job.webview;

import org.json.JSONObject;

/**
 * 通过AOP切面调用，不要轻易更改文件所有的包路径和类名
 *
 * @author ArgusAPM Team
 */
public class AopWebTrace {
    public static void dispatch(JSONObject jsonObject) {
        WebTrace.dispatch(jsonObject);
    }
}
