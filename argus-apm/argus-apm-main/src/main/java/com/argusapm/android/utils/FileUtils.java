package com.argusapm.android.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.argusapm.android.Env;
import com.argusapm.android.api.ApmTask;
import com.argusapm.android.debug.storage.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * 文件、sd卡相关工具类
 *
 * @author ArgusAPM Team
 */
public class FileUtils {
    public static final String SUB_TAG = "FileUtils";

    /**
     * 获取sd卡根目录
     *
     * @return
     */
    public static String getSDPath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (!sdCardExist) {
            return "";
        }
        File sdDir = Environment.getExternalStorageDirectory();//获取sd卡根目录
        return sdDir.toString();
    }


    public static boolean writeFile(String filePath, String str) {
        boolean state = false;
        PrintStream ps = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();


            ps = new PrintStream(new FileOutputStream(file));
            ps.println(str);// 往文件里写入字符串
            state = true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.safeClose(ps);
        }

        return state;
    }

    public static String readFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }

        File f = new File(path);

        if (!f.exists() || !f.isFile()) {
            return "";
        }

        BufferedReader br = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.safeClose(br);
        }

        return stringBuilder.toString();
    }

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


}
