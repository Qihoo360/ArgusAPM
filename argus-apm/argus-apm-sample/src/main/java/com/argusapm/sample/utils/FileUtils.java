package com.argusapm.sample.utils;

import android.content.Context;
import android.os.Environment;

import com.argusapm.android.api.ApmTask;

import java.io.File;

/**
 * 文件工具类
 *
 * @author ArgusAPM Team
 */
public class FileUtils {
    //因为在debug模式下，手动导入argus_apm_sdk_config.json至SD卡下的/360/Apm目录下
    public static String getSDArgusAPMConfigFilePath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (!sdCardExist) {
            return "";
        }
        File sdDir = Environment.getExternalStorageDirectory();//获取sd卡根目录
        return sdDir.toString() + "/360/Apm/argus_apm_sdk_config.json";
    }

    public static boolean isContainApmConfigFileOfSdcard() {
        File file = new File(getSDArgusAPMConfigFilePath());
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    //该路径下的文件为网络上下载的云规则文件地址
    public static String getApmConfigFilePath(Context c) {
        String result = "";
        if (c == null) {
            return result;
        }
        try {
            result = c.getFilesDir() + File.separator + ApmTask.APM_CONFIG_FILE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isDownloadApmConfigFileFromServer(Context context) {
        File file = new File(getApmConfigFilePath(context));
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
