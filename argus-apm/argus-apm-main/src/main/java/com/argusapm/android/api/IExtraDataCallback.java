package com.argusapm.android.api;

/**
 * 该接口用于sdk读取外部数据
 * 第一个参数（int）：代表类型
 * 其余参数，根据类型确定
 *
 * @author ArgusAPM Team
 */
public interface IExtraDataCallback {
    Object parse(Object... args);
}
