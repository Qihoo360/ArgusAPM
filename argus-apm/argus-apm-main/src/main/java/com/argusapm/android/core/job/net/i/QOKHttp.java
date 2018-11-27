package com.argusapm.android.core.job.net.i;

import com.argusapm.android.core.job.net.NetInfo;

/**
 * OkHttp相关，对外暴露的接口
 *
 * @author ArgusAPM Team
 */
public class QOKHttp {

    /**
     * 记录一次网络请求
     *
     * @param url          请求url
     * @param code         状态码
     * @param requestSize  发送的数据大小
     * @param responseSize 接收的数据大小
     * @param startTime    发起时间
     * @param costTime     耗时
     */
    public static void recordUrlRequest(String url, int code, long requestSize, long responseSize,
                                        long startTime, long costTime) {
        NetInfo netInfo = new NetInfo();
        netInfo.setStartTime(startTime);
        netInfo.setURL(url);
        netInfo.setStatusCode(code);
        netInfo.setSendBytes(requestSize);
        netInfo.setRecordTime(System.currentTimeMillis());
        netInfo.setReceivedBytes(responseSize);
        netInfo.setCostTime(costTime);
        netInfo.end();
    }
}