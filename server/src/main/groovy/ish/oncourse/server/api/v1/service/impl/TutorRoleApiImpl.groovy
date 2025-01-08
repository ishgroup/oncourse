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
import ish.oncourse.server.api.service.TutorRoleApiService
import ish.oncourse.server.api.v1.model.DefinedTutorRoleDTO
import ish.oncourse.server.api.v1.service.TutorRoleApi

class TutorRoleApiImpl implements TutorRoleApi {

    @Inject
    private TutorRoleApiService service

    @Override
    void create(DefinedTutorRoleDTO tutorRole) {
        service.create(tutorRole)
    }

    @Override
    DefinedTutorRoleDTO get(Long id) {
        service.get(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, DefinedTutorRoleDTO tutorRole) {
        service.update(id, tutorRole)
    }
}
