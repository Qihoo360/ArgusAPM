package com.argusapm.android.cloudconfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.network.CloudRule;
import com.argusapm.android.cloudconfig.data.ArgusApmConfigData;
import com.argusapm.android.core.Manager;
import com.argusapm.android.helper.ApmDebugEnable;
import com.argusapm.android.network.IRuleRequest;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.ExtraInfoHelper;
import com.argusapm.android.utils.FileUtils;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.PreferenceUtils;
import com.argusapm.android.utils.ProcessUtils;

import java.util.Collection;

import static com.argusapm.android.Env.TAG_O;

/**
 * Argus APM 配置数据管理类
 *
 * @author ArgusAPM Team
 */
public class ArgusApmConfigManager {
    public final String SUB_TAG = "ArgusApmConfigManager";
    protected Context mContext;
    private ArgusApmConfigData argusApmConfigData;// 云控数据都在这个类里
    private long mLastTime; //上次更新云控时间
    private CloudRule mCloudRule;

    private static ArgusApmConfigManager mInstance;

    public static ArgusApmConfigManager getInstance() {
        if (null == mInstance) {
            synchronized (ArgusApmConfigManager.class) {
                if (null == mInstance) {
                    mInstance = new ArgusApmConfigManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void initArgusApmData(Context context, IRuleRequest ruleRequest) {
        mContext = context;
        initLocalData();
        initCloud(ruleRequest);
        register();
    }

    /**
     * 初始化云控逻辑
     */
    private void initCloud(IRuleRequest ruleRequest) {
        if (Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_CLOUD_UPDATE)) {//只需要在一个进程中进行云控逻辑
            mLastTime = PreferenceUtils.getLong(mContext, PreferenceUtils.SP_KEY_LAST_UPDATE_TIME, 0);
            mCloudRule = new CloudRule(mContext, ruleRequest);
            startLoadCloudData(Constant.APP_START_CLOUD_MAX_DELAY_TIME);
        }
    }

    /**
     * 加载配置文件（本地）
     */
    public void initLocalData() {
        String data = "";
        if (Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_LOCAL_DEBUG)) {
            data = ApmDebugEnable.load(); //读取本地配置
        }
        if (TextUtils.isEmpty(data)) {
            data = readConfigFile(); //读取app私有目录缓存文件
        }
        if (data.length() > 0) {
            LogX.o(TAG_O, SUB_TAG, "initLocalData success");
        }
        if (argusApmConfigData == null) {
            argusApmConfigData = new ArgusApmConfigData();
        }
        argusApmConfigData.parseData(data);
        notifyUpdate();
    }

    private void register() {
        try {
            IntentFilter filter = new IntentFilter();
            if (Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_CLOUD_UPDATE)) {
                filter.addAction(Intent.ACTION_USER_PRESENT);
            }
            filter.addAction(Constant.CLOUD_RULE_UPDATE_ACTION);
            mContext.registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unRegister() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Intent.ACTION_USER_PRESENT)) {
                startLoadCloudData(getArgusApmConfigData().randomControlTime);
            } else if (TextUtils.equals(action, Constant.CLOUD_RULE_UPDATE_ACTION)) {
                // 收到该广播，则更新所有配置
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "onReceive CLOUD_RULE_UPDATE_ACTION, reload sdk");
                }
                Manager.getInstance().reload();
            }
        }
    };

    /**
     * 开始准备加载云控配置
     */
    private void startLoadCloudData(long maxDelayTime) {
        if (mCloudRule == null) {
            return;
        }
        long min = getArgusApmConfigData().cloudInterval;
        long curtime = System.currentTimeMillis();
        long delta = curtime - mLastTime;

        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, String.format("recv ACTION_USER_PRESENT [delta:%s, min:%s]", delta, min));
        }
        if (delta > min) {
            long real_random_time = 2500;
            if (maxDelayTime > 0) {
                real_random_time = (long) (Math.random() * maxDelayTime % maxDelayTime);
            }
            AsyncThreadTask.executeDelayed(new Runnable() {
                @Override
                public void run() {
                    LogX.o(TAG_O, SUB_TAG, "start down cloud file");
                    if (Env.DEBUG) {
                        LogX.d(Env.TAG, SUB_TAG, "开始请求云配置");
                    }
                    mCloudRule.request();
                }
            }, 2500 + real_random_time);
            if (mContext != null) {
                PreferenceUtils.setLong(mContext, PreferenceUtils.SP_KEY_LAST_UPDATE_TIME, curtime);
            }
            mLastTime = curtime;
        }
    }

    @Override
    public String toString() {
        return argusApmConfigData.toString();
    }

    public void useDefaultConfig() {
        argusApmConfigData = new ArgusApmConfigData();
        Collection<Integer> flags = ApmTask.getTaskMap().values();
        for (Integer f : flags) {
            argusApmConfigData.configCore.setEnabled(f);
        }
        argusApmConfigData.debug = true;
        notifyUpdate();
    }

    private String readConfigFile() {
        return FileUtils.readFile(FileUtils.getApmConfigFilePath(mContext));
    }

    /**
     * 发送云控更新通知，主要用于插件化
     */
    private void notifyUpdate() {
        if (Manager.getInstance().getConfig().isEnabled(ApmTask.FLAG_CLOUD_UPDATE)) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "notifyUpdate proc : " + ProcessUtils.getCurrentProcessName());
            }
            Collection<String> tasks = ApmTask.getTaskMap().keySet();
            for (String task : tasks) {
                //此开关设置为数据库写入时的开关。此处开关设置应该为云控文件的开关为准，不应该以为常驻进程的模块开关为准
                ExtraInfoHelper.notifyV5Update(task, getDBTaskEnable(task));
            }
        }
    }

    private boolean getDBTaskEnable(String task) {
        if (argusApmConfigData == null) {
            return false;
        }
        if (argusApmConfigData.configCore == null) {
            return false;
        }
        return argusApmConfigData.configCore.isEnabled(task);
    }

    public ArgusApmConfigData getArgusApmConfigData() {
        if (argusApmConfigData == null) {
            argusApmConfigData = new ArgusApmConfigData();
        }
        return argusApmConfigData;
    }

}
