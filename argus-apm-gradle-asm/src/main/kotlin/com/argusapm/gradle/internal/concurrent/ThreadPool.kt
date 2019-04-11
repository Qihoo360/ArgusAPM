package com.argusapm.gradle.internal.concurrent

import java.util.concurrent.Executors

class ThreadPool {
    private val executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1)
    val taskList = arrayListOf<ITask>()

    fun addTask(task: ITask) {
        taskList.add(task)
    }

    fun startWork() {
        executorService.invokeAll(taskList)
        taskList.clear()
    }
}