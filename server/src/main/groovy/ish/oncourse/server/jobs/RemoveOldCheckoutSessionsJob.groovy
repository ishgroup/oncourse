/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.jobs


import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.CheckoutSession
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

/**
 * If user closed payment form (without submit / new create session request) session will remain in our database
 */
@DisallowConcurrentExecution
class RemoveOldCheckoutSessionsJob implements Job {
    private static final Logger logger = LogManager.getLogger()
    private static final int OFFSET_TIME_DATS = 7

    @Inject
    private ICayenneService cayenneService


    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        def cayenneContext = cayenneService.newContext
        def sessionsToDelete = ObjectSelect.query(CheckoutSession)
                .where(CheckoutSession.CREATED_ON.lt(new Date().minus(OFFSET_TIME_DATS)))
                .select(cayenneContext)
        cayenneContext.deleteObjects(sessionsToDelete)
    }
}
