package com.argusapm.android.utils;

import android.database.Cursor;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class IOStreamUtils {
    private static final String SUB_TAG = "IOStreamUtils";

    public static void closeSilently(Closeable c) {
        if (null == c) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            if (DEBUG) {
                LogX.e(TAG, SUB_TAG, e.toString());
            }
        }
    }

    public static void closeQuietly(Cursor c) {
        if (null == c) {
            return;
        }
        c.close();
    }

    /**
     * 读取少量数据(小于4k)
     */
    public static String readStreamToStr(InputStream in) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (Throwable e) {
            if (DEBUG) {
                LogX.e(TAG, SUB_TAG, e.toString());
            }
        } finally {
            closeSilently(br);
        }
        return sb.toString();
    }

    public static String readTinyFile(String filePath) {
        return readFilePart(0, -1, filePath);
    }

    /**
     * 读取字符文件一部分数据[start,end)
     *
     * @param start    起始行 必须>=0
     * @param end      结束行 小于0时,表示不限制
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFilePart(int start, int end, String filePath) {
        if (start < 0) {
            throw new RuntimeException("start must >=0");
        }
        if (end > 0 && start > end) {
            throw new RuntimeException("check index,start>=0,if end >0,start >0");
        }
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int index = -1;
            while ((line = br.readLine()) != null) {
                index++;
                if (index < start) {
                    continue;
                }
                if (end >= 0 && index >= end) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
        } catch (Throwable e) {
            if (DEBUG) {
                LogX.e(TAG, SUB_TAG, e.toString());
            }
        } finally {
            closeSilently(br);
        }
        return sb.toString();
    }
}