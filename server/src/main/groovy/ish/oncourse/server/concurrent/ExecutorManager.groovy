/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.concurrent

import groovy.transform.CompileStatic
import ish.oncourse.server.api.v1.model.ProcessStatusDTO

import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
@CompileStatic
class ExecutorManager {

    private static final int MAX_THREADS = 10

    private final Map<String, Future<?>> taskMap = new ConcurrentHashMap<>()
    private final Map<String, String> messageMap = new ConcurrentHashMap<>()

    private ExecutorService workerThreadExecutor = Executors.newFixedThreadPool(MAX_THREADS)

    String submit(Callable task) {
        String key = UUID.randomUUID().toString()
        Future<?> future = workerThreadExecutor.submit(task)
        taskMap.put(key, future)

        key
    }

    def getResult(String key) {
        taskMap[key].get()
    }

    ProcessStatusDTO getStatus(String key) {
        if (!taskMap[key]) {
            ProcessStatusDTO.NOT_FOUND
        } else if (taskMap[key].cancelled) {
            ProcessStatusDTO.FAILED
        } else if (taskMap[key].done) {
            ProcessStatusDTO.FINISHED
        }  else {
            ProcessStatusDTO.IN_PROGRESS
        }
    }

    String getMessage(String key) {
        messageMap[key]
    }

    void interrupt(String key) {
        taskMap[key]?.cancel(true)
        messageMap.put(key, 'Process cancelled.')
    }

}
