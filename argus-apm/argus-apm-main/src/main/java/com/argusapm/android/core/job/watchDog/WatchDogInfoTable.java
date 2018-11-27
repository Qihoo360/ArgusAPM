package com.argusapm.android.core.job.watchDog;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.job.block.BlockInfo;
import com.argusapm.android.core.job.processinfo.ProcessInfo;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * @author ArgusAPM Team
 */
public class WatchDogInfoTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(
                DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", ProcessInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                BlockInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                BlockInfo.DBKey.PROCESS_NAME, DbHelper.DATA_TYPE_TEXT,
                BlockInfo.DBKey.BLOCK_STACK, DbHelper.DATA_TYPE_TEXT,
                BlockInfo.DBKey.BLOCK_TIME, DbHelper.DATA_TYPE_INTEGER,
                BlockInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                BlockInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                BlockInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_WATCHDOG;
    }

}
