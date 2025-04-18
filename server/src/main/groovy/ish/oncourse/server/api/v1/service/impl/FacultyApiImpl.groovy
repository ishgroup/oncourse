/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.api.service.FacultyApiService
import ish.oncourse.server.api.v1.model.FacultyDTO
import ish.oncourse.server.api.v1.service.FacultyApi

class FacultyApiImpl implements FacultyApi {

    @Inject
    private FacultyApiService facultyApiService

    @Override
    Long create(FacultyDTO facultyDTO) {
        facultyApiService.create(facultyDTO).id
    }

    @Override
    FacultyDTO get(Long id) {
        facultyApiService.get(id)
    }

    @Override
    void remove(Long id) {
        facultyApiService.remove(id)
    }

    @Override
    void update(Long id, FacultyDTO facultyDTO) {
        facultyApiService.update(id, facultyDTO)
    }
}
