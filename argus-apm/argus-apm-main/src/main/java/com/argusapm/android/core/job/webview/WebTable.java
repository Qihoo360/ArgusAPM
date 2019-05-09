package com.argusapm.android.core.job.webview;

import android.text.TextUtils;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.storage.DbHelper;
import com.argusapm.android.core.storage.ITable;

/**
 * @author ArgusAPM Team
 */
public class WebTable implements ITable {
    @Override
    public String createSql() {
        return TextUtils.concat(
                DbHelper.CREATE_TABLE_PREFIX + getTableName(),
                "(", WebInfo.KEY_ID_RECORD, " INTEGER PRIMARY KEY AUTOINCREMENT,",
                WebInfo.KEY_TIME_RECORD, DbHelper.DATA_TYPE_INTEGER,
                WebInfo.DBKey.KEY_URL, DbHelper.DATA_TYPE_TEXT,
                WebInfo.DBKey.KEY_IS_WIFI, DbHelper.DATA_TYPE_INTEGER,
                WebInfo.DBKey.KEY_NAVIGATION_START, DbHelper.DATA_TYPE_INTEGER,
                WebInfo.DBKey.KEY_RESPONSE_START, DbHelper.DATA_TYPE_INTEGER,
                WebInfo.DBKey.KEY_PAGE_TIME, DbHelper.DATA_TYPE_INTEGER,
                WebInfo.KEY_PARAM, DbHelper.DATA_TYPE_TEXT,
                WebInfo.KEY_RESERVE_1, DbHelper.DATA_TYPE_TEXT,
                WebInfo.KEY_RESERVE_2, DbHelper.DATA_TYPE_TEXT_SUF
        ).toString();
    }

    @Override
    public String getTableName() {
        return ApmTask.TASK_WEBVIEW;
    }
}
