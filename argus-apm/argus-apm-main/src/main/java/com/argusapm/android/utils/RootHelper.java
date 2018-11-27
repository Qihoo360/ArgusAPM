package com.argusapm.android.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * @author ArgusAPM Team
 */
public class RootHelper {

    public static boolean isRooted() {
        return isCmdExist("su");
    }

    /**
     * 检查命令行 cmd 是否存在
     */
    private static boolean isCmdExist(String cmd) {
        String path = System.getenv("PATH");
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file;
        if (path.contains(":")) {
            String[] array = path.split(":");
            for (String str : array) {
                file = new File(str, cmd);
                if (file.exists()) {
                    return true;
                }
            }
            return false;
        } else {
            // 可能 PATH 中只有一个路径
            file = new File(path, cmd);
            boolean ret = file.exists();
            return ret;
        }
    }
}
