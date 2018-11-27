package com.argusapm.android.aop;

import com.argusapm.android.core.job.net.i.QHC;
import com.argusapm.android.core.job.net.i.QURL;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * HTTPClient和URLConnection网络请求切面文件
 *
 * @author ArgusAPM Team
 */
@Aspect
public class TraceNetTrafficMonitor {
    @Pointcut("(!within(com.argusapm.android.aop.*) && ((!within(com.argusapm.android.**) && (!within(com.argusapm.android.core.job.net.i.*) && (!within(com.argusapm.android.core.job.net.impl.*) && (!within(com.qihoo360.mobilesafe.mms.transaction.MmsHttpClient) && !target(com.qihoo360.mobilesafe.mms.transaction.MmsHttpClient)))))))")
    public void baseCondition() {
    }

    @Pointcut("call(org.apache.http.HttpResponse org.apache.http.client.HttpClient.execute(org.apache.http.client.methods.HttpUriRequest)) && (target(httpClient) && (args(request) && baseCondition()))")
    public void httpClientExecuteOne(HttpClient httpClient, HttpUriRequest request) {
    }

    @Around("httpClientExecuteOne(httpClient, request)")
    public HttpResponse httpClientExecuteOneAdvice(HttpClient httpClient, HttpUriRequest request) throws IOException {
        return QHC.execute(httpClient, request);
    }

    @Pointcut("call(org.apache.http.HttpResponse org.apache.http.client.HttpClient.execute(org.apache.http.client.methods.HttpUriRequest, org.apache.http.protocol.HttpContext)) && (target(httpClient) && (args(request, context) && baseCondition()))")
    public void httpClientExecute2(HttpClient httpClient, HttpUriRequest request, HttpContext context) {
    }

    @Around("httpClientExecute2(httpClient, request, context)")
    public HttpResponse httpClientExecute2Advice(HttpClient httpClient, HttpUriRequest request, HttpContext context) throws IOException {
        return QHC.execute(httpClient, request, context);
    }

    @Pointcut("call(org.apache.http.HttpResponse org.apache.http.client.HttpClient.execute(org.apache.http.HttpHost, org.apache.http.HttpRequest)) && (target(httpClient) && (args(target, request) && baseCondition()))")
    public void httpClientExecuteThree(HttpClient httpClient, HttpHost target, HttpRequest request) {
    }

    @Around("httpClientExecuteThree(httpClient, target, request)")
    public HttpResponse httpClientExecuteThreeAdvice(HttpClient httpClient, HttpHost target, HttpRequest request) throws IOException {
        return QHC.execute(httpClient, target, request);
    }

    //修正钱包插件中阿里sdk导致aop失败的问题
    @Pointcut("call(org.apache.http.HttpResponse org.apache.http.client.HttpClient.execute(org.apache.http.HttpHost, org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)) && (target(httpClient) && (args(target, request, context) && baseCondition()))")
    public void httpClientExecuteFour(HttpClient httpClient, HttpHost target, HttpRequest request, HttpContext context) {
    }

    @Around("httpClientExecuteFour(httpClient, target, request, context)")
    public HttpResponse httpClientExecuteFourAdvice(HttpClient httpClient, HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        return QHC.execute(httpClient, target, request, context);
    }

    @Pointcut("call(* org.apache.http.client.HttpClient.execute(org.apache.http.client.methods.HttpUriRequest, org.apache.http.client.ResponseHandler)) && (target(httpClient) && (args(request, responseHandler) && baseCondition()))")
    public void httpClientExecuteFive(HttpClient httpClient, HttpUriRequest request, ResponseHandler responseHandler) {
    }

    @Around("httpClientExecuteFive(httpClient, request, responseHandler)")
    public Object httpClientExecuteFiveAdvice(HttpClient httpClient, HttpUriRequest request, ResponseHandler responseHandler) throws IOException {
        return QHC.execute(httpClient, request, responseHandler);
    }

    @Pointcut("call(* org.apache.http.client.HttpClient.execute(org.apache.http.client.methods.HttpUriRequest, org.apache.http.client.ResponseHandler, org.apache.http.protocol.HttpContext)) && (target(httpClient) && (args(request, responseHandler, context) && baseCondition()))")
    public void httpClientExecuteSix(HttpClient httpClient, HttpUriRequest request, ResponseHandler responseHandler, HttpContext context) {
    }

    @Around("httpClientExecuteSix(httpClient, request, responseHandler, context)")
    public Object httpClientExecuteSixAdvice(HttpClient httpClient, HttpUriRequest request, ResponseHandler responseHandler, HttpContext context) throws IOException {
        return QHC.execute(httpClient, request, responseHandler, context);
    }

    @Pointcut("call(* org.apache.http.client.HttpClient.execute(org.apache.http.HttpHost, org.apache.http.HttpRequest, org.apache.http.client.ResponseHandler)) && (target(httpClient) && (args(target, request, responseHandler) && baseCondition()))")
    public void httpClientExecuteSeven(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler responseHandler) {
    }

    @Around("httpClientExecuteSeven(httpClient, target, request, responseHandler)")
    public Object httpClientExecuteSevenAdvice(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler responseHandler) throws IOException {
        return QHC.execute(httpClient, target, request, responseHandler);
    }

    @Pointcut("call(* org.apache.http.client.HttpClient.execute(org.apache.http.HttpHost, org.apache.http.HttpRequest, org.apache.http.client.ResponseHandler, org.apache.http.protocol.HttpContext)) && (target(httpClient) && (args(target, request, responseHandler, context) && baseCondition()))")
    public void httpClientExecuteEight(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler responseHandler, HttpContext context) {
    }

    @Around("httpClientExecuteEight(httpClient, target, request, responseHandler, context)")
    public Object httpClientExecuteEightAdvice(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler responseHandler, HttpContext context) throws IOException {
        return QHC.execute(httpClient, target, request, responseHandler, context);
    }

    @Pointcut("call(java.net.URLConnection openConnection()) && (target(url) && baseCondition())")
    public void URLOpenConnectionOne(URL url) {
    }

    @Around("URLOpenConnectionOne(url)")
    public URLConnection URLOpenConnectionOneAdvice(URL url) throws IOException {
        return QURL.openConnection(url);
    }

    @Pointcut("call(java.net.URLConnection openConnection(java.net.Proxy)) && (target(url) && (args(proxy) && baseCondition()))")
    public void URLOpenConnectionTwo(URL url, Proxy proxy) {
    }

    @Around("URLOpenConnectionTwo(url, proxy)")
    public URLConnection URLOpenConnectionTwoAdvice(URL url, Proxy proxy) throws IOException {
        return QURL.openConnection(url, proxy);
    }
}
