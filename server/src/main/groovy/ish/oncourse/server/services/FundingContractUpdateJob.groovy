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

package ish.oncourse.server.services

import javax.inject.Inject
import ish.oncourse.types.FundingStatus
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.FundingUpload
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

@DisallowConcurrentExecution
class FundingContractUpdateJob implements Job {

    private ICayenneService cayenneService

    @Inject
    FundingContractUpdateJob(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    @Override
    void execute(JobExecutionContext executionContext) throws JobExecutionException {
        execute()
    }

    void execute() {
        ObjectContext context = cayenneService.newNonReplicatingContext

        Date timestampForFailed = new Date() - 3
        Date timestampForExported = new Date() - 28

        List<FundingUpload> uploadsToDelete = ObjectSelect.query(FundingUpload)
                .where(FundingUpload.STATUS.eq(FundingStatus.FAILED).andExp(FundingUpload.CREATED_ON.lt(timestampForFailed))
                        .orExp(FundingUpload.STATUS.eq(FundingStatus.EXPORTED).andExp(FundingUpload.CREATED_ON.lt(timestampForExported))))
                .select(context)

        context.deleteObjects(uploadsToDelete)
        context.commitChanges()
    }
}
