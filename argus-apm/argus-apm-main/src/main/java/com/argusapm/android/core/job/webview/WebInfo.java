package com.argusapm.android.core.job.webview;

import android.content.ContentValues;

import com.argusapm.android.core.BaseInfo;
import com.argusapm.android.utils.LogX;

import org.json.JSONException;
import org.json.JSONObject;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 网页性能信息
 *
 * @author ArgusAPM Team
 */
public class WebInfo extends BaseInfo {

    private final String SUB_TAG = "WebInfo";

    public String url;
    public boolean isWifi = false;

    /**
     * 当前浏览器窗口的前一个网页关闭，发生unload事件时的Unix毫秒时间戳。如果没有前一个网页，
     * 则等于fetchStart属性。
     */
    public long navigationStart;
    /**
     * 返回浏览器从服务器收到（或从本地缓存读取）第一个字节时的Unix毫秒时间戳。
     */
    public long responseStart;
    public long pageTime;


    public static class DBKey {
        public static final String KEY_URL = "u";
        public static final String KEY_IS_WIFI = "w";
        public static final String KEY_NAVIGATION_START = "ns";
        public static final String KEY_RESPONSE_START = "rs";
        public static final String KEY_PAGE_TIME = "pt";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(DBKey.KEY_URL, url)
                .put(DBKey.KEY_IS_WIFI, isWifi)
                .put(DBKey.KEY_NAVIGATION_START, navigationStart)
                .put(DBKey.KEY_RESPONSE_START, responseStart)
                .put(DBKey.KEY_PAGE_TIME, pageTime);
        return ori;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.url = json.getString(DBKey.KEY_URL);
        this.isWifi = json.getBoolean(DBKey.KEY_IS_WIFI);
        this.navigationStart = json.getLong(DBKey.KEY_NAVIGATION_START);
        this.responseStart = json.getLong(DBKey.KEY_RESPONSE_START);
        this.pageTime = json.getLong(DBKey.KEY_PAGE_TIME);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        try {
            values.put(DBKey.KEY_URL, url);
            values.put(DBKey.KEY_IS_WIFI, isWifi);
            values.put(DBKey.KEY_NAVIGATION_START, navigationStart);
            values.put(DBKey.KEY_RESPONSE_START, responseStart);
            values.put(DBKey.KEY_PAGE_TIME, pageTime);
        } catch (Exception e) {
            if (DEBUG) {
                LogX.e(TAG, SUB_TAG, e.toString());
            }
        }
        return values;

    }
}
