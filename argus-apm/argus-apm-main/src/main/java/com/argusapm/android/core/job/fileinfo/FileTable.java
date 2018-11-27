package com.argusapm.android.core.job.fileinfo;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * 文件表
 *
 * @author ArgusAPM Team
 */
public class FileTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", FileInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                FileInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_FILE_NAME, DbHelper.DATA_TYPE_TEXT,
                FileInfo.DBKey.KEY_FILE_PATH, DbHelper.DATA_TYPE_TEXT,
                FileInfo.DBKey.KEY_FILE_TYPE, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_LAST_MODIFIED, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_FILE_SIZE, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_READABLE, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_WRITABLE, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_EXECUTABLE, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.DBKey.KEY_SUB_FILE_NUM, DbHelper.DATA_TYPE_INTEGER,
                FileInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                FileInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                FileInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_FILE_INFO;
    }
}
