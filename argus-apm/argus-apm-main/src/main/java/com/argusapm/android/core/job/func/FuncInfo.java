package com.argusapm.android.core.job.func;

import android.content.ContentValues;
import android.text.TextUtils;

import com.argusapm.android.core.BaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ArgusAPM Team
 */
public class FuncInfo extends BaseInfo {
    public static final String SUB_TAG = "tracefunc";

    public static final int FUNC_TYPE_UNKNOWN = 0;
    public static final int FUNC_TYPE_RUN = 1;
    public static final int FUNC_TYPE_ONRECEIVE = 2;

    public static final String KEY_TYPE = "ty";

    public static final String KEY_COST = "cost";
    public static final String KEY_LOCATION = "loc";

    private int type = FUNC_TYPE_UNKNOWN;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_TYPE, type);
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
        if (json.has(KEY_TYPE)) {
            type = json.getInt(KEY_TYPE);
        }
        if (json.has(KEY_PARAM)) {
            params = json.getString(KEY_PARAM);
        }
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_PARAM, params);
        return values;
    }
}
