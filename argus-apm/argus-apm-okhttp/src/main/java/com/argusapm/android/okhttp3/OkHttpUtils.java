package com.argusapm.android.okhttp3;

import java.util.List;

import okhttp3.Interceptor;

/**
 * 协助ASM进行代码织入
 *
 * @author ArgusAPM Team
 */
public class OkHttpUtils {
    public static void insertToOkHttpClientBuilder(List<Interceptor> interceptors) {
        try {
            boolean hasAddNetWorkInterceptor = false;
            for (Interceptor interceptor : interceptors) {
                if (interceptor instanceof NetWorkInterceptor) {
                    hasAddNetWorkInterceptor = true;
                    break;
                }
            }
            if (!hasAddNetWorkInterceptor) {
                interceptors.add(new NetWorkInterceptor());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
