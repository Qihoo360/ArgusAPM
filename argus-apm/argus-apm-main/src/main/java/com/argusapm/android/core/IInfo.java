package com.argusapm.android.core;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 通用基本数据接口
 *
 * @author ArgusAPM Team
 */
public interface IInfo extends Serializable {
    // 目前是数据库自增字段
    int getId();

    JSONObject toJson() throws JSONException;

    void parserJsonStr(String json) throws JSONException;

    void parserJson(JSONObject json) throws JSONException;

    ContentValues toContentValues();

}