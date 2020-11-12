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
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.v1.function.RoomFunctions.validateForDelete
import ish.oncourse.server.api.v1.service.RoomValidationApi
import ish.oncourse.server.cayenne.Room

class RoomValidationApiImpl implements RoomValidationApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    void remove(Long id) {
        Room room = getRecordById(cayenneService.newContext, Room, id)

        checkForBadRequest(validateEntityExistence(id, room))
        checkForBadRequest(validateForDelete(room))
    }
}
