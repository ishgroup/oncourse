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
import ish.oncourse.server.api.service.WaitingListApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.WaitingListDTO
import ish.oncourse.server.api.v1.service.WaitingListApi


class WaitingListApiImpl implements WaitingListApi {

    @Inject
    private WaitingListApiService entityApiService

    @Override
    void create(WaitingListDTO waitingList) {
        entityApiService.create(waitingList)
    }

    @Override
    WaitingListDTO get(Long id) {
        entityApiService.get(id)
    }

    @Override
    void update(Long id, WaitingListDTO waitingListDTO) {
        entityApiService.update(id, waitingListDTO)
    }

    @Override
    void bulkChange(DiffDTO diff) {
        entityApiService.bulkChange(diff)
    }
}
