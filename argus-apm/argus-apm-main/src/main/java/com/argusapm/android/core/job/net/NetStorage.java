package com.argusapm.android.core.job.net;

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
public class NetStorage extends TableStorage {
    private final String SUB_TAG = "NetStorage";

    @Override
    public String getName() {
        return ApmTask.TASK_NET;
    }

    @Override
    public List<IInfo> readDb(String selection) {
        List<IInfo> netInfoList = new LinkedList<IInfo>();
        Cursor cursor = null;
        try {
            cursor = Manager.getInstance().getConfig().appContext.getContentResolver()
                    .query(getTableUri(), null, selection, null, null);
            if (null == cursor || !cursor.moveToFirst()) {
                IOStreamUtils.closeQuietly(cursor);
                return netInfoList;
            }
            int indexId = cursor.getColumnIndex(NetInfo.KEY_ID_RECORD);
            int indexTimeRecord = cursor.getColumnIndex(NetInfo.KEY_TIME_RECORD);
            int indexUrl = cursor.getColumnIndex(NetInfo.KEY_URL);
            int indexStatusCode = cursor.getColumnIndex(NetInfo.KEY_STATUS_CODE);
            int indexErrorCode = cursor.getColumnIndex(NetInfo.KEY_ERROR_CODE);
            int indexSendBytes = cursor.getColumnIndex(NetInfo.KEY_SEND_BYTES);
            int indexReceiveBytes = cursor.getColumnIndex(NetInfo.KEY_RECEIVE_BYTES);
            int indexIsWifi = cursor.getColumnIndex(NetInfo.KEY_IS_WIFI);
            int indexStartTime = cursor.getColumnIndex(NetInfo.KEY_TIME_START);
            int indexCostTime = cursor.getColumnIndex(NetInfo.KEY_TIME_COST);
            do {
                NetInfo info = new NetInfo(cursor.getInt(indexId));
                info.setRecordTime(cursor.getLong(indexTimeRecord));
                info.url = cursor.getString(indexUrl);
                info.statusCode = cursor.getInt(indexStatusCode);
                info.errorCode = cursor.getInt(indexErrorCode);
                info.sentBytes = cursor.getLong(indexSendBytes);
                info.receivedBytes = cursor.getLong(indexReceiveBytes);
                info.isWifi = cursor.getInt(indexIsWifi) == 1;
                info.startTime = cursor.getLong(indexStartTime);
                info.costTime = cursor.getLong(indexCostTime);
                netInfoList.add(info);
            } while (cursor.moveToNext());
        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, getName() + "; " + e.toString());
        } finally {
            IOStreamUtils.closeQuietly(cursor);
        }
        return netInfoList;
    }
}