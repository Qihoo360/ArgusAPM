package com.argusapm.android.core.job.fps;

import android.content.ContentValues;
import android.text.TextUtils;

import com.argusapm.android.core.BaseInfo;
import com.argusapm.android.utils.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ArgusAPM Team
 */
public class FpsInfo extends BaseInfo {
    protected static int FPS_SAMPLE_TYPE_CHOREOGRAPHER = 0;
    protected static int FPS_SAMPLE_TYPE_SURFACEFLINGER = 1;

    private String activity;
    private int fps;
    private String processName;


    public static final String KEY_TYPE = "ty";// fps类型
    public static final String KEY_FPS = "f";
    public static final String KEY_ACTIVITY = "ac";
    public static final String KEY_STACK = "s";


    public int getFpsType() {
        return FPS_SAMPLE_TYPE_CHOREOGRAPHER;
    }

    public FpsInfo(int id) {
        mId = id;
    }

    public FpsInfo() {
        this(-1);
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getFps() {
        return fps;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_FPS, fps)
                .put(KEY_ACTIVITY, activity)
                .put(KEY_TYPE, getFpsType())
                .put(KEY_PROCESS_NAME, processName);

        if (!TextUtils.isEmpty(params)) {
            ori.put(KEY_PARAM, params);
        }
        return ori;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        activity = json.getString(KEY_ACTIVITY);
        fps = json.getInt(KEY_FPS);
        params = json.getString(KEY_PARAM);
        if (json.has(KEY_PROCESS_NAME)) {
            processName = json.getString(KEY_PROCESS_NAME);
        }
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_FPS, fps);
        values.put(KEY_ACTIVITY, activity);
        values.put(KEY_TYPE, getFpsType());
        values.put(KEY_PARAM, params);
        values.put(KEY_PROCESS_NAME, processName);
        return values;
    }
}