package com.argusapm.gradle

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.Sets
import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.ajc.AjcWeaverManager
import com.argusapm.gradle.internal.cutter.FileFilter
import com.argusapm.gradle.internal.cutter.InputSourceCutter
import com.argusapm.gradle.internal.cutter.InputSourceFileStatus
import com.argusapm.gradle.internal.utils.LogStatus
import com.argusapm.gradle.internal.utils.outputFiles
import org.gradle.api.Project

internal class AspectJTransform(private val project: Project) : Transform() {
    override fun getName(): String {
        return AppConstant.TRANSFORM_NAME
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)!!
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation) {
        val transformTask = transformInvocation.context as TransformTask
        LogStatus.logStart(transformTask.variantName)

        //第一步：对输入源Class文件进行切割分组
        val fileFilter = FileFilter(project, transformTask.variantName)
        val inputSourceFileStatus = InputSourceFileStatus()
        InputSourceCutter(transformInvocation, fileFilter, inputSourceFileStatus).startCut()

        //第二步：如果含有AspectJ文件,则开启织入;否则,将输入源输出到目标目录下
        if (PluginConfig.argusApmConfig().enabled && fileFilter.hasAspectJFile()) {
            AjcWeaverManager(transformInvocation, inputSourceFileStatus).weaver()
        } else {
            outputFiles(transformInvocation)
        }

        LogStatus.logEnd(transformTask.variantName)
    }
}