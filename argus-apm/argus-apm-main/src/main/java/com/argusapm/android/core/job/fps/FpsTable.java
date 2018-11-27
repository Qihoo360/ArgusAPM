package com.argusapm.android.core.job.fps;

import android.text.TextUtils;

import com.android.internal.http.multipart.FilePartSource;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * @author ArgusAPM Team
 */
public class FpsTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(
                DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", FpsInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                FpsInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                FpsInfo.KEY_TYPE, DbHelper.DATA_TYPE_INTEGER,
                FpsInfo.KEY_ACTIVITY, DbHelper.DATA_TYPE_TEXT,
                FpsInfo.KEY_FPS, DbHelper.DATA_TYPE_INTEGER,
                FpsInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                FpsInfo.KEY_PROCESS_NAME, DbHelper.DATA_TYPE_TEXT,
                FpsInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                FpsInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_FPS;
    }

}