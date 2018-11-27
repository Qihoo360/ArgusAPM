package com.argusapm.android.core.job.appstart;

import android.content.ContentValues;

import com.argusapm.android.core.BaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * App启动时间Info
 *
 * @author ArgusAPM Team
 */
public class AppStartInfo extends BaseInfo {
    public static String KEY_START_TIME = "st";

    private int mStartTime;

    public AppStartInfo(int id, int startTime) {
        this(id, 0, startTime);
    }

    public AppStartInfo(int id, long recordTime, int startTime) {
        mId = id;
        mStartTime = startTime;
        this.recordTime = recordTime;
    }


    public AppStartInfo(int startTime) {
        this(-1, startTime);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_START_TIME, mStartTime);
        return ori;
    }

    public int getStartTime() {
        return mStartTime;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.mStartTime = json.getInt(KEY_START_TIME);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_START_TIME, mStartTime);
        return values;
    }

    @Override
    public String toString() {
        String value;
        try {
            value = toJson().toString();
        } catch (Exception e) {
            value = super.toString();
        }
        return value;
    }

}
