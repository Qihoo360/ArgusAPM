package com.argusapm.android.network;

import android.content.Context;

/**
 * 云规则网络请求接口
 *
 * @author ArgusAPM Team
 */
public interface IRuleRequest {
    /**
     * 请求apm配置数据
     * @param context
     * @param apmId
     * @param apmVer
     * @param appName
     * @param appVer
     * @return
     */
    String request(Context context, String apmId, String apmVer, String appName, String appVer);
}
