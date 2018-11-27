package com.argusapm.android.core.storage;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.core.BaseInfo;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.utils.LogX;

import java.util.List;

/**
 * @author ArgusAPM Team
 */
public abstract class TableStorage implements IStorage {
    public static final String SUB_TAG = "TableStorage";

    @Override
    public Object[] invoke(Object... args) {
        return new Object[0];
    }

    @Override
    public IInfo get(Integer key) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select * from ").append(getName()).append(" where ");
        stringBuffer.append(BaseInfo.KEY_ID_RECORD).append("=");
        stringBuffer.append(key);
        String sql = stringBuffer.toString();
        List<IInfo> infoList = readDb(sql);
        return (null == infoList || infoList.isEmpty()) ? null : infoList.get(0);
    }

    @Override
    public boolean save(IInfo value) {
        ContentValues values = value.toContentValues();
        if (!values.containsKey(BaseInfo.KEY_TIME_RECORD)) {
            values.put(BaseInfo.KEY_TIME_RECORD, System.currentTimeMillis());
        }
        try {
            return null != Manager.getInstance().getConfig().appContext.getContentResolver().insert(getTableUri(), values);
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "save ex : " + Log.getStackTraceString(e));
            }
        }
        return false;
    }

    @Override
    public boolean delete(Integer key) {
        try {
            return -1 != Manager.getInstance().getConfig().appContext.getContentResolver()
                    .delete(getTableUri(), BaseInfo.KEY_ID_RECORD, new String[]{String.valueOf(key)});
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "delete ex : " + Log.getStackTraceString(e));
            }
        }
        return false;
    }

    @Override
    public int deleteByTime(long time) {
        try {
            return Manager.getInstance().getConfig().appContext.getContentResolver()
                    .delete(getTableUri(), BaseInfo.KEY_TIME_RECORD + " < ?", new String[]{String.valueOf(time)});
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "deleteByTime ex : " + Log.getStackTraceString(e));
            }
        }
        return -2;
    }

    @Override
    public boolean update(Integer key, ContentValues cv) {
        try {
            return -1 != Manager.getInstance().getConfig().appContext.getContentResolver()
                    .update(getTableUri(), cv, BaseInfo.KEY_ID_RECORD + " = ?", new String[]{String.valueOf(key)});
        } catch (Exception e) {
            if (Env.DEBUG) {
                LogX.d(Env.TAG, "update ex : " + Log.getStackTraceString(e));
            }
        }
        return false;
    }

    @Override
    public List<IInfo> getAll() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select * from ").append(getName());
        String sql = stringBuffer.toString();
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "getData sql : " + sql);
        }
        return readDb(sql);
    }

    @Override
    public List<IInfo> getData(int index, int count) {
        // 读取flag为0的数据
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select * from ").append(getName()).append(" order by id asc");
//        stringBuffer.append(BaseInfo.KEY_FLAG).append("=0");
        stringBuffer.append(" limit ").append(count).append(" offset ").append(index);
        String sql = stringBuffer.toString();
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "getData sql : " + sql);
        }
        return readDb(sql);
    }

    @Override
    public boolean clean() {
        try {
            return Manager.getInstance().getConfig().appContext.getContentResolver().delete(getTableUri(), null, null) > 0;
        } catch (Exception e) {
            LogX.d(Env.TAG, "clean ex : " + Log.getStackTraceString(e));
        }
        return false;
    }

    /**
     * 清理数据库数据
     *
     * @param count 清理的条数
     * @return
     */
    @Override
    public boolean cleanByCount(int count) {
        try {
            return Manager.getInstance().getConfig().appContext.getContentResolver().delete(getTableUri(), "id in(select id from " + getName() + " order by id asc limit " + count + ")", null) > 0;
        } catch (Exception e) {
            LogX.d(Env.TAG, "cleanByCount ex : " + Log.getStackTraceString(e));
        }
        return false;
    }

    protected Uri getTableUri() {
        Context c = Manager.getContext();
        if (c == null) {
            return null;
        }
        return StorageUtils.getTableUri(c.getPackageName(), getName());
    }

    /**
     * 目前按照selection传入sql语句的方法来处理
     *
     * @param selection 待执行的sql语句
     * @return
     */
    public abstract List<IInfo> readDb(String selection);
}
