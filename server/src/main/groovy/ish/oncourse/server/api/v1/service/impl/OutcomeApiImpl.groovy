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

import ish.oncourse.server.api.service.OutcomeApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.OutcomeDTO
import ish.oncourse.server.api.v1.service.OutcomeApi

import javax.inject.Inject

class OutcomeApiImpl implements OutcomeApi {

    @Inject
    private OutcomeApiService service

    @Override
    void bulkChange(DiffDTO diff) {
        service.bulkChange(diff)
    }

    @Override
    void create(OutcomeDTO outcome) {
        service.create(outcome)
    }

    @Override
    OutcomeDTO get(Long id) {
        service.get(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, OutcomeDTO outcome) {
        service.update(id, outcome)
    }
}
