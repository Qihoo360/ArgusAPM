package com.argusapm.android.core.job.activity;

import android.content.ContentValues;
import android.text.TextUtils;

import com.argusapm.android.core.BaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity信息类型
 *
 * @author ArgusAPM Team
 */
public class ActivityInfo extends BaseInfo {
    public static final String TAG = "ActivityInfo";
    /**
     * Activity生命周期类型枚举
     */
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_FIRST_FRAME = 1;
    public static final int TYPE_CREATE = 2;
    public static final int TYPE_START = 3;
    public static final int TYPE_RESUME = 4;
    public static final int TYPE_PAUSE = 5;
    public static final int TYPE_STOP = 6;
    public static final int TYPE_DESTROY = 7;
    /**
     * Activity生命周期类型值对应的名称
     */
    public static final String TYPE_STR_FIRSTFRAME = "firstFrame";
    public static final String TYPE_STR_ONCREATE = "onCreate";
    public static final String TYPE_STR_ONSTART = "onStart";
    public static final String TYPE_STR_ONRESUME = "onResume";
    public static final String TYPE_STR_ONPAUSE = "onPause";
    public static final String TYPE_STR_ONSTOP = "onStop";
    public static final String TYPE_STR_ONDESTROY = "onDestroy";
    public static final String TYPE_STR_UNKNOWN = "unKnown";
    /**
     * 启动类型枚举
     */
    public static final int COLD_START = 1; //冷启动
    public static final int HOT_START = 2; //热启动
    /**
     * activity DB数据私有字段
     */
    public static final String KEY_NAME = "n"; //activityName 完整类名
    public static final String KEY_TIME = "t";  //time 运行时间
    public static final String KEY_LIFE_CYCLE = "lc"; //life_cycle生命周期类型
    public static final String KEY_START_TYPE = "st"; //start_type启动类型（冷,热启动）
    /**
     * activityInfo对象属性
     */
    public String activityName = ""; //activity名称
    public int startType = 0; //启动类型
    public long time = 0;  //消耗时间
    public int lifeCycle = 0;  //生命周期
    public String pluginName = ""; //插件名称
    public String pluginVer = ""; //插件版本号

    public ActivityInfo() {
    }

    public void resetData() {
        this.mId = -1;
        this.activityName = "";
        this.recordTime = 0;
        this.startType = 0;
        this.time = 0;
        this.lifeCycle = 0;
        this.pluginName = "";
        this.pluginVer = "";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_NAME, activityName)
                .put(KEY_START_TYPE, startType)
                .put(KEY_TIME, time)
                .put(KEY_LIFE_CYCLE, lifeCycle)
                .put(BaseInfo.KEY_APP_NAME, pluginName)
                .put(BaseInfo.KEY_APP_VER, pluginVer);
        return ori;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.activityName = json.getString(KEY_NAME);
        this.startType = json.getInt(KEY_START_TYPE);
        this.time = json.getLong(KEY_TIME);
        this.lifeCycle = json.getInt(KEY_LIFE_CYCLE);
        this.pluginName = json.getString(BaseInfo.KEY_APP_NAME);
        this.pluginVer = json.getString(BaseInfo.KEY_APP_VER);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, activityName);
        values.put(KEY_START_TYPE, startType);
        values.put(KEY_TIME, time);
        values.put(KEY_LIFE_CYCLE, lifeCycle);
        values.put(BaseInfo.KEY_APP_NAME, pluginName);
        values.put(BaseInfo.KEY_APP_VER, pluginVer);
        return values;
    }

    /**
     * 生命周期数值转换成字符串
     */
    public String getLifeCycleString() {
        String lifeStr;
        switch (lifeCycle) {
            case TYPE_FIRST_FRAME:
                lifeStr = TYPE_STR_FIRSTFRAME;
                break;
            case TYPE_CREATE:
                lifeStr = TYPE_STR_ONCREATE;
                break;
            case TYPE_START:
                lifeStr = TYPE_STR_ONSTART;
                break;
            case TYPE_RESUME:
                lifeStr = TYPE_STR_ONRESUME;
                break;
            case TYPE_PAUSE:
                lifeStr = TYPE_STR_ONPAUSE;
                break;
            case TYPE_STOP:
                lifeStr = TYPE_STR_ONSTOP;
                break;
            case TYPE_DESTROY:
                lifeStr = TYPE_STR_ONDESTROY;
                break;
            default:
                lifeStr = TYPE_STR_UNKNOWN;
        }
        return lifeStr;
    }

    /**
     * 生命周期字符串转换成数值
     *
     * @param lcStr
     * @return
     */
    public static int ofLifeCycleString(String lcStr) {
        int lc = 0;
        if (TextUtils.equals(lcStr, TYPE_STR_FIRSTFRAME)) {
            lc = TYPE_FIRST_FRAME;
        } else if (TextUtils.equals(lcStr, TYPE_STR_ONCREATE)) {
            lc = TYPE_CREATE;
        } else if (TextUtils.equals(lcStr, TYPE_STR_ONSTART)) {
            lc = TYPE_START;
        } else if (TextUtils.equals(lcStr, TYPE_STR_ONRESUME)) {
            lc = TYPE_RESUME;
        } else if (TextUtils.equals(lcStr, TYPE_STR_ONPAUSE)) {
            lc = TYPE_PAUSE;
        } else if (TextUtils.equals(lcStr, TYPE_STR_ONSTOP)) {
            lc = TYPE_STOP;
        } else if (TextUtils.equals(lcStr, TYPE_STR_ONDESTROY)) {
            lc = TYPE_DESTROY;
        }
        return lc;
    }
}