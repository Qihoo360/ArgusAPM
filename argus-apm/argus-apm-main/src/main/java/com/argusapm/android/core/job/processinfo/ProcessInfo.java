package com.argusapm.android.core.job.processinfo;

import android.content.ContentValues;

import com.argusapm.android.core.BaseInfo;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 进程信息
 *
 * @author ArgusAPM Team
 */
public class ProcessInfo extends BaseInfo {
    private final String SUB_TAG = "ProcessInfo";


    public String processName;//进程名称
    public int startCount = 1;//启动次数

    public static class DBKey {
        public static final String PROCESS_NAME = "pn"; //进程名称
        public static final String START_COUNT = "sc"; //进程启动次数
    }

    public ProcessInfo() {
        processName = ProcessUtils.getCurrentProcessName();
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(DBKey.PROCESS_NAME, processName)
                .put(DBKey.START_COUNT, startCount);
        return ori;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.processName = json.getString(DBKey.PROCESS_NAME);
        this.startCount = json.getInt(DBKey.START_COUNT);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        try {
            values.put(DBKey.PROCESS_NAME, processName);
            values.put(DBKey.START_COUNT, startCount);
        } catch (Exception e) {
            if (DEBUG) {
                LogX.e(TAG, SUB_TAG, e.toString());
            }
        }
        return values;
    }
}
