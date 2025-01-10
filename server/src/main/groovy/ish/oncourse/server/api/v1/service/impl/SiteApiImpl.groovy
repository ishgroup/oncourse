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
import ish.oncourse.server.api.service.SiteApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.api.v1.service.SiteApi


class SiteApiImpl implements SiteApi {

    @Inject
    private SiteApiService entityApiService

    @Override
    void remove(Long id) {
        entityApiService.remove(id)
    }

    @Override
    SiteDTO get(Long id) {
        entityApiService.get(id)
    }

    @Override
    void create(SiteDTO site) {
        entityApiService.create(site)
    }

    @Override
    void update(Long id, SiteDTO site) {
        entityApiService.update(id, site)
    }
}
