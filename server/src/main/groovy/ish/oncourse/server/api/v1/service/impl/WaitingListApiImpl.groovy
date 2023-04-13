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
import ish.oncourse.server.api.service.WaitingListApiService
import static ish.oncourse.server.api.v1.function.WaitingListFunctions.toDbWaitingList
import static ish.oncourse.server.api.v1.function.WaitingListFunctions.toRestWaitingList
import static ish.oncourse.server.api.v1.function.WaitingListFunctions.validateForSave
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.WaitingListDTO
import ish.oncourse.server.api.v1.service.WaitingListApi
import ish.oncourse.server.cayenne.WaitingList
import org.apache.cayenne.ObjectContext

class WaitingListApiImpl implements WaitingListApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private WaitingListApiService entityApiService

    @Override
    void create(WaitingListDTO waitingList) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(waitingList, context))

        WaitingList newWaitingList = context.newObject(WaitingList)
        toDbWaitingList(waitingList, newWaitingList, context)

        context.commitChanges()
    }

    @Override
    WaitingListDTO get(Long id) {
        toRestWaitingList(getRecordById(cayenneService.newContext, WaitingList, id))
    }

    @Override
    void update(Long id, WaitingListDTO waitingListDTO) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext

        WaitingList entity = getRecordById(context, WaitingList, id)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForSave(waitingListDTO, context, id))

        toDbWaitingList(waitingListDTO, entity, context)
        context.commitChanges()
    }

    @Override
    void bulkChange(DiffDTO diff) {
        entityApiService.bulkChange(diff)
    }

    @Override
    void bulkDelete(DiffDTO diff) {
        entityApiService.bulkRemove(diff)
    }
}
