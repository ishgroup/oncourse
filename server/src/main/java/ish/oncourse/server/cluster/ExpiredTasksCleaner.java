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

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import ish.common.types.TaskResultType;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.v1.model.ProcessStatusDTO;
import ish.oncourse.server.cayenne.ExecutorManagerTask;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This job removes all {@link ClusteredExecutorManager} tasks
 * that are in progress longer than 2 hours or finished more than hour ago.
 */
@DisallowConcurrentExecution
public class ExpiredTasksCleaner implements Job {

    private static final int HOURS_INACTIVE_IN_PROGRESS = 2;
    private static final int HOURS_INACTIVE_DONE = 1;

    @Inject
    private ICayenneService cayenneService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        execute();
    }

    void execute() {
        ObjectContext context = cayenneService.getNewNonReplicatingContext();

        LocalDateTime now = LocalDateTime.now();

        List<ExecutorManagerTask> expiredTasks = ObjectSelect.query(ExecutorManagerTask.class)
                .where(ExecutorManagerTask.CREATED_ON.lt(now.minusHours(HOURS_INACTIVE_IN_PROGRESS))
                        .andExp(ExecutorManagerTask.STATUS.eq(TaskResultType.IN_PROGRESS)))
                .or(ExecutorManagerTask.MODIFIED_ON.lt(now.minusHours(HOURS_INACTIVE_DONE))
                        .andExp(ExecutorManagerTask.STATUS.in(TaskResultType.SUCCESS, TaskResultType.FAILURE)))
                .select(context);

        context.deleteObjects(expiredTasks);
        context.commitChanges();
    }
}
