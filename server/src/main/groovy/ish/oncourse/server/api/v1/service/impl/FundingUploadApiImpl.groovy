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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.function.FundingUploadFunctions
import ish.oncourse.server.api.v1.model.FundingUploadDTO
import ish.oncourse.server.api.v1.service.FundingUploadApi
import ish.oncourse.server.cayenne.FundingUpload
import ish.oncourse.server.services.ISystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.addAqlExp
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import static ish.oncourse.server.api.v1.function.FundingUploadFunctions.validateStatus

class FundingUploadApiImpl implements FundingUploadApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ISystemUserService systemUserService

    @Inject
    private AqlService aqlService

    @Override
    List<FundingUploadDTO> get(String search) {
        boolean lastSettingsFound = false

        ObjectContext context = cayenneService.newContext
        ObjectSelect query = ObjectSelect.query(FundingUpload)
        query = addAqlExp(search, FundingUpload, context, query, aqlService) as ObjectSelect<FundingUpload>

        query.orderBy(FundingUpload.CREATED_ON.desc())
            .select(context)
            .collect { it  ->
                if (!lastSettingsFound && it.systemUser.id == systemUserService.currentUser?.id && it.settings) {
                    lastSettingsFound = true
                    return FundingUploadFunctions.toRestFundingUpload(it, true)
                }
                return FundingUploadFunctions.toRestFundingUpload(it)
            }
    }

    @Override
    void update(FundingUploadDTO upload) {
        checkForBadRequest(validateIdParam(upload?.id))
        checkForBadRequest(validateStatus(upload?.status))

        ObjectContext context = cayenneService.newContext
        FundingUpload fundingUpload = getRecordById(context, FundingUpload, upload.id)

        checkForBadRequest(validateEntityExistence(upload.id, fundingUpload))

        fundingUpload.status = FundingUploadFunctions.fundingUploadStatusMap.getByValue(upload.status)
        context.commitChanges()
    }
}
