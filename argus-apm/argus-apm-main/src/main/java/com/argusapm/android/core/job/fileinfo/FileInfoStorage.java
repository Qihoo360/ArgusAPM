package com.argusapm.android.core.job.fileinfo;

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
 * 文件信息存储
 *
 * @author ArgusAPM Team
 */
public class FileInfoStorage extends TableStorage {
    @Override
    public String getName() {
        return ApmTask.TASK_FILE_INFO;
    }

    @Override
    public List<IInfo> readDb(String selection) {
        List<IInfo> infoList = new LinkedList<IInfo>();
        Cursor cursor = null;
        try {
            cursor = Manager.getInstance().getConfig().appContext.getContentResolver().query(getTableUri(), null, selection, null, null);
            if (null == cursor || !cursor.moveToFirst()) {
                IOStreamUtils.closeQuietly(cursor);
                return infoList;
            }
            int indexId = cursor.getColumnIndex(FileInfo.KEY_ID_RECORD);
            int indexTimeRecord = cursor.getColumnIndex(FileInfo.KEY_TIME_RECORD);
            int nFileNameIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_FILE_NAME);
            int nFilePathIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_FILE_PATH);
            int nFileTypeIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_FILE_TYPE);
            int nCreateTimeIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_LAST_MODIFIED);
            int nFileSizeIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_FILE_SIZE);
            int nWritableIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_WRITABLE);
            int nReadableIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_READABLE);
            int nExecutableIndex = cursor.getColumnIndex(FileInfo.DBKey.KEY_EXECUTABLE);
            int nSubFIleNum = cursor.getColumnIndex(FileInfo.DBKey.KEY_SUB_FILE_NUM);
            do {
                FileInfo fileInfo = new FileInfo(cursor.getInt(indexId));
                fileInfo.setRecordTime(cursor.getLong(indexTimeRecord));
                fileInfo.mFileName = cursor.getString(nFileNameIndex);
                fileInfo.mFilePath = cursor.getString(nFilePathIndex);
                fileInfo.mFileType = cursor.getInt(nFileTypeIndex);
                fileInfo.mLastModified = cursor.getLong(nCreateTimeIndex);
                fileInfo.mFileSize = cursor.getLong(nFileSizeIndex);
                fileInfo.mWritable = cursor.getInt(nWritableIndex);
                fileInfo.mReadable = cursor.getInt(nReadableIndex);
                fileInfo.mExecutable = cursor.getInt(nExecutableIndex);
                fileInfo.mSubFIleNum = cursor.getInt(nSubFIleNum);
                infoList.add(fileInfo);
            } while (cursor.moveToNext());
        } catch (Exception e) {
            LogX.e(TAG, SUB_TAG, e.toString());
        } finally {
            IOStreamUtils.closeQuietly(cursor);
        }
        return infoList;
    }
}
