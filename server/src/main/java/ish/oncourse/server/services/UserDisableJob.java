/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.SystemUser;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

@DisallowConcurrentExecution
public class UserDisableJob implements Job {

    private static final Logger logger = LogManager.getLogger();

    private final ICayenneService cayenneService;

    @Inject
    public UserDisableJob(ICayenneService cayenneService) {
        super();
        this.cayenneService = cayenneService;
        logger.info("cayenneService {}", cayenneService);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        DataContext dataContext = cayenneService.getNewContext();
        LocalDate time = LocalDate.now().minus(Period.ofYears(4));
        ObjectSelect.query(SystemUser.class)
                .where((SystemUser.LAST_LOGIN_ON.lte(Date.valueOf(time)).andExp(SystemUser.IS_ACTIVE.isTrue()))
                        .orExp(SystemUser.LAST_LOGIN_ON.isNull()
                                .andExp(SystemUser.CREATED_ON.lte(Date.valueOf(time)))
                                .andExp(SystemUser.IS_ACTIVE.isTrue())
                        ))
                .select(dataContext)
                .forEach(user -> {
                    try {
                        user.setIsActive(false);
                        dataContext.commitChanges();
                    } catch (Exception e) {
                        logger.error("User disable failed", e);
                        dataContext.rollbackChangesLocally();
                    }
                });
    }
}
