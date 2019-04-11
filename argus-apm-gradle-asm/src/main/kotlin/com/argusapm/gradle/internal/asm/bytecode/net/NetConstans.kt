package com.argusapm.gradle.internal.asm.bytecode.net

class NetConstans {
    companion object {
        const val HTTPCLIENT = "org/apache/http/client/HttpClient"
        const val EXECUTE = "execute"
        const val REQUEST = "(Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/HttpResponse;"
        const val REQUEST_CONTEXT = "(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/protocol/HttpContext;"
        const val REQUEST_RESPONSEHANDLER = "(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;)Ljava/lang/Object;"
        const val REQUEST_RESPONSEHANDLER_CONTEXT = "(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;"
        const val HOST_REQUEST = "(Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;)Lorg/apache/http/HttpResponse;"
        const val HOST_REQUEST_CONTEXT = "(Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;Lorg/apache/http/protocol/HttpContext;)Lorg/apache/http/HttpResponse;"
        const val HOST_REQUEST_RESPONSEHANDLER = "(Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;Lorg/apache/http/client/ResponseHandler;)Ljava/lang/Object;"
        const val HOST_REQUEST_RESPONSEHANDLER_CONTEXT = "(Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;"


        const val URL = "java/net/URL"
        const val OPEN_CONNECTION = "openConnection"
        const val URL_CONNECTION = "()Ljava/net/URLConnection;"
        const val URL_CONNECTION_PROXY = "(Ljava/net/Proxy;)Ljava/net/URLConnection;"

    }
}