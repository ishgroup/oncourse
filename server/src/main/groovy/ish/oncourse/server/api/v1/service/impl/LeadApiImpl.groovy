/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.api.service.LeadApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.LeadDTO
import ish.oncourse.server.api.v1.service.LeadApi

class LeadApiImpl implements LeadApi {

    @Inject
    private LeadApiService leadApiService

    @Override
    void bulkChange(DiffDTO diff) {
        leadApiService.bulkChange(diff)
    }

    @Override
    void create(LeadDTO lead) {
        leadApiService.create(lead)
    }

    @Override
    LeadDTO get(Long id) {
        return leadApiService.get(id)
    }

    @Override
    void remove(Long id) {
        leadApiService.remove(id)
    }

    @Override
    void update(Long id, LeadDTO lead) {
        leadApiService.update(id, lead)
    }
}
