package com.argusapm.android.core.job.anr;

import android.content.Context;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.network.UploadManager;
import com.argusapm.android.utils.LogX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ArgusAPM Team
 */
public abstract class AnrTask extends BaseTask {

    public static final String SUB_TAG = "AnrTask";
    public static final String ANR_DIR = "/data/anr/";

    protected Context mContext;
    protected AnrFileParser parser;

    public AnrTask(Context c) {
        mContext = c;
    }

    @Override
    public void start() {
        super.start();
        List<String> allowedList = ArgusApmConfigManager.getInstance().getArgusApmConfigData().anrFilter;
        if (Env.DEBUG) {
            if (allowedList == null || allowedList.isEmpty()) {
                LogX.d(Env.TAG, SUB_TAG, "anr filter is empty");
            } else {
                for (String str : allowedList) {
                    LogX.d(Env.TAG, SUB_TAG, "anr filter : " + str);
                }
            }
        }
        parser = new AnrFileParser(mContext, allowedList);
    }

    protected void handle(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        AnrInfo anrInfo = parser.parseFile(path);
        if (anrInfo != null) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, String.format("anr info : (%s, %s, %s)", anrInfo.getProId(), anrInfo.getTime(), anrInfo.getProName()));
            }
            Map<String, List<IInfo>> infoList = new HashMap<String, List<IInfo>>();
            List<IInfo> anrInfos = new ArrayList<IInfo>();
            anrInfos.add(anrInfo);
            infoList.put(ApmTask.TASK_ANR, anrInfos);
            boolean success = UploadManager.getInstance().upload(infoList);
            if (success) {
                parser.addUploadedPref(anrInfo.getProId(), anrInfo.getTime());
            }
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, String.format("anr info : (%s, %s, %s)", path, anrInfo.getAnrContent(), success ? "1" : "0"));
            }
            LogX.o(String.format("anr.upload %s %s %s", path, anrInfo.getProName(), success ? "1" : "0"));
        }
    }

    @Override
    protected IStorage getStorage() {
        return null;
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_ANR;
    }
}
