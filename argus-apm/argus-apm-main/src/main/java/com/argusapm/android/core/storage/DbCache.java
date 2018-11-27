package com.argusapm.android.core.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.core.StorageConfig;
import com.argusapm.android.utils.LogX;

import java.util.ArrayList;
import java.util.List;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 数据库缓存
 * 防止频繁IO：1.导致数据无法写入；2.性能问题
 *
 * @author ArgusAPM Team
 */
public class DbCache {
    private static final String SUB_TAG = "DbCache";

    private static final int MSG_WHAT_PRE_WRITE_DB = 0;// 带数据
    private static final int MSG_WHAT_WRITE_DB = 1;
    private static final int MSG_WHAT_TIME_OUT = 2;

    private final List<InfoHolder> myDataList = new ArrayList<InfoHolder>();
    private long mLastTime = 0; //最后一次写数据库时间
    private DbHelper mDbHelper;

    private Handler mHandler;

    public DbCache(DbHelper db) {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "new DbCache");
        }
        mLastTime = System.currentTimeMillis();
        newThread();
        mDbHelper = db;
    }

    public boolean saveDataToDB(InfoHolder data) {
        if (data == null) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "saveDataToDB To tmp list data == null");
            }
            return false;
        }
        Message msg = mHandler.obtainMessage(MSG_WHAT_PRE_WRITE_DB);
        msg.what = MSG_WHAT_PRE_WRITE_DB;
        msg.obj = data;
        mHandler.sendMessage(msg);
        return true;
    }

    private void newThread() {
        HandlerThread handlerThread = new HandlerThread("dbCache");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_WHAT_PRE_WRITE_DB:
                        InfoHolder holder = null;
                        try {
                            holder = (InfoHolder) msg.obj;
                        } catch (ClassCastException ex) {
                            LogX.d(Env.TAG, SUB_TAG, "class cast exception : " + ex.getMessage());
                        }
                        if (holder != null) {
                            handleData(holder);
                        } else {
                            if (Env.DEBUG) {
                                LogX.d(Env.TAG, SUB_TAG, "holder == null");
                            }
                        }
                        break;
                    case MSG_WHAT_WRITE_DB:
                        if (Env.DEBUG) {
                            LogX.d(Env.TAG, SUB_TAG, "MSG_WHAT_WRITE_DB");
                        }
                        updateTime(System.currentTimeMillis());
                        readFromListAndWriteToDB();
                        break;
                    case MSG_WHAT_TIME_OUT:
                        if (Env.DEBUG) {
                            LogX.d(Env.TAG, SUB_TAG, "MSG_WHAT_TIME_OUT -> MSG_WHAT_WRITE_DB");
                        }
                        if (mHandler.hasMessages(MSG_WHAT_WRITE_DB)) {
                            mHandler.removeMessages(MSG_WHAT_WRITE_DB);
                        }
                        mHandler.sendEmptyMessage(MSG_WHAT_WRITE_DB);
                        break;
                }
            }
        };
    }

    private void handleData(InfoHolder data) {
        int size = 0;
        synchronized (myDataList) {
            if (!myDataList.contains(data)) {
                myDataList.add(data);
            }
            size = myDataList.size();
        }

        long cur = System.currentTimeMillis();
        long interval = cur - mLastTime;
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "saveDataToDB size = " + size + " interval = " + interval + " : name " + data.tableName + " | info : " + data.info.toString());
        }

        // 每收到一条数据，就移除上一条数据的延时
        if (mHandler.hasMessages(MSG_WHAT_TIME_OUT)) {
            mHandler.removeMessages(MSG_WHAT_TIME_OUT);
        }

        // 双重保护。
        // 1. 防止频繁发生数据库IO操作，设置最小时间间隔为INTERVAL；
        // 2. 防止数据写入太快，队列溢出，当队列达到100的时候，即批量写入数据库
        if (interval >= StorageConfig.SAVE_DB_INTERVAL || size >= StorageConfig.SAVE_DB_MAX_COUNT) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "saveDataToDB To db size = " + size);
            }

            if (mHandler.hasMessages(MSG_WHAT_WRITE_DB)) {
                mHandler.removeMessages(MSG_WHAT_WRITE_DB);
            }

            mHandler.sendEmptyMessage(MSG_WHAT_WRITE_DB);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_TIME_OUT, StorageConfig.SAVE_DB_INTERVAL);
        }
    }

    private void updateTime(long cur) {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "updateTime = " + cur);
        }
        mLastTime = cur;
    }


    public static class InfoHolder {
        public ContentValues info;
        public String tableName;

        public InfoHolder(ContentValues i, String name) {
            info = i;
            tableName = name;
        }
    }

    private void readFromListAndWriteToDB() {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "readFromListAndWriteToDB: tid = " + Thread.currentThread().getId() + " start");
        }

        if (myDataList == null || myDataList.isEmpty()) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "readFromListAndWriteToDB: myDataList empty");
            }
            return;
        }

        SQLiteDatabase db = mDbHelper.getDatabase();
        if (db == null) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "readFromListAndWriteToDB: db == null");
            }
            return;
        }
        int count = 0;
        long start = System.currentTimeMillis();

        // 该行用于处理数据库被恶意删除，导致数据库读写失败而引起的崩溃问题
        boolean isBeginTransaction = false;
        try {
            db.beginTransaction();
            isBeginTransaction = true;
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "readFromListAndWriteToDB beginTransaction");
            }

            while (myDataList.size() > 0) {
//                if (Env.DEBUG) {
//                    LogX.d(Env.TAG, SUB_TAG, "readFromListAndWriteToDB ...runing thread id: " + Thread.currentThread().getId() + " size = " + myDataList.size());
//                }
                InfoHolder data = myDataList.get(0);
                count++;
                if (data != null) {
//                    if (Env.DEBUG) {
//                        LogX.d(Env.TAG, SUB_TAG, "readFromListAndWriteToDB saveData To DB tableName : " + data.tableName + " | " + data.info.toString());
//                    }

                    long rowId = db.insert(data.tableName, null, data.info);
                    if (rowId < 0) {
                        break;
                    }
//                    if (DEBUG) {
//                        LogX.d(TAG, "readFromListAndWriteToDB DB insert ??? " + (rowId > 0 ? "success" : "failed") + " info : " + data.info.toString());
//                    }
                    synchronized (myDataList) {
                        if (myDataList.size() > 0) {
                            myDataList.remove(0);
                        }
                    }
                }

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "readFromListAndWriteToDB error\r\n" + Log.getStackTraceString(e));
            }
        } finally {
            if (isBeginTransaction) {
                try {
                    db.endTransaction();
                } catch (Exception e) {

                    LogX.e(TAG, SUB_TAG, "readFromListAndWriteToDB finally error\r\n" + Log.getStackTraceString(e));
                }

            }
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "readFromListAndWriteToDB endTransaction count = " + count + " | time = " + (System.currentTimeMillis() - start));
            }
        }
        //db.close();// ???
    }
}
