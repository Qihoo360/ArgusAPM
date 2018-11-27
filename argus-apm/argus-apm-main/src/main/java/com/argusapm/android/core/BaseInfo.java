package com.argusapm.android.core;

import android.content.ContentValues;

import com.argusapm.android.utils.ExtraInfoHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ArgusAPM Team
 */
public abstract class BaseInfo implements IInfo {
    public static final String KEY_ID_RECORD = "id";//数据表自增主键
    public static final String KEY_TIME_RECORD = "tr";//写入时的时间
    // 扩展字段，每种采集类型的参数保持不变，后续如果有添加或者修改，通过写入一个json到该字段，即可
    public static final String KEY_PARAM = "par";//param_record
    // KEY_PARAM有该字段的前提下，基本无用
    public static final String KEY_RESERVE_1 = "r1";
    // KEY_PARAM有该字段的前提下，基本无用
    public static final String KEY_RESERVE_2 = "r2";

    public static final String KEY_APP_NAME = "an"; //插件名称/APP名称
    public static final String KEY_APP_VER = "av";//插件版本号/App版本号

    // 定义一些每个模块都可能用到的公共字段
    public static final String KEY_PROCESS_NAME = "pn";
    public static final String KEY_THREAD_NAME = "tn";
    public static final String KEY_THREAD_ID = "tid";
    public static final String KEY_STACK_NAME = "sn";

    // 从数据库里读出的id有效，其他途径赋值无效
    protected int mId = -1;

    protected String params;

    protected long recordTime;

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(KEY_TIME_RECORD, recordTime);

    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {

    }

    @Override
    public void parserJson(JSONObject json) throws JSONException {

    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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
