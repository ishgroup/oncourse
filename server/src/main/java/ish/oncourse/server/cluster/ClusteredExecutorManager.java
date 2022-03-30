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


package ish.oncourse.server.cluster;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.inject.Inject;

import ish.common.types.TaskResultType;
import ish.oncourse.server.CayenneService;
import ish.oncourse.server.api.v1.model.ProcessStatusDTO;
import ish.oncourse.server.cayenne.ExecutorManagerTask;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

public class ClusteredExecutorManager {

    private static final int MAX_THREADS = 10;

    private static final ExecutorListener EMPTY_LISTENER = () -> {};

    @Inject
    CayenneService cayenneService;

    private final ExecutorService workerThreadExecutor = Executors.newFixedThreadPool(MAX_THREADS);

    private final Map<String, ExecutorListener> listenerMap = new ConcurrentHashMap<>();
    private final Map<String, ExecutorManagerTask> localTasks = new ConcurrentHashMap<>();

    public String submit(Callable<? extends TaskResult> task) {
        return submit(task, null);
    }

    public String submit(Callable<? extends TaskResult> task, ExecutorListener listener) {
        String key = UUID.randomUUID().toString();
        listenerMap.put(key, listener == null ? EMPTY_LISTENER : listener);

        Callable<? extends TaskResult> decoratedTask = decorateTask(key, task);
        Future<? extends TaskResult> future = workerThreadExecutor.submit(decoratedTask);
        storeTaskInfo(key, future);

        return key;
    }

    public TaskResult getResult(String key) {
        // save DB lookup for the local task
        if(isLocal(key)) {
            return localTasks.get(key).getTaskResult();
        }

        ExecutorManagerTask task = ObjectSelect.query(ExecutorManagerTask.class)
                .where(ExecutorManagerTask.KEY.eq(key))
                .selectOne(cayenneService.getNewContext());
        return task.getTaskResult();
    }

    public ProcessStatusDTO getStatus(String key) {
        // save DB lookup for the local task
        TaskResultType taskStatus;
        if(isLocal(key)) {
            taskStatus = localTasks.get(key).getStatus();
        } else {
            taskStatus = ObjectSelect.columnQuery(ExecutorManagerTask.class, ExecutorManagerTask.STATUS)
                    .where(ExecutorManagerTask.KEY.eq(key))
                    .selectFirst(cayenneService.getNewContext());
        }

        if(taskStatus == null) {
            return ProcessStatusDTO.NOT_FOUND;
        }

        switch (taskStatus) {
            case SUCCESS:
                return ProcessStatusDTO.FINISHED;
            case FAILURE:
                return ProcessStatusDTO.FAILED;
            case IN_PROGRESS:
                return ProcessStatusDTO.IN_PROGRESS;
        }

        return ProcessStatusDTO.NOT_FOUND;
    }

    public void addListener(String key, ExecutorListener listener) {
        listenerMap.compute(key, (k, presentListener) -> {
            if(presentListener != null) {
                return presentListener.andThen(listener);
            }
            return listener;
        });
    }

    public void interrupt(String key) {
        // TODO need to make this method to support cluster environment
        localTasks.computeIfPresent(key, (k, task) -> {
            task.getLocalTask().cancel(true);
            return null;
        });
    }

    private void storeTaskInfo(String key, Future<? extends TaskResult> future) {
        DataContext context = cayenneService.getNewContext();
        ExecutorManagerTask task = context.newObject(ExecutorManagerTask.class);
        task.setKey(key);
        task.setStatus(TaskResultType.IN_PROGRESS);
        task.setLocalTask(future);
        context.commitChanges();
        localTasks.put(key, task);
    }

    private void onTaskComplete(TaskResult result, String key) {
        if(!isLocal(key)) {
            return;
        }

        ExecutorManagerTask task = localTasks.get(key);
        if(task == null) {
            return;
        }

        listenerMap.remove(key).onDone();

        if(result == null) {
            task.setStatus(TaskResultType.FAILURE);
        } else if(result.getError() != null) {
            task.setStatus(TaskResultType.FAILURE);
            task.setErrorMessage(result.getError());
        } else {
            task.setStatus(TaskResultType.SUCCESS);
            task.setResult(result.getData());
            if(result.getStatusMessage() != null) {
                task.setStatusMessage(result.getStatusMessage());
            }
            task.setTaskName(result.getName());
            task.setOutputType(result.getResultOutputType());
        }

        task.setLocalTask(null);
        task.getContext().commitChanges();

        localTasks.remove(key);
    }

    private Callable<? extends TaskResult> decorateTask(String key, Callable<? extends TaskResult> task) {
        return () -> {
            TaskResult result = null;
            try {
                result = task.call();
            } finally {
                onTaskComplete(result, key);
            }
            return result;
        };
    }

    private boolean isLocal(String key) {
        return localTasks.containsKey(key);
    }
}
