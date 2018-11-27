package com.argusapm.android.okhttp3;

import android.text.TextUtils;
import android.util.Log;

import com.argusapm.android.core.job.net.i.QOKHttp;

/**
 * 具体实现逻辑，位于mobile module中
 *
 * @author ArgusAPM Team
 */
public class DataRecordUtils {

    /**
     * recordUrlRequest
     *
     * @param okHttpData
     */
    public static void recordUrlRequest(OkHttpData okHttpData) {
        if (okHttpData == null || TextUtils.isEmpty(okHttpData.url)) {
            return;
        }

        QOKHttp.recordUrlRequest(okHttpData.url, okHttpData.code, okHttpData.requestSize,
                okHttpData.responseSize, okHttpData.startTime, okHttpData.costTime);

        if (Env.DEBUG) {
            Log.d(Env.TAG, "存储okkHttp请求数据，结束。");
        }
    }
}