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
import ish.oncourse.server.api.service.CorporatePassApiService
import ish.oncourse.server.api.v1.model.CorporatePassDTO
import ish.oncourse.server.api.v1.service.CorporatePassApi

class CorporatePassApiImpl implements CorporatePassApi {

    @Inject
    private CorporatePassApiService corporatePassApiService

    @Override
    void create(CorporatePassDTO corporatePass) {
        corporatePassApiService.create(corporatePass)
    }

    @Override
    CorporatePassDTO get(Long id) {
        corporatePassApiService.get(id)
    }

    @Override
    void remove(Long id) {
        corporatePassApiService.remove(id)
    }

    @Override
    void update(Long id, CorporatePassDTO corporatePass) {
        corporatePassApiService.update(id, corporatePass)
    }
}
