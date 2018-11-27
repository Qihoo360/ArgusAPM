package com.argusapm.android.core.storage;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.StorageConfig;
import com.argusapm.android.utils.LogX;

import java.io.File;
import java.io.IOException;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * 数据库处理类
 *
 * @author ArgusAPM Team
 */
public class DbHelper extends SQLiteOpenHelper {
    private final String SUB_TAG = "DbHelper";
    public static final String DATA_TYPE_INTEGER = " INTEGER,";
    public static final String DATA_TYPE_REAL = " REAL,";
    public static final String DATA_TYPE_INTEGER_SUF = " INTEGER);";
    public static final String DATA_TYPE_TEXT = " TEXT,";
    public static final String DATA_TYPE_TEXT_SUF = " TEXT);";
    public static final String CREATE_TABLE_PREFIX = "CREATE TABLE IF NOT EXISTS ";

    private ITable[] mTableList;
    private Context appContext;

    private boolean isInSdcard = false;

    private SQLiteDatabase mDb;


    private String mDbPath;

    public DbHelper(Context context, boolean isInSdcard) {
        super(context, StorageConfig.DB_NAME, null, StorageConfig.DB_VERSION);
        this.isInSdcard = isInSdcard;
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "db isInSdcard = " + isInSdcard);
        }

        init(context);

        mDbPath = getDbPath();

    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, StorageConfig.DB_NAME, factory, StorageConfig.DB_VERSION);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, StorageConfig.DB_NAME, factory, StorageConfig.DB_VERSION, errorHandler);
        init(context);
    }

    private void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private String getDbPath() {
        String path = "";
        if (!isInSdcard) {
            String filesDir = appContext.getDatabasePath(StorageConfig.DB_NAME).getAbsolutePath();
            path = filesDir;
        } else {
            String pkgName = appContext.getPackageName();
            String sdDir = Manager.getInstance().getBasePath() + pkgName + File.separator;
            path = sdDir + StorageConfig.DB_NAME;
        }
        return path;
    }

    public SQLiteDatabase getDatabase() {
        if (mDb == null) {
            try {
                if (isInSdcard) {
                    newDbFile(mDbPath);
                    mDb = SQLiteDatabase.openOrCreateDatabase(mDbPath, null);
                    onCreate(mDb);
                } else {
                    mDb = getWritableDatabase();
                }
            } catch (Exception e) {
                if (Env.DEBUG) {
                    LogX.e(Env.TAG, SUB_TAG, "getDatabase ex : " + Log.getStackTraceString(e));
                }
            }
        }
        return mDb;
    }

    private void newDbFile(String path) {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "db path = " + path);
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }

        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "newDbFile ioException : " + Log.getStackTraceString(e));
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "创建数据库 onCreate: " + (mTableList == null ? null : mTableList.length));
        }
        if (null == mTableList) return;

        for (ITable table : mTableList) {
            db.execSQL(table.createSql());
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, table.getTableName() + " :" + table.createSql());
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "升级数据库:" + newVersion);
        }
        deleteDBByName(StorageConfig.DB_NAME);
//        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "数据库降级:" + newVersion);
        }
        deleteDBByName(StorageConfig.DB_NAME);
//        onCreate(db);
    }


    public boolean deleteDBByName(String DBName) {
        try {
            if (isInSdcard) {
                File f = new File(mDbPath);
                if (f.exists() && f.isFile()) {
                    f.delete();
                }
            } else {
                appContext.deleteDatabase(DBName);
            }
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "删除数据库:" + DBName);
            }
        } catch (Exception e) {
            if (DEBUG) {
                LogX.d(TAG, "清理数据库失败: " + e.toString());
            }
        }
        return true;
    }

    public void setTableList(ITable[] mTableList) {
        this.mTableList = mTableList;
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "setTableList: " + (mTableList == null ? null : mTableList.length));
        }
    }
}