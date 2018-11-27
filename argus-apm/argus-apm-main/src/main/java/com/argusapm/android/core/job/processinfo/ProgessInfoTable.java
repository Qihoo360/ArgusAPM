package com.argusapm.android.core.job.processinfo;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * 进程信息数据表
 *
 * @author ArgusAPM Team
 */
public class ProgessInfoTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(
                DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", ProcessInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                ProcessInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                ProcessInfo.DBKey.PROCESS_NAME, DbHelper.DATA_TYPE_TEXT,
                ProcessInfo.DBKey.START_COUNT, DbHelper.DATA_TYPE_INTEGER,
                ProcessInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                ProcessInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                ProcessInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_PROCESS_INFO;
    }
}
