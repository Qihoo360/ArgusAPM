package com.argusapm.android.core.job.anr;

import com.argusapm.android.core.BaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ANR信息类型
 *
 * @author ArgusAPM Team
 */
public class AnrInfo extends BaseInfo {
    public static final String KEY_PRO_NAME = "P";
    public static final String KEY_PRO_ID = "pid";
    public static final String KEY_CONTENT = "c";
    public static final String KEY_TIME = "t";
    private String proName;
    private long time;
    private String anrContent;
    private long proId;

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAnrContent() {
        return anrContent;
    }

    public void setAnrContent(String anrContent) {
        this.anrContent = anrContent;
    }

    public long getProId() {
        return proId;
    }

    public void setProId(long proId) {
        this.proId = proId;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson()
                .put(KEY_PRO_NAME, proName)
                .put(KEY_CONTENT, anrContent)
                .put(KEY_PRO_ID, proId)
                .put(KEY_TIME, time);
        return ori;
    }

}
