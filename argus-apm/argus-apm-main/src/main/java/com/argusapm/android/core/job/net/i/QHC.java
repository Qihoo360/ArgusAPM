
package com.argusapm.android.core.job.net.i;

import com.argusapm.android.api.ApmTask;
import com.argusapm.android.core.Manager;
import com.argusapm.android.core.job.net.impl.AopHttpClient;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Aop使用
 *
 * @author ArgusAPM Team
 */
public class QHC {
    public static HttpResponse execute(HttpClient client, HttpUriRequest request) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, request)
                : client.execute(request);
    }

    public static HttpResponse execute(HttpClient client, HttpHost host, HttpRequest request, HttpContext context) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, host, request, context)
                : client.execute(host, request, context);
    }

    public static <T> T execute(HttpClient client, HttpHost host, HttpRequest request, ResponseHandler<? extends T> handler, HttpContext context) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, host, request, handler, context)
                : client.execute(host, request, handler, context);
    }

    public static <T> T execute(HttpClient client, HttpHost host, HttpRequest request, ResponseHandler<? extends T> handler) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, host, request, handler)
                : client.execute(host, request, handler);
    }

    public static HttpResponse execute(HttpClient client, HttpHost host, HttpRequest request) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, host, request)
                : client.execute(host, request);
    }

    public static HttpResponse execute(HttpClient client, HttpUriRequest request, HttpContext context) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, request, context)
                : client.execute(request, context);
    }

    public static <T> T execute(HttpClient client, HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, request, responseHandler, httpContext)
                : client.execute(request, responseHandler, httpContext);
    }

    public static <T> T execute(HttpClient client, HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return isTaskRunning()
                ? AopHttpClient.execute(client, request, responseHandler)
                : client.execute(request, responseHandler);
    }

    private static boolean isTaskRunning() {
        return Manager.getInstance().getTaskManager().taskIsCanWork(ApmTask.TASK_NET);
    }
}