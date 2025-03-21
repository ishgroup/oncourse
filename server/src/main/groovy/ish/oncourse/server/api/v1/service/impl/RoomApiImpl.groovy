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
import ish.oncourse.server.api.service.RoomApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.api.v1.service.RoomApi


class RoomApiImpl implements RoomApi {

    @Inject
    private RoomApiService entityApiService

    @Override
    void remove(Long id) {
        entityApiService.remove(id)
    }

    @Override
    RoomDTO get(Long id) {
        entityApiService.get(id)
    }

    @Override
    void create(RoomDTO room) {
        entityApiService.create(room)
    }

    @Override
    void update(Long id, RoomDTO room) {
        entityApiService.update(id, room)
    }
}
