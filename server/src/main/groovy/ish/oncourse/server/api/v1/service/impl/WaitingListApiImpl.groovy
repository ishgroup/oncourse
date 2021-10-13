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

import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.service.WaitingListApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.WaitingListDTO
import ish.oncourse.server.api.v1.service.WaitingListApi
import ish.oncourse.server.cayenne.WaitingList
import org.apache.cayenne.ObjectContext

import javax.inject.Inject

import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.*
import static ish.oncourse.server.api.v1.function.WaitingListFunctions.*

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
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        WaitingList entity = getRecordById(context, WaitingList, id)

        checkForBadRequest(validateEntityExistence(id, entity))

        deleteRecord(context, entity)
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
}
