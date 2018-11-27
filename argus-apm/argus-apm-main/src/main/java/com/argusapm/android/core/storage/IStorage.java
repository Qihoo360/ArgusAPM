package com.argusapm.android.core.storage;

import android.content.ContentValues;

import com.argusapm.android.core.IInfo;

import java.util.List;

/**
 * 数据增删改查中间操作
 *
 * @author ArgusAPM Team
 */
public interface IStorage {
    String getName();

    IInfo get(Integer id);

    boolean save(IInfo data);

    int deleteByTime(long time);

    boolean delete(Integer id);

    // 按照一条id更新数据
    boolean update(Integer id, ContentValues cv);

    List<IInfo> getAll();

    List<IInfo> getData(int index, int count);

    boolean clean();

    boolean cleanByCount(int count);

    Object[] invoke(Object... args);
}