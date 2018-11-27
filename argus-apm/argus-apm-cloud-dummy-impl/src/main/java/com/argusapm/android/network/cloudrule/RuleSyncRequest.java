package com.argusapm.android.network.cloudrule;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.argusapm.android.network.IRuleRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.argusapm.android.network.DebugConfig.TAG_O;

/**
 * 云控同步请求
 *
 * @author ArgusAPM Team
 */
public class RuleSyncRequest implements IRuleRequest {

    @Override
    public String request(Context context, String apmId, String apmVer, String appName, String appVer) {
        // TODO: 模拟网络请求，直接返回请求成功的数据;如果有自己的云规则服务器，可以自行实现网络请求的逻辑
        return getCloudFileContent(context, "argus_apm_sdk_config.json");
    }

    //读取assets目录下的云规则文件
    private String getCloudFileContent(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG_O, "cloudrule response " + 0);

        return stringBuilder.toString();
    }
}
