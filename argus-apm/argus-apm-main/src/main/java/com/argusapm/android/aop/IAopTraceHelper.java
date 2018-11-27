package com.argusapm.android.aop;

/**
 * @author ArgusAPM Team
 */
public interface IAopTraceHelper {
    void dispatch(long startTime,
                  String kind,
                  String sign,
                  Object[] args,
                  Object target,
                  Object thiz,
                  String location,
                  String staticPartStr,
                  String methodName,
                  Object result
    );
}
