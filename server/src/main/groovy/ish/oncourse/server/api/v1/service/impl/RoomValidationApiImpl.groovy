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
import ish.oncourse.server.api.dao.RoomDao
import ish.oncourse.server.api.service.RoomApiService
import ish.oncourse.server.api.v1.service.RoomValidationApi
import ish.oncourse.server.cayenne.Room

class RoomValidationApiImpl implements RoomValidationApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private RoomApiService roomApiService

    @Inject
    private RoomDao roomDao

    @Override
    void remove(Long id) {
        Room room = roomDao.getById(cayenneService.newContext, id)
        roomApiService.validateModelBeforeRemove(room)
    }
}
