package com.argusapm.android.core.job.func;

import android.database.Cursor;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.storage.TableStorage;
import com.argusapm.android.utils.IOStreamUtils;
import com.argusapm.android.utils.LogX;

import java.util.LinkedList;
import java.util.List;

import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class FuncStorage extends TableStorage {
    @Override
    public List<IInfo> readDb(String selection) {
        List<IInfo> infoList = new LinkedList<IInfo>();
        Cursor cursor = null;
        try {
            cursor = Manager.getInstance().getConfig().appContext.getContentResolver()
                    .query(getTableUri(), null, selection, null, null);
            if (null == cursor || !cursor.moveToNext()) {
                IOStreamUtils.closeQuietly(cursor);
                return infoList;
            }

            int indexId = cursor.getColumnIndex(FuncInfo.KEY_ID_RECORD);
            int indexTimeRecord = cursor.getColumnIndex(FuncInfo.KEY_TIME_RECORD);
            int indexType = cursor.getColumnIndex(FuncInfo.KEY_TYPE);
            int indexParams = cursor.getColumnIndex(FuncInfo.KEY_PARAM);
            do {
                FuncInfo info = new FuncInfo();
                info.setRecordTime(cursor.getLong(indexTimeRecord));
                info.setType(cursor.getInt(indexType));
                info.setParams(cursor.getString(indexParams));
                infoList.add(info);
            } while (cursor.moveToNext());

        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, getName() + "; " + e.toString());
        } finally {
            IOStreamUtils.closeQuietly(cursor);
        }
        return infoList;
    }

    @Override
    public String getName() {
        return ApmTask.TASK_FUNC;
    }
}
