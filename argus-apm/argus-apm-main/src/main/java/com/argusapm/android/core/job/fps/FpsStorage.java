package com.argusapm.android.core.job.fps;

import android.database.Cursor;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.IInfo;
import com.argusapm.android.core.Manager;
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
public class FpsStorage extends TableStorage {
    private final String SUB_TAG = "FpsStorage";

    @Override
    public String getName() {
        return ApmTask.TASK_FPS;
    }

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
            int indexId = cursor.getColumnIndex(FpsInfo.KEY_ID_RECORD);
            int indexTimeRecord = cursor.getColumnIndex(FpsInfo.KEY_TIME_RECORD);
            int indexActivity = cursor.getColumnIndex(FpsInfo.KEY_ACTIVITY);
            int indexFps = cursor.getColumnIndex(FpsInfo.KEY_FPS);
            int indexParams = cursor.getColumnIndex(FpsInfo.KEY_PARAM);
            int indexProcessName = cursor.getColumnIndex(FpsInfo.KEY_PROCESS_NAME);
            do {
                FpsInfo fps = new FpsInfo(cursor.getInt(indexId));
                fps.setActivity(cursor.getString(indexActivity));
                fps.setFps(cursor.getInt(indexFps));
                fps.setParams(cursor.getString(indexParams));
                fps.setRecordTime(cursor.getLong(indexTimeRecord));
                fps.setProcessName(cursor.getString(indexProcessName));
                infoList.add(fps);
            } while (cursor.moveToNext());

        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, getName() + "; " + e.toString());
        } finally {
            IOStreamUtils.closeQuietly(cursor);
        }
        return infoList;
    }
}
