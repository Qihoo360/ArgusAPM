package com.argusapm.android.debug.storage;

import com.argusapm.android.Env;
import com.argusapm.android.utils.LogX;

/**
 * 日志数据存储管理类
 *
 * @author ArgusAPM Team
 */
public class StorageManager {
    /**
     * 按行保存到文本文件
     *
     * @param line
     */
    public static void saveToFile(String line) {
        TraceWriter.log(Env.TAG, line);
    }

}
