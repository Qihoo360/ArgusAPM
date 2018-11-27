package com.argusapm.android.core.storage;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.utils.LogX;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.argusapm.android.Env.TAG_O;

/**
 * @author ArgusAPM Team
 */
public class DataHelper {
    public static final String SUB_TAG = "DataHelper";

    private static final int LIMIT = 1000;
    private static DataHelper instance;

    public static DataHelper getInstance() {
        if (instance == null) {
            synchronized (DataHelper.class) {
                if (instance == null) {
                    instance = new DataHelper();
                }
            }
        }
        return instance;
    }

    public static Map<String, List<IInfo>> readAll() {
        Map<String, List<IInfo>> dataMap = new HashMap<String, List<IInfo>>();
        for (IStorage storage : ApmTask.sAllStorage) {
            List<IInfo> data = storage.getAll();
            if (data == null || data.isEmpty()) {
                continue;
            }
            dataMap.put(storage.getName(), data);
        }

        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "dataMap = " + dataMap.toString());
        }
        return dataMap;
    }

    private Map<String, Integer> cursorMap;

    public void readAll(DataHandler handler) {
        if (handler == null) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "handler == null");
            }
            return;
        }
        handler.onStart();
        int oneNum = 0;//一次取到的数据条数
        int storageCount = ApmTask.sAllStorage.size();
        int succNum = 0;//本表中的上传数据总条数
        Map<String, List<IInfo>> dataMap = new HashMap<String, List<IInfo>>();
        cursorMap = new HashMap<String, Integer>();

        for (int i = 0; i < storageCount; i++) {
            IStorage storage = ApmTask.sAllStorage.get(i);
            succNum = 0;
            while (true) {
                List<IInfo> data = storage.getData(succNum, LIMIT);
                if (data == null || data.isEmpty()) {
                    if (Env.DEBUG) {
                        LogX.d(Env.TAG, SUB_TAG, "table " + storage.getName() + " is empty");
                    }
                    break;
                }
                dataMap.put(storage.getName(), data);
                oneNum = data.size();
                if (oneNum >= LIMIT) {//数据表中还有数据
                    boolean success = handler.onRead(dataMap);
                    if (!success) {//上传失败，直接结束
                        cursorMap.put(storage.getName(), succNum);
                        clearUploadData(handler);
                        return;
                    }
                    succNum += data.size();
                    dataMap.clear();
                } else {
                    succNum += data.size();
                    break;
                }
            }
            cursorMap.put(storage.getName(), succNum);
        }
        if (!dataMap.isEmpty()) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "onRead 1 hasCode = " + Integer.toHexString(dataMap.hashCode()));
            }
            handler.onRead(dataMap);
        }
        clearUploadData(handler);
    }

    private void clearUploadData(DataHandler handler) {
        for (IStorage storage : ApmTask.sAllStorage) {
            Integer num = cursorMap.get(storage.getName());
            if (num != null && num > 0) {
                boolean clearResult = storage.cleanByCount(num);
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "清理数据库clearResult:" + clearResult);
                }
            }
        }
        handler.onEnd();
    }

    public interface DataHandler {
        void onStart();

        boolean onRead(Map<String, List<IInfo>> data);

        void onEnd();
    }

    public static void deleteOld() {
        long delTime = getDelTime();
        if (delTime <= 0) {
            return;
        }
        LogX.o(TAG_O, SUB_TAG, "del.old ");
        for (IStorage storage : ApmTask.sAllStorage) {
            storage.deleteByTime(delTime);
        }
    }

    private static long getDelTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int exp = (int) ArgusApmConfigManager.getInstance().getArgusApmConfigData().cleanExp;
        calendar.add(Calendar.DATE, -exp);
        return calendar.getTime().getTime();
    }

    public boolean clean() {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "clean");
        }
        for (IStorage storage : ApmTask.sAllStorage) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "clean table name = " + storage.getName());
            }
            storage.clean();
        }
        return true;
    }
}