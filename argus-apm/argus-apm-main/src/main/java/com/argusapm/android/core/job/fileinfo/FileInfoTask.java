package com.argusapm.android.core.job.fileinfo;

import android.text.TextUtils;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.cloudconfig.ArgusApmConfigManager;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.core.storage.IStorage;
import com.argusapm.android.core.tasks.BaseTask;
import com.argusapm.android.utils.AsyncThreadTask;
import com.argusapm.android.utils.PreferenceUtils;
import com.argusapm.android.utils.SystemUtils;

import java.io.File;
import java.util.LinkedList;

/**
 * 文件信息Task
 *
 * @author ArgusAPM Team
 */
public class FileInfoTask extends BaseTask {
    private long mMinFileSize = 0;

    /*
     * @param paths 需要遍历的文件路径
     *  @depth 遍历的深度
     */
    public FileInfoTask() {

    }

    private void setMinFileSize() {
        mMinFileSize = ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.minFileSize;
    }

    private class LiteFileInfo {
        public long mFileSize = 0;
        public long mSubFileNum = 0;

        public LiteFileInfo(long fileSize, long subFileNum) {
            mFileSize = fileSize;
            mSubFileNum = subFileNum;
        }

        public LiteFileInfo() {
            this(0, 0);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if ((!isCanWork()) || (!checkTime())) {
                return;
            }
            updateLastTime();
            for (String dir : ArgusApmConfigManager.getInstance().getArgusApmConfigData().fileSdDirs) { //sd卡
                if (TextUtils.isEmpty(dir)) { //避免遍历整个sd卡
                    continue;
                }
                File f = new File(SystemUtils.sdcardPath() + File.separator + dir);
                if (!f.exists()) {
                    continue;
                }
                saveFileInfo(f, ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.getFileDepth());
            }
            //data目录
            if (ArgusApmConfigManager.getInstance().getArgusApmConfigData().fileDataDirs.size() > 0) {//有参数
                for (String dir : ArgusApmConfigManager.getInstance().getArgusApmConfigData().fileDataDirs) {
                    if (TextUtils.isEmpty(dir)) { //避免遍历整个data目录
                        continue;
                    }
                    File f = new File(Manager.getInstance().getContext().getFilesDir().getParent() + File.separator + dir);
                    if (!f.exists()) {
                        continue;
                    }
                    saveFileInfo(f, ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.getFileDepth());
                }
            } else {//默认取数据库信息
                File f = new File(Manager.getInstance().getContext().getFilesDir().getParent() + File.separator + TaskConfig.DATABASES);
                if (f.exists()) {
                    saveFileInfo(f, ArgusApmConfigManager.getInstance().getArgusApmConfigData().funcControl.getFileDepth());
                }
            }
            if (Env.DEBUG) {
                AsyncThreadTask.executeDelayed(runnable, TaskConfig.TEST_INTERVAL);
            } else {
                AsyncThreadTask.executeDelayed(runnable, TaskConfig.FILE_INFO_INTERVAL);
            }
        }
    };

    private boolean checkTime() {
        long diff = System.currentTimeMillis() - PreferenceUtils.getLong(Manager.getContext(), PreferenceUtils.SP_KEY_LAST_FILE_INFO_TIME, 0);
        boolean res = true;
        if (Env.DEBUG) {
            res = diff > TaskConfig.TEST_INTERVAL;
        } else {
            res = diff > TaskConfig.FILE_INFO_INTERVAL;
        }
        return res;
    }

    private void updateLastTime() {
        PreferenceUtils.setLong(Manager.getContext(), PreferenceUtils.SP_KEY_LAST_FILE_INFO_TIME, System.currentTimeMillis());
    }

    private LiteFileInfo saveFileInfo(File f, int depath) {
        LiteFileInfo liteFileInfo = new LiteFileInfo();
        if (null == f) {
            return liteFileInfo;
        }

        if (f.isFile()) {
            liteFileInfo = saveFileInfoImp(f);
        } else if (f.isDirectory()) {
            if (depath >= 0) {
                liteFileInfo = saveDirInfo(f, depath - 1);

                saveFileInfoImp(f, liteFileInfo);
            } else {
                liteFileInfo = saveFileInfoUnRecrusive(f);
            }
        }
        return liteFileInfo;
    }


    /*
     *非递归遍历目录，主要是获取文件夹大小和子文件个数
     */
    private LiteFileInfo saveFileInfoUnRecrusive(File f) {
        LiteFileInfo liteFileInfo = new LiteFileInfo();
        if (null == f) {
            return liteFileInfo;
        }

        LinkedList<File> dirs = new LinkedList<File>();
        if (f.isFile()) {
            saveFileInfoImp(f);
        } else if (f.isDirectory()) {
            File[] files = f.listFiles();

            if (null != files && 0 != files.length) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        dirs.add(file);
                    } else {
                        liteFileInfo.mFileSize = liteFileInfo.mFileSize + getFileSize(file);
                        liteFileInfo.mSubFileNum++;
                    }
                }
            }

            File tmpFile = null;
            while (!dirs.isEmpty()) {
                tmpFile = dirs.removeFirst();

                if (tmpFile.isDirectory()) {
                    files = tmpFile.listFiles();
                    if (null == files || 0 == files.length) {
                        continue;
                    }

                    for (File file : files) {
                        if (file.isDirectory()) {
                            dirs.add(file);
                        } else {
                            liteFileInfo.mFileSize = liteFileInfo.mFileSize + getFileSize(file);
                            liteFileInfo.mSubFileNum++;
                        }

                    }
                } else {
                    liteFileInfo.mFileSize = liteFileInfo.mFileSize + getFileSize(tmpFile);
                    liteFileInfo.mSubFileNum++;
                }
            }
        }

        return liteFileInfo;
    }

    private LiteFileInfo saveFileInfoImp(File fIle) {
        LiteFileInfo liteFileInfo = new LiteFileInfo();
        if (null == fIle) {
            return liteFileInfo;
        }

        liteFileInfo.mFileSize = getFileSize(fIle);

        FileInfo fileInfo = new FileInfo();
        fileInfo.mFileName = fIle.getName();
        fileInfo.mFilePath = fIle.getAbsolutePath();
        fileInfo.mFileType = getFileType(fIle);
        fileInfo.mLastModified = fIle.lastModified();
        fileInfo.mFileSize = getFileSize(fIle);
        fileInfo.mReadable = true == fIle.canRead() ? 1 : 0;
        fileInfo.mWritable = true == fIle.canWrite() ? 1 : 0;
        fileInfo.mExecutable = true == fIle.canExecute() ? 1 : 0;

        if (fIle.isFile()) {
            liteFileInfo.mFileSize = fileInfo.mFileSize = getFileSize(fIle);
            liteFileInfo.mSubFileNum = fileInfo.mSubFIleNum = 1;
        }

        if (liteFileInfo.mFileSize >= mMinFileSize) {
            save(fileInfo);
        }


        return liteFileInfo;
    }

    private void saveFileInfoImp(File fIle, LiteFileInfo fileSizeSubFile) {
        if (null == fIle) {
            return;
        }

        //获取v5云控的最小文件大小，如果当前的文件大小小于这一值，则不采集
        if (fileSizeSubFile.mFileSize < mMinFileSize) {
            return;
        }

        FileInfo fileInfo = new FileInfo();
        fileInfo.mFileName = fIle.getName();
        fileInfo.mFilePath = fIle.getAbsolutePath();
        fileInfo.mFileType = getFileType(fIle);
        fileInfo.mLastModified = fIle.lastModified();
        fileInfo.mFileSize = fileSizeSubFile.mFileSize;
        fileInfo.mReadable = true == fIle.canRead() ? 1 : 0;
        fileInfo.mWritable = true == fIle.canWrite() ? 1 : 0;
        fileInfo.mExecutable = true == fIle.canExecute() ? 1 : 0;
        fileInfo.mSubFIleNum = fileSizeSubFile.mSubFileNum;

        save(fileInfo);
    }

    private LiteFileInfo saveDirInfo(File file, int depath) {
        LiteFileInfo liteFileInfo = new LiteFileInfo();

        if (null == file || !file.isDirectory()) {
            return liteFileInfo;
        }

        if (depath >= 0) {
            File[] files = file.listFiles();

            if (null != files && 0 != files.length) {
                int tmpDepath = depath - 1;
                for (File f : files) {
                    LiteFileInfo tmpFileSizeSubFileNum = saveFileInfo(f, tmpDepath);
                    liteFileInfo.mFileSize = liteFileInfo.mFileSize + tmpFileSizeSubFileNum.mFileSize;
                    liteFileInfo.mSubFileNum = liteFileInfo.mSubFileNum + tmpFileSizeSubFileNum.mSubFileNum;
                }
            }
        } else {
            liteFileInfo = saveFileInfoUnRecrusive(file);
        }

        return liteFileInfo;
    }

    private long getFileSize(File f) {
        return f.length();
    }

    private int getFileType(File f) {
        int fileType;

        if (f.isDirectory()) {
            fileType = FileInfo.FileType.FILE_TYPE_DIR;
        } else if (f.isFile()) {
            fileType = FileInfo.FileType.FILE_TYPE_FILE;
        } else {
            fileType = FileInfo.FileType.FILE_TYPE_OTHER;
        }

        return fileType;
    }

    @Override
    protected IStorage getStorage() {
        return new FileInfoStorage();
    }

    @Override
    public void start() {
        super.start();
        setMinFileSize();
        AsyncThreadTask.executeDelayed(runnable, (int) (Math.round(Math.random() * 5000)));
    }

    @Override
    public String getTaskName() {
        return ApmTask.TASK_FILE_INFO;
    }
}
