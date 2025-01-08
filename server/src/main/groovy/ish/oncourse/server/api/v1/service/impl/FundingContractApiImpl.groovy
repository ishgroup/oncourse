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
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.v1.model.FundingSourceDTO
import ish.oncourse.server.api.v1.service.FundingContractApi
import ish.oncourse.server.cayenne.FundingSource
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import static ish.oncourse.server.api.v1.function.FundingContractFunctions.toDbFundingContract
import static ish.oncourse.server.api.v1.function.FundingContractFunctions.toRestFundingContract
import static ish.oncourse.server.api.v1.function.FundingContractFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.FundingContractFunctions.validateForPatch
import static ish.oncourse.server.api.v1.function.FundingContractFunctions.validateForSave

class FundingContractApiImpl implements FundingContractApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private FundingSourceDao fundingSourceDao


    @Override
    List<FundingSourceDTO> get() {

        fundingSourceDao.getAll()
                .collect{ toRestFundingContract(it) }
    }

    @Override
    void patch(List<FundingSourceDTO> fundingContracts) {
        ObjectContext context = cayenneService.newContext
        fundingContracts.each { checkForBadRequest(validateForPatch(it)) }
        fundingContracts.each { it ->
            if (it.id != null) {
                FundingSource dbObj = fundingSourceDao.getById(context, it.id)
                dbObj.active = it.active
                dbObj
            }
        }
        context.commitChanges()
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        FundingSource entity = fundingSourceDao.getById(context, id)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        context.deleteObject(entity)
        context.commitChanges()
    }

    @Override
    void update(List<FundingSourceDTO> fundingContracts) {
        ObjectContext context = cayenneService.newContext

        fundingContracts.each { checkForBadRequest(validateForSave(it, context)) }

        fundingContracts.each{ it ->
            FundingSource dbObj = it.id ?
                    fundingSourceDao.getById(context, it.id) :
                    context.newObject(FundingSource)

            toDbFundingContract(it, dbObj)
        }

        context.commitChanges()
    }
}
