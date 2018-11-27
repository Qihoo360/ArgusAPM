package com.argusapm.android.core.job.memory;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * @author ArgusAPM Team
 */
public class MemoryTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(
                DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", MemoryInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                MemoryInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                MemoryInfo.KEY_PROCESS_NAME, DbHelper.DATA_TYPE_TEXT,
                MemoryInfo.KEY_TOTAL_PSS, DbHelper.DATA_TYPE_INTEGER,
                MemoryInfo.KEY_DALVIK_PSS, DbHelper.DATA_TYPE_INTEGER,
                MemoryInfo.KEY_NATIVE_PSS, DbHelper.DATA_TYPE_INTEGER,
                MemoryInfo.KEY_OTHER_PSS, DbHelper.DATA_TYPE_INTEGER,
                MemoryInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                MemoryInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                MemoryInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_MEM;
    }

}