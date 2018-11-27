package com.argusapm.android.core.storage;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.utils.LogX;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;
import static com.argusapm.android.Env.TAG_O;

/**
 * 数据存储Provider
 *
 * @author ArgusAPM Team
 */
public class ApmProvider extends ContentProvider {
    private static final String SUB_TAG = "ApmProvider";

    private DbHelper mDbHelper; //数据库处理
    private final UriMatcher mTableMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SparseArray<ITable> mTableMap;

    private DbCache mDbCache;

    @Override
    public boolean onCreate() {
        LogX.o(TAG_O, SUB_TAG, "version " + Env.getVersionName());
        initTable();
        mDbHelper = new DbHelper(getContext(), Env.DB_IN_SDCARD);
        mDbHelper.setTableList(ApmTask.sTableList);
        mDbCache = new DbCache(mDbHelper);
        return true;
    }

    /**
     * 初始化数据库查询表
     */
    private void initTable() {
        mTableMap = new SparseArray<ITable>();
        int num = ApmTask.sTableNameList.length;
        for (int i = 0; i < num; i++) {
            mTableMap.append(i, ApmTask.sTableList[i]);
            mTableMatcher.addURI(StorageUtils.getAuthority(getContext().getPackageName()), ApmTask.sTableNameList[i], i);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ITable table = mTableMap.get(mTableMatcher.match(uri));
        if (null == table) return null;
        try {
            Cursor cursor = mDbHelper.getDatabase().rawQuery(selection, null);
            if (cursor != null) {
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            } else {
                if (Env.DEBUG) {
                    LogX.d(Env.TAG, SUB_TAG, "cursor == null");
                }
            }
            return cursor;
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "query ex : " + Log.getStackTraceString(e));
            }
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        ITable table = mTableMap.get(mTableMatcher.match(uri));
        if (null == values || null == table) return null;
        if (!DbSwitch.getSwitchState(table.getTableName())) {
            if (DEBUG) {
                LogX.d(TAG, "数据库禁止写入数据（" + table.getTableName() + "）");
            }
            return null;
        }
        boolean ret = mDbCache.saveDataToDB(new DbCache.InfoHolder(values, table.getTableName()));
        return ret ? uri : null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        ITable table = mTableMap.get(mTableMatcher.match(uri));
        if (null == table) return -1;
        int count = -1;
        try {
            count = mDbHelper.getDatabase().delete(table.getTableName(), selection, selectionArgs);
            if (DEBUG) {
                LogX.d(TAG, "数据库成功删除表（" + table.getTableName() + "）: " + count + "条数据");
            }
        } catch (Exception e) {
            if (DEBUG) {
                LogX.e(TAG, "数据库删除表（" + table.getTableName() + "）数据失败: " + e.toString());
            }
            return count;
        }
        notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        ITable table = mTableMap.get(mTableMatcher.match(uri));
        if (null == values || null == table) return 0;
        int count = 0;
        try {
            count = mDbHelper.getDatabase().update(table.getTableName(), values, selection, selectionArgs);
        } catch (Exception e) {
            if (DEBUG) {
                LogX.e(TAG, "数据库更新失败: " + e.toString());
            }
            return count;
        }
        notifyChange(uri, null);
        return count;
    }

    private void notifyChange(Uri uri, ContentObserver observer) {
        try {
            getContext().getContentResolver().notifyChange(uri, observer);
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "notifyChange ex : " + Log.getStackTraceString(e));
            }
        }
    }
}