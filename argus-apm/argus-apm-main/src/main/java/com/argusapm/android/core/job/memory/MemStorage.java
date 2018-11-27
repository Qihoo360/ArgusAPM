package com.argusapm.android.core.job.memory;

import android.database.Cursor;
import android.net.Uri;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.job.fileinfo.FileInfo;
import com.argusapm.android.core.storage.TableStorage;
import com.argusapm.android.utils.IOStreamUtils;
import com.argusapm.android.utils.LogX;

import java.util.LinkedList;
import java.util.List;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class MemStorage extends TableStorage {
    private final String SUB_TAG = "MemStorage";

    @Override
    public String getName() {
        return ApmTask.TASK_MEM;
    }

    @Override
    public List<IInfo> readDb(String selection) {
        List<IInfo> memoryInfoList = new LinkedList<IInfo>();
        Cursor cursor = null;
        try {
            cursor = Manager.getInstance().getConfig().appContext.getContentResolver()
                    .query(getTableUri(), null, selection, null, null);
            if (null == cursor || !cursor.moveToFirst()) {
                IOStreamUtils.closeQuietly(cursor);
                return memoryInfoList;
            }
            int indexId = cursor.getColumnIndex(MemoryInfo.KEY_ID_RECORD);
            int indexTimeRecord = cursor.getColumnIndex(MemoryInfo.KEY_TIME_RECORD);
            int indexProcessName = cursor.getColumnIndex(MemoryInfo.KEY_PROCESS_NAME);
            int indexTotalPss = cursor.getColumnIndex(MemoryInfo.KEY_TOTAL_PSS);
            int indexDalvikPss = cursor.getColumnIndex(MemoryInfo.KEY_DALVIK_PSS);
            int indexNativePss = cursor.getColumnIndex(MemoryInfo.KEY_NATIVE_PSS);
            int indexOtherPss = cursor.getColumnIndex(MemoryInfo.KEY_OTHER_PSS);
            do {
                memoryInfoList.add(new MemoryInfo(
                        cursor.getInt(indexId),
                        cursor.getLong(indexTimeRecord),
                        cursor.getString(indexProcessName),
                        cursor.getInt(indexTotalPss),
                        cursor.getInt(indexDalvikPss),
                        cursor.getInt(indexNativePss),
                        cursor.getInt(indexOtherPss)
                ));
            } while (cursor.moveToNext());
        } catch (Exception e) {
            if (DEBUG) {
                LogX.e(TAG, SUB_TAG, getName() + "; " + e.toString());
            }
        } finally {
            IOStreamUtils.closeQuietly(cursor);
        }
        return memoryInfoList;
    }
}