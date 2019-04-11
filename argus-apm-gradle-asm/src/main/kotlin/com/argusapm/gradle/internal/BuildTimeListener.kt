package com.argusapm.gradle.internal

import com.argusapm.gradle.internal.utils.log
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class BuildTimeListener : TaskExecutionListener, BuildListener {

    private var startTime: Long = 0L
    private var times = mutableListOf<Pair<Long, String>>()

    override fun buildStarted(gradle: Gradle) {}
    override fun settingsEvaluated(settings: Settings) {}
    override fun projectsLoaded(gradle: Gradle) {}
    override fun projectsEvaluated(gradle: Gradle) {}

    override fun buildFinished(result: BuildResult) {
        log("Task spend time:")
        times.filter { it.first > 50 }
                .forEach { log("%7sms\t%s".format(it.first, it.second)) }
    }

    override fun beforeExecute(task: Task) {
        startTime = System.currentTimeMillis()
    }

    override fun afterExecute(task: Task, state: TaskState) {
        val ms = System.currentTimeMillis() - startTime
        times.add(Pair(ms, task.path))
        task.project.logger.warn("${task.path} spend ${ms}ms")
    }
}