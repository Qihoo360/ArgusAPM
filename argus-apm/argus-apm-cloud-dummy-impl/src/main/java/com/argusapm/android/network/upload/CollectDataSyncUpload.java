package com.argusapm.android.network.upload;

import android.content.Context;

import com.argusapm.android.network.IUpload;

import java.util.Map;

/**
 * 采集的数据上传的接口
 *
 * @author ArgusAPM Team
 */
public class CollectDataSyncUpload implements IUpload {

    @Override
    public boolean upload(Context context, String apmId, Map<String, String> data) {
        // TODO: 模拟上传成功，直接返回true,如果需要上传至自己的服务器，请自行实现
        return true;
    }
}
