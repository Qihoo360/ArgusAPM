package com.argusapm.android.core.job.fileinfo;

import android.content.ContentValues;

import com.argusapm.android.core.BaseInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件信息
 *
 * @author ArgusAPM Team
 */
public class FileInfo extends BaseInfo {
    private final String SUB_TAG = "FileInfo";
    public String mFileName;                            //文件名
    public String mFilePath;                              //文件路径
    public int mFileType;                                   //文件类型
    public long mLastModified = 0;                  //文件最后修改时间
    public long mFileSize = 0;                           //文件大小
    public int mWritable = 0;                            //文件可写状态
    public int mReadable = 0;                           //文件可读状态
    public int mExecutable = 0;                         //文件可执行状态
    public long mSubFIleNum = 0;                     //包含的子文件个数

    public static class DBKey {
        public static final String KEY_FILE_NAME = "fn";            //文件名
        public static final String KEY_FILE_PATH = "fp";             //文件路径
        public static final String KEY_FILE_TYPE = "ft";                //文件类型
        public static final String KEY_LAST_MODIFIED = "lm";   //最后修改时间
        public static final String KEY_FILE_SIZE = "fs";                //文件大小
        public static final String KEY_WRITABLE = "fw";             //可写状态
        public static final String KEY_READABLE = "fr";              //可读状态
        public static final String KEY_EXECUTABLE = "fe";          //可执行状态
        public static final String KEY_SUB_FILE_NUM = "sfn";    //子文件个数
    }

    public static class FileType {
        public static int FILE_TYPE_FILE = 0;              //文件夹
        public static int FILE_TYPE_DIR = 1;              //文件
        public static int FILE_TYPE_OTHER = 2;         //其它文件
    }


    public FileInfo() {
        this(-1);
    }

    public FileInfo(int id) {
        mId = id;
    }

    @Override
    public void parserJsonStr(String json) throws JSONException {
        parserJson(new JSONObject(json));
    }


    @Override
    public void parserJson(JSONObject json) throws JSONException {
        this.mFileName = json.getString(FileInfo.DBKey.KEY_FILE_NAME);
        this.mFilePath = json.getString(DBKey.KEY_FILE_PATH);
        this.mFileType = json.getInt(DBKey.KEY_FILE_TYPE);
        this.mLastModified = json.getLong(FileInfo.DBKey.KEY_LAST_MODIFIED);
        this.mFileSize = json.getLong(FileInfo.DBKey.KEY_FILE_SIZE);
        this.mWritable = json.getInt(FileInfo.DBKey.KEY_WRITABLE);
        this.mReadable = json.getInt(FileInfo.DBKey.KEY_READABLE);
        this.mExecutable = json.getInt(FileInfo.DBKey.KEY_EXECUTABLE);
        this.mSubFIleNum = json.getLong(FileInfo.DBKey.KEY_SUB_FILE_NUM);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        try {
            values.put(FileInfo.DBKey.KEY_FILE_NAME, mFileName);
            values.put(DBKey.KEY_FILE_PATH, mFilePath);
            values.put(DBKey.KEY_FILE_TYPE, mFileType);
            values.put(FileInfo.DBKey.KEY_LAST_MODIFIED, mLastModified);
            values.put(FileInfo.DBKey.KEY_FILE_SIZE, mFileSize);
            values.put(FileInfo.DBKey.KEY_WRITABLE, mWritable);
            values.put(FileInfo.DBKey.KEY_READABLE, mReadable);
            values.put(DBKey.KEY_EXECUTABLE, mExecutable);
            values.put(FileInfo.DBKey.KEY_SUB_FILE_NUM, mSubFIleNum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject ori = super.toJson().put(DBKey.KEY_FILE_NAME, mFileName)
                .put(DBKey.KEY_FILE_PATH, mFilePath)
                .put(DBKey.KEY_FILE_TYPE, mFileType)
                .put(DBKey.KEY_LAST_MODIFIED, mLastModified)
                .put(DBKey.KEY_FILE_SIZE, mFileSize)
                .put(DBKey.KEY_WRITABLE, mWritable)
                .put(DBKey.KEY_READABLE, mReadable)
                .put(DBKey.KEY_EXECUTABLE, mExecutable)
                .put(DBKey.KEY_SUB_FILE_NUM, mSubFIleNum);

        return ori;
    }

}
