package com.argusapm.android.core.storage;

import android.net.Uri;
import android.text.TextUtils;

import com.argusapm.android.core.StorageConfig;

/**
 * 数据存储相关-工具类
 *
 * @author ArgusAPM Team
 */
public class StorageUtils {
    /**
     * 获取各任务table的uri
     *
     * @param pkgName   包名
     * @param tableName table名称
     * @return uri
     */
    public static Uri getTableUri(String pkgName, String tableName) {
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(tableName)) {
            return null;
        }
        String uriStr = TextUtils.concat(
                StorageConfig.CONTENT_PATH_PREFIX,
                getAuthority(pkgName),
                "/",
                tableName).toString();
        return Uri.parse(uriStr);
    }

    /**
     * 获取AUTHORITY
     *
     * @param pkgName 包名
     * @return
     */
    public static String getAuthority(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return "";
        }
        return TextUtils.concat(pkgName, ".", StorageConfig.AUTHORITY_SUFFIX).toString();
    }
}
