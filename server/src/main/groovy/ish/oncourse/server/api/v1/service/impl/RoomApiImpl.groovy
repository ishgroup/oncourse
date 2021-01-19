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
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import ish.oncourse.server.api.service.RoomApiService
import static ish.oncourse.server.api.v1.function.RoomFunctions.toDbRoom
import static ish.oncourse.server.api.v1.function.RoomFunctions.toRestRoom
import static ish.oncourse.server.api.v1.function.RoomFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.RoomFunctions.validateForSave
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.api.v1.service.RoomApi
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext

class RoomApiImpl implements RoomApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private DocumentService documentService

    @Inject
    private SystemUserService systemUserService

    @Inject
    private RoomApiService entityApiService

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Room entity = getRecordById(context, Room, id)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        deleteRecord(context, entity)
    }

    @Override
    RoomDTO get(Long id) {
        toRestRoom(getRecordById(cayenneService.newContext, Room, id), preferenceController, documentService)
    }

    @Override
    void create(RoomDTO room) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(room, context))

        Room newRoom = context.newObject(Room)
        toDbRoom(room, newRoom, context, context.localObject(systemUserService.currentUser))

        context.commitChanges()
    }

    @Override
    void update(Long id, RoomDTO room) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext

        Room entity = getRecordById(context, Room, id)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForSave(room, context, id))

        toDbRoom(room, entity, context, context.localObject(systemUserService.currentUser))
        context.commitChanges()
    }

    @Override
    void bulkChange(DiffDTO diff) {
        entityApiService.bulkChange(diff)
    }
}
