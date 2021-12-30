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

import ish.common.types.TaskResultType;
import ish.oncourse.server.ICayenneService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    void execute() throws JobExecutionException {
        try {
            Connection connection = cayenneService.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM ExecutorManagerTask" +
                    " WHERE (timestampdiff(hour, createdOn, current_timestamp)>? AND status=?)" +
                    " OR (timestampdiff(hour, modifiedOn, current_timestamp)>? AND (status=? OR status=?))");
            statement.setInt(1, HOURS_INACTIVE_IN_PROGRESS);
            statement.setInt(2, TaskResultType.IN_PROGRESS.getDatabaseValue());
            statement.setInt(3, HOURS_INACTIVE_DONE);
            statement.setInt(4, TaskResultType.SUCCESS.getDatabaseValue());
            statement.setInt(5, TaskResultType.FAILURE.getDatabaseValue());
            statement.execute();
        } catch (SQLException throwables) {
            throw new JobExecutionException(throwables);
        }
    }
}
