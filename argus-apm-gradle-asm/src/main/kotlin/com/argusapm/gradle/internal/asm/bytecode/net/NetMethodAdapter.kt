package com.argusapm.gradle.internal.bytecode.func

import com.argusapm.gradle.internal.asm.bytecode.net.NetConstans
import com.argusapm.gradle.internal.utils.log
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.commons.LocalVariablesSorter

class NetMethodAdapter(api: Int, access: Int, desc: String?, mv: MethodVisitor?) : LocalVariablesSorter(api, access, desc, mv) {

    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {

        if (owner == NetConstans.HTTPCLIENT && name == NetConstans.EXECUTE) {
            when (desc) {
                NetConstans.REQUEST -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/HttpResponse;",
                            false);
                }
                NetConstans.REQUEST_CONTEXT -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/protocol/HttpContext;)Lorg/apache/http/HttpResponse;",
                            false);
                }
                NetConstans.REQUEST_RESPONSEHANDLER -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;)Ljava/lang/Object;",
                            false);
                }
                NetConstans.REQUEST_RESPONSEHANDLER_CONTEXT -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;",
                            false);
                }
                NetConstans.HOST_REQUEST -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;)Lorg/apache/http/HttpResponse;",
                            false);
                }
                NetConstans.HOST_REQUEST_CONTEXT -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;Lorg/apache/http/protocol/HttpContext;)Lorg/apache/http/HttpResponse;",
                            false);
                }
                NetConstans.HOST_REQUEST_RESPONSEHANDLER -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;Lorg/apache/http/client/ResponseHandler;)Ljava/lang/Object;",
                            false);
                }
                NetConstans.HOST_REQUEST_RESPONSEHANDLER_CONTEXT -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QHC",
                            "execute",
                            "(Lorg/apache/http/client/HttpClient;Lorg/apache/http/HttpHost;Lorg/apache/http/HttpRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;",
                            false);
                }
                else -> super.visitMethodInsn(opcode, owner, name, desc, itf)
            }
        } else if (owner == NetConstans.URL && name == NetConstans.OPEN_CONNECTION) {
            when (desc) {
                NetConstans.URL_CONNECTION -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QURL",
                            "openConnection",
                            "(Ljava/net/URL;)Ljava/net/URLConnection;",
                            false)
                }
                NetConstans.URL_CONNECTION_PROXY -> {
                    mv.visitMethodInsn(INVOKESTATIC,
                            "com/argusapm/android/core/job/net/i/QURL",
                            "openConnection",
                            "(Ljava/net/URL;Ljava/net/Proxy;)Ljava/net/URLConnection;",
                            false)
                }
                else -> super.visitMethodInsn(opcode, owner, name, desc, itf)
            }
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }
}