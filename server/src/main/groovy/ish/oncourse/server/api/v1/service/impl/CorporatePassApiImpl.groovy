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

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import static ish.oncourse.server.api.v1.function.CorporatePassFunctions.toDbCorporatePass
import static ish.oncourse.server.api.v1.function.CorporatePassFunctions.toRestCorporatePass
import static ish.oncourse.server.api.v1.function.CorporatePassFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.CorporatePassFunctions.validateForSave
import ish.oncourse.server.api.v1.model.CorporatePassDTO
import ish.oncourse.server.api.v1.service.CorporatePassApi
import ish.oncourse.server.cayenne.CorporatePass
import org.apache.cayenne.ObjectContext

class CorporatePassApiImpl implements CorporatePassApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    void create(CorporatePassDTO corporatePass) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(corporatePass, context))

        CorporatePass newCorporatePass = context.newObject(CorporatePass)
        toDbCorporatePass(corporatePass, newCorporatePass, context)

        context.commitChanges()
    }

    @Override
    CorporatePassDTO get(Long id) {
        toRestCorporatePass(getRecordById(cayenneService.newContext, CorporatePass, id))
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        CorporatePass entity = getRecordById(context, CorporatePass, id)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        deleteRecord(context, entity)
    }

    @Override
    void update(Long id, CorporatePassDTO corporatePass) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext

        CorporatePass dbCorporatePass = getRecordById(context, CorporatePass, id)
        checkForBadRequest(validateEntityExistence(id, dbCorporatePass))
        checkForBadRequest(validateForSave(corporatePass, context, id))

        toDbCorporatePass(corporatePass, dbCorporatePass, context)
        context.commitChanges()
    }
}
