package com.argusapm.android.core.job.memory;

import android.content.ContentValues;

import com.argusapm.android.core.BaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 内存占用,单位kb
 *
 * @author ArgusAPM Team
 */
public class MemoryInfo extends BaseInfo {
    public String processName;
    public int dalvikPss;
    public int nativePss;
    public int otherPss;
    public int totalPss;

    public static final String KEY_PROCESS_NAME = "pn"; //processName
    public static final String KEY_DALVIK_PSS = "dp"; //dalvikPss
    public static final String KEY_NATIVE_PSS = "np"; //nativePss
    public static final String KEY_OTHER_PSS = "op"; //otherPss
    public static final String KEY_TOTAL_PSS = "tp"; //totalPss


    public MemoryInfo(int id, String processName, int totalPss, int dalvikPss, int nativePss, int otherPss) {
        this(id, 0, processName, totalPss, dalvikPss, nativePss, otherPss);
    }

    public MemoryInfo(int id, long time, String processName, int totalPss, int dalvikPss, int nativePss, int otherPss) {
        mId = id;
        this.processName = processName;
        this.totalPss = totalPss;
        this.dalvikPss = dalvikPss;
        this.nativePss = nativePss;
        this.otherPss = otherPss;
        recordTime = time;
    }

    public MemoryInfo(String processName, int totalPss, int dalvikPss, int nativePss, int otherPss) {
        this(-1, processName, totalPss, dalvikPss, nativePss, otherPss);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_PROCESS_NAME, processName)
                .put(KEY_TOTAL_PSS, totalPss)
                .put(KEY_DALVIK_PSS, dalvikPss)
                .put(KEY_NATIVE_PSS, nativePss)
                .put(KEY_OTHER_PSS, otherPss);
        return ori;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.processName = json.getString(KEY_PROCESS_NAME);
        this.dalvikPss = json.getInt(KEY_DALVIK_PSS);
        this.nativePss = json.getInt(KEY_NATIVE_PSS);
        this.otherPss = json.getInt(KEY_OTHER_PSS);
        this.totalPss = json.getInt(KEY_TOTAL_PSS);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_PROCESS_NAME, processName);
        values.put(KEY_TOTAL_PSS, totalPss);
        values.put(KEY_DALVIK_PSS, dalvikPss);
        values.put(KEY_NATIVE_PSS, nativePss);
        values.put(KEY_OTHER_PSS, otherPss);
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