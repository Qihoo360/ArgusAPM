package com.argusapm.android.network;

import android.content.Context;

import java.util.Map;

/**
 * 上传数据采集结果的接口
 *
 * @author ArgusAPM Team
 */
public interface IUpload {
    /**
     * 上传采集到的数据
     *
     * @param apmId
     * @param data
     * @return
     */
    boolean upload(Context context, String apmId, Map<String, String> data);
}
