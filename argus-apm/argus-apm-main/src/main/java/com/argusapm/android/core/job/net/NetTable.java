package com.argusapm.android.core.job.net;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * @author ArgusAPM Team
 */
public class NetTable implements ITable {

    @Override
    public String createSql() {
        return TextUtils.concat(
                DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", NetInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                NetInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_URL, DbHelper.DATA_TYPE_TEXT,
                NetInfo.KEY_STATUS_CODE, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_ERROR_CODE, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_RECEIVE_BYTES, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_SEND_BYTES, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_IS_WIFI, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_TIME_START, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_TIME_COST, DbHelper.DATA_TYPE_INTEGER,
                NetInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                NetInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                NetInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_NET;
    }
}