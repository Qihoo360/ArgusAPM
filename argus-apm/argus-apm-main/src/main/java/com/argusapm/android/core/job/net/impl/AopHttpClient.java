
package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.Env;
import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.utils.LogX;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopHttpClient {
    private static final String SUB_TAG = "AopHttpClient";

    public static HttpResponse execute(HttpClient httpClient, HttpUriRequest request) throws IOException {
        NetInfo data = new NetInfo();
        HttpResponse response = httpClient.execute(handleRequest(request, data));
        handleResponse(response, data);
        return response;
    }

    public static HttpResponse execute(HttpClient client, HttpHost host, HttpRequest request, HttpContext context) throws IOException {
        NetInfo data = new NetInfo();
        HttpResponse response = client.execute(host, handleRequest(host, request, data), context);
        handleResponse(response, data);
        return response;
    }

    public static <T> T execute(HttpClient client, HttpHost host, HttpRequest request, ResponseHandler<? extends T> handler, HttpContext context) throws IOException {
        NetInfo data = new NetInfo();
        return client.execute(host, handleRequest(host, request, data), AopResponseHandler.wrap(handler, data), context);
    }

    public static <T> T execute(HttpClient client, HttpHost host, HttpRequest request, ResponseHandler<? extends T> handler) throws IOException {
        NetInfo data = new NetInfo();
        return client.execute(host, handleRequest(host, request, data), AopResponseHandler.wrap(handler, data));
    }

    public static HttpResponse execute(HttpClient client, HttpHost host, HttpRequest request) throws IOException {
        NetInfo data = new NetInfo();
        HttpResponse response = client.execute(host, handleRequest(host, request, data));
        handleResponse(response, data);
        return response;
    }

    public static HttpResponse execute(HttpClient client, HttpUriRequest request, HttpContext context) throws IOException {
        NetInfo data = new NetInfo();
        HttpResponse response = client.execute(handleRequest(request, data), context);
        handleResponse(response, data);
        return response;
    }

    public static <T> T execute(HttpClient client, HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException {
        NetInfo data = new NetInfo();
        return client.execute(handleRequest(request, data), AopResponseHandler.wrap(responseHandler, data), httpContext);
    }

    public static <T> T execute(HttpClient client, HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        NetInfo data = new NetInfo();
        return client.execute(handleRequest(request, data), AopResponseHandler.wrap(responseHandler, data));
    }

    private static HttpUriRequest handleRequest(HttpUriRequest request, NetInfo data) {
        data.setURL(request.getURI().toString());
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
            if (entityRequest.getEntity() != null) {
                entityRequest.setEntity(new AopHttpRequestEntity(entityRequest.getEntity(), data));
            }
            return (HttpUriRequest) entityRequest;
        }
        return request;
    }

    private static HttpRequest handleRequest(HttpHost host, HttpRequest request, NetInfo data) {
        RequestLine requestLine = request.getRequestLine();
        if (requestLine != null) {
            String uri = requestLine.getUri();
            int i = (uri != null) && (uri.length() >= 10) && (uri.substring(0, 10).indexOf("://") >= 0) ? 1 : 0;
            if ((i == 0) && (uri != null) && (host != null)) {
                String uriFromHost = host.toURI().toString();
                data.setURL(uriFromHost + ((uriFromHost.endsWith("/")) || (uri.startsWith("/")) ? "" : "/") + uri);
            } else if (i != 0) {
                data.setURL(uri);
            }
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
            if (entityRequest.getEntity() != null) {
                entityRequest.setEntity(new AopHttpRequestEntity(entityRequest.getEntity(), data));
            }
            return entityRequest;
        }
        return request;
    }

    private static HttpResponse handleResponse(HttpResponse response, NetInfo data) {
        data.setStatusCode(response.getStatusLine().getStatusCode());
        Header[] headers = response.getHeaders("Content-Length");
        if ((headers != null) && (headers.length > 0)) {
            try {
                long l = Long.parseLong(headers[0].getValue());
                data.setReceivedBytes(l);
                if (DEBUG) {
                    LogX.d(TAG, SUB_TAG, "-handleResponse--end--1");
                }
                data.end();
            } catch (NumberFormatException e) {
                if (Env.DEBUG) {
                    LogX.d(TAG, SUB_TAG, "NumberFormatException ex : " + e.getMessage());
                }
            }
        } else if (response.getEntity() != null) {
            response.setEntity(new AopHttpResponseEntity(response.getEntity(), data));
        } else {
            data.setReceivedBytes(0);
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "----handleResponse--end--2");
            }
            data.end();
        }
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "execute:" + data.toString());
        }
        return response;
    }
}