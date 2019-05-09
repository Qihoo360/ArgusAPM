
package com.argusapm.android.core.job.func;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.tasks.ITask;
import com.argusapm.android.utils.AspectjUtils;
import com.argusapm.android.utils.CommonUtils;
import com.argusapm.android.utils.LogX;
import com.argusapm.android.utils.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ArgusAPM Team
 */
public class FuncTrace {
    public static final String SUB_TAG = "tracefunc";

    public synchronized static void dispatch(long startTime, String kind, String sign, Context context, Intent intent,
                                             Object target, Object thiz, String location,
                                             String staticPartStr, String methodName, Object result) {
        Object[] args = new Object[2];
        args[0] = context;
        args[1] = intent;
        dispatch(startTime, kind, sign, args, target, thiz, location, staticPartStr, methodName, result);
    }

    public synchronized static void dispatch(long startTime, String kind, String sign, Object[] args,
                                             Object target, Object thiz, String location,
                                             String staticPartStr, String methodName, Object result) {

        if (!Manager.getInstance().getTaskManager().taskIsCanWork(ApmTask.TASK_FUNC)) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "func task is not work");
            }
            return;
        }


        if (TextUtils.isEmpty(kind) ||
                TextUtils.isEmpty(sign) ||
                TextUtils.isEmpty(methodName) ||
                TextUtils.isEmpty(location)
        ) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "params is empty");
            }
            return;
        }

        long cost = System.currentTimeMillis() - startTime;
        if (Env.DEBUG) {
            if (!TextUtils.isEmpty(methodName) && !methodName.contains("read") && !methodName.contains("write")) {
                LogX.d(Env.TAG, SUB_TAG, String.format(
                        "info [cost:%sms, kind:%s, sign:%s, target:%s, this: %s, location:%s, StaticPart:%s]",
                        cost,
                        kind,
                        sign,
                        target,
                        thiz,
                        location,
                        staticPartStr
                ));
                if (args != null && args.length > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("[");
                    for (Object arg : args) {
                        stringBuilder.append(arg).append(",");
                    }
                    stringBuilder.append("]");
                    LogX.d(Env.TAG, SUB_TAG, "invoke args :" + stringBuilder);
                } else {
                    LogX.d(Env.TAG, SUB_TAG, "invoke args : null");
                }
            }
        }

        if (TextUtils.equals(kind, AspectjUtils.JOINPOINT_KIND_EXECUTION_METHOD) ||
                TextUtils.equals(kind, AspectjUtils.JOINPOINT_KIND_CALL_METHOD)) {
            int type = FuncInfo.FUNC_TYPE_UNKNOWN;
            long minTime = Long.MAX_VALUE;
            if (methodName.equals("run")) {
                type = FuncInfo.FUNC_TYPE_RUN;
                minTime = ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.threadMinTime;
            } else if (methodName.equals("onReceive")) {
                type = FuncInfo.FUNC_TYPE_ONRECEIVE;
                minTime = ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.onreceiveMinTime;
            } else {

            }

            if (type == FuncInfo.FUNC_TYPE_UNKNOWN) {
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "unknown func type");
                }
                return;
            }
            if (cost < minTime) {
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, String.format("[min:%s, real:%s, ignore]", minTime, cost));
                }
                return;
            }

            FuncInfo info = new FuncInfo();
            info.setType(type);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(FuncInfo.KEY_PROCESS_NAME, ProcessUtils.getCurrentProcessName());
                jsonObject.put(FuncInfo.KEY_THREAD_NAME, Thread.currentThread().getName());
                jsonObject.put(FuncInfo.KEY_THREAD_ID, Thread.currentThread().getId());
                jsonObject.put(FuncInfo.KEY_COST, cost);
                jsonObject.put(FuncInfo.KEY_LOCATION, location);
                jsonObject.put(FuncInfo.KEY_STACK_NAME, CommonUtils.getStack());

                if (type == FuncInfo.FUNC_TYPE_ONRECEIVE) {
                    if (args != null && args.length >= 2) {
                        try {
                            Intent intent = (Intent) args[1];
                            String action = intent.getAction();
                            if (!TextUtils.isEmpty(action)) {
                                jsonObject.put("action", action);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            info.setParams(jsonObject.toString());
            ITask task = Manager.getInstance().getTaskManager().getTask(ApmTask.TASK_FUNC);
            if (task != null) {
                task.save(info);
            }
        }
    }
}
