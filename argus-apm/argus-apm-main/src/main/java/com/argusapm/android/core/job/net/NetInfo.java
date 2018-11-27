
package com.argusapm.android.core.job.net;

import android.content.ContentValues;
import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.BaseInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.core.tasks.ITask;
import com.argusapm.android.debug.AnalyzeManager;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class NetInfo extends BaseInfo {
    private static final String SUB_TAG = "NetInfo";

    public String url = "";
    public long sentBytes = 0;
    public long receivedBytes = 0;
    public long startTime = 0;
    public long costTime = 0;
    public boolean isWifi = false;
    public int statusCode = 0;
    public int errorCode = 0;

    public static final String KEY_URL = "u"; //url 访问的url
    public static final String KEY_SEND_BYTES = "sb"; //send_bytes 发送数据字节大小
    public static final String KEY_RECEIVE_BYTES = "rb"; //receive_bytes 接收数据字节大小
    public static final String KEY_TIME_START = "t"; //触发具体时间
    public static final String KEY_TIME_COST = "tc"; //连接时间
    public static final String KEY_IS_WIFI = "w"; //is_wifi 是否为wifi状态
    public static final String KEY_STATUS_CODE = "sc"; //状态码
    public static final String KEY_ERROR_CODE = "ec"; //错误码

    public NetInfo(int id) {
        mId = id;
        startTime = System.currentTimeMillis();
    }

    public NetInfo() {
        this(-1);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_URL, url)
                .put(KEY_STATUS_CODE, statusCode)
                .put(KEY_ERROR_CODE, errorCode)
                .put(KEY_SEND_BYTES, sentBytes)
                .put(KEY_RECEIVE_BYTES, receivedBytes)
                .put(KEY_IS_WIFI, isWifi)
                .put(KEY_TIME_START, startTime)
                .put(KEY_TIME_COST, costTime);
        return ori;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.url = json.getString(KEY_URL);
        this.statusCode = json.getInt(KEY_STATUS_CODE);
        this.errorCode = json.getInt(KEY_ERROR_CODE);
        this.sentBytes = json.getLong(KEY_SEND_BYTES);
        this.receivedBytes = json.getLong(KEY_RECEIVE_BYTES);
        this.isWifi = json.getBoolean(KEY_IS_WIFI);
        this.startTime = json.getLong(KEY_TIME_START);
        this.costTime = json.getLong(KEY_TIME_COST);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_URL, url);
        values.put(KEY_STATUS_CODE, statusCode);
        values.put(KEY_ERROR_CODE, errorCode);
        values.put(KEY_SEND_BYTES, sentBytes);
        values.put(KEY_RECEIVE_BYTES, receivedBytes);
        values.put(KEY_IS_WIFI, isWifi);
        values.put(KEY_TIME_START, startTime);
        values.put(KEY_TIME_COST, costTime);
        return values;
    }

    /**
     * 为什存储的操作要写到这里呢?
     * 历史原因
     */
    public void end() {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "end :");
        }
        this.isWifi = SystemUtils.isWifiConnected();
        this.costTime = System.currentTimeMillis() - startTime;
        if (AnalyzeManager.getInstance().isDebugMode()) {
            AnalyzeManager.getInstance().getNetTask().parse(this);
        }
        ITask task = Manager.getInstance().getTaskManager().getTask(ApmTask.TASK_NET);
        if (task != null) {
            task.save(this);
        } else {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "task == null");
            }
        }
    }

    public void setURL(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.sentBytes = url.getBytes().length;
            this.url = sanitizeUrl(url);
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "setURL-url: " + url + ",-upLoadSize: " + sentBytes + " ,url: " + this.url.getBytes().length);
            }
        }
    }

    private String sanitizeUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
        String portStr = url.getPort() == -1 ? "" : ":" + url.getPort();
        return TextUtils.concat(
                url.getProtocol(),
                "://",
                url.getHost(),
                portStr,
                url.getPath()
        ).toString();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setSendBytes(long uploadSize) {
        this.sentBytes = uploadSize;
    }

    public void setReceivedBytes(long downloadSize) {
        this.receivedBytes = downloadSize;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }
}