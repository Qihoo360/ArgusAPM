package com.argusapm.android.core.job.activity;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * Activity表
 *
 * @author ArgusAPM Team
 */
public class ActivityTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", ActivityInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                ActivityInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                ActivityInfo.KEY_NAME, DbHelper.DATA_TYPE_TEXT,
                ActivityInfo.KEY_START_TYPE, DbHelper.DATA_TYPE_INTEGER,
                ActivityInfo.KEY_TIME, DbHelper.DATA_TYPE_INTEGER,
                ActivityInfo.KEY_LIFE_CYCLE, DbHelper.DATA_TYPE_INTEGER,
                ActivityInfo.KEY_APP_NAME, DbHelper.DATA_TYPE_TEXT,
                ActivityInfo.KEY_APP_VER, DbHelper.DATA_TYPE_TEXT,
                ActivityInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                ActivityInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                ActivityInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_ACTIVITY;
    }
}