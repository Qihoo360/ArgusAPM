package com.argusapm.gradle.internal.ajc

import com.argusapm.gradle.internal.concurrent.ITask
import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.utils.LogStatus
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.GradleException
import java.io.File

class AjcWeaver : ITask {
    var encoding: String? = null
    val inPath = arrayListOf<File>()
    var aspectPath = arrayListOf<File>()
    var classPath = arrayListOf<File>()
    var ajcArgs = arrayListOf<String>()
    var bootClassPath: String? = null
    var sourceCompatibility: String? = null
    var targetCompatibility: String? = null
    var outputDir: String? = null
    var outputJar: String? = null
    override fun call(): Any? {
        val log = PluginConfig.project.logger
        val args = mutableListOf(
                "-showWeaveInfo",
                "-encoding", encoding,
                "-source", sourceCompatibility,
                "-target", targetCompatibility,
                "-classpath", classPath.joinToString(File.pathSeparator),
                "-bootclasspath", bootClassPath
        )

        if (!inPath.isEmpty()) {
            args.add("-inpath")
            args.add(inPath.joinToString(File.pathSeparator))
        }
        if (!aspectPath.isEmpty()) {
            args.add("-aspectpath")
            args.add(aspectPath.joinToString(File.pathSeparator))
        }

        if (outputDir != null && !this.outputDir!!.isEmpty()) {
            args.add("-d")
            args.add(outputDir)
        }

        if (outputJar != null && !outputJar!!.isEmpty()) {
            args.add("-outjar")
            args.add(outputJar)
        }

        if (!ajcArgs.isEmpty()) {
            if (!ajcArgs.contains("-Xlint")) {
                args.add("-Xlint:ignore")
            }
            if (!ajcArgs.contains("-warn")) {
                args.add("-warn:none")
            }

            args.addAll(ajcArgs)
        } else {
            args.add("-Xlint:ignore")
            args.add("-warn:none")
        }

        val handler = MessageHandler(true)
        val m = Main()

        args.forEach {
            com.argusapm.gradle.internal.utils.log("$it")
        }

        LogStatus.weaveStart()

        m.run(args.toTypedArray(), handler)
        handler.getMessages(null, true).forEach { message ->
            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    log.error(message.message, message.thrown)
                    throw  GradleException(message.message, message.thrown)
                }

                IMessage.WARNING -> {
                    log.warn(message.message, message.thrown)
                }

                IMessage.INFO -> {
                    log.info(message.message, message.thrown)
                }

                IMessage.DEBUG -> {
                    log.debug(message.message, message.thrown)
                }
            }
        }
        return null
    }
}