package com.argusapm.android.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.api.Client;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.storage.DataHelper;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.PreferenceUtils;
import com.argusapm.android.utils.SystemUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;
import static com.argusapm.android.Env.TAG_O;

/**
 * 数据上传管理类
 *
 * @author ArgusAPM Team
 */
public class UploadManager {
    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static UploadManager instance;
    private final String SUB_TAG = "UploadManager";
    private Context mContext;
    private IUpload mUploader;

    public static UploadManager getInstance() {
        if (instance == null) {
            synchronized (UploadManager.class) {
                if (instance == null) {
                    instance = new UploadManager();
                }
            }
        }
        return instance;
    }

    /**
     * 构造函数
     */
    private UploadManager() {

    }

    /**
     * 初始化操作
     *
     * @param context
     */
    public void init(Context context, IUpload upload) {
        mContext = context;
        mUploader = upload;
        try {
            mContext.registerReceiver(receiver, new IntentFilter(ACTION_CONNECTIVITY_CHANGE));
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "init ex : " + Log.getStackTraceString(e));
            }
        }
    }

    /**
     * 开始启动上传操作
     */
    private void startUpload() {
        AsyncThreadTask.execute(new Runnable() {
            @Override
            public void run() {
                uploadData();
            }
        });
    }

    /**
     * 销毁
     */
    public void destroy() {
        try {
            mContext.unregisterReceiver(receiver);
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "UploadManager destroy");
            }
        } catch (Exception e) {
            LogX.d(TAG, SUB_TAG, "destroy error : " + Log.getStackTraceString(e));
        }
    }

    public void testUploadData() {
        startUpload();
    }


    /**
     * 开始数据上传
     */
    private void uploadData() {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "uploadData: 数据上传");
        }
        final DataHelper dataHelper = new DataHelper();
        LogX.o(TAG_O, SUB_TAG, "begin uploadData ");
        dataHelper.readAll(new DataHelper.DataHandler() {
            @Override
            public void onStart() {
                if (Env.DEBUG) {
                    LogX.d(TAG, SUB_TAG, "uploadData start ...");
                }
            }

            @Override
            public boolean onRead(Map<String, List<IInfo>> infoList) {
                boolean success = upload(infoList);
                LogX.o(TAG_O, SUB_TAG, "upload.state " + Integer.toHexString(infoList.hashCode()) + (success ? " 1" : " 0"));
                if (success) {
                    updateTime();
                }
                return success;
            }

            @Override
            public void onEnd() {
                if (Env.DEBUG) {
                    LogX.d(TAG, SUB_TAG, "uploadData finish ");
                }
            }
        });
    }

    public boolean upload(Map<String, List<IInfo>> infoList) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("apm_data", parseDataToJson(infoList).toString());
        if (mUploader == null) {
            return false;
        }
        //失败重试逻辑
        int reUploadCount = UploadConfig.RETRY_COUNT;
        boolean success;
        while (true) {
            success = mUploader.upload(Client.getContext(), Manager.getInstance().getConfig().apmId, data);
            if (reUploadCount > 0 && (!success)) {
                reUploadCount--;
            } else {
                break;
            }
        }
        LogX.o(TAG_O, SUB_TAG, "upload.state.c " + Integer.toHexString(infoList.hashCode()) + (success ? " 1" : " 0"));
        return success;
    }

    /**
     * 把上传数据解析成json
     *
     * @param data
     * @return
     */
    private JSONObject parseDataToJson(Map<String, List<IInfo>> data) {
        JSONObject jsonRoot = new JSONObject();
        try {
            for (Map.Entry<String, List<IInfo>> entry : data.entrySet()) {
                JSONArray array = new JSONArray();
                for (IInfo info : entry.getValue()) {
                    array.put(info.toJson());
                }
                jsonRoot.put(entry.getKey(), array);
            }
            // 手机系统参数
            jsonRoot.put(UploadInfoField.KEY_MODE, android.os.Build.MODEL);
            jsonRoot.put(UploadInfoField.KEY_MANUFACTURE, android.os.Build.MANUFACTURER);
            jsonRoot.put(UploadInfoField.KEY_SDKINI, android.os.Build.VERSION.SDK_INT);
            jsonRoot.put(UploadInfoField.KEY_RELEASEVERSION, android.os.Build.VERSION.RELEASE);
            //公共字段
            jsonRoot.put(UploadInfoField.KEY_FRAME_VER, Manager.getInstance().getConfig().appVersion);
            jsonRoot.put(UploadInfoField.KEY_APMID, Manager.getInstance().getConfig().apmId);
            jsonRoot.put(UploadInfoField.KEY_APMVER, Env.getVersionName());
            jsonRoot.put(UploadInfoField.KEY_TIME, System.currentTimeMillis());
        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, "parseDataToJson error:" + e.toString());
        }
        return jsonRoot;
    }

    /**
     * 网络变化监听，当网络变化时，尝试数据上传
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) LogX.d(TAG, SUB_TAG, "onReceive: " + intent.getAction());
            if (!ACTION_CONNECTIVITY_CHANGE.equalsIgnoreCase(intent.getAction()) || !needWork()) {
                return;
            }
            startUpload();
        }

        private boolean needWork() {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "isWifiConnected: " + SystemUtils.isWifiConnected());
            }
            return SystemUtils.isWifiConnected() && checkTime();
        }
    };

    private boolean checkTime() {
        long diff = System.currentTimeMillis() - PreferenceUtils.getLong(mContext, PreferenceUtils.SP_KEY_DISPOSE_ITEM, 0);
        boolean res = diff > ArgusApmConfigManager.getInstance().getArgusApmConfigData().uploadInterval;
        if (DEBUG) LogX.d(TAG, SUB_TAG, "checkTime: " + diff + " update = " + res);
        return res;
    }

    private void updateTime() {
        PreferenceUtils.setLong(mContext, PreferenceUtils.SP_KEY_DISPOSE_ITEM, System.currentTimeMillis());
    }

}
