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
import ish.oncourse.server.api.service.CourseClassTutorApiService
import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly
import ish.oncourse.server.api.v1.model.CourseClassTutorDTO
import ish.oncourse.server.api.v1.service.CourseClassTutorApi
import ish.oncourse.server.cayenne.CourseClassTutor
import org.apache.cayenne.validation.ValidationException

class CourseClassTutorApiImpl implements CourseClassTutorApi {

    @Inject
    private CourseClassTutorApiService apiService

    private static final List<String> IGNORE = ["\"$CourseClassTutor.COURSE_CLASS.name\"  is required.".toString()]

    @Override
    Long create(CourseClassTutorDTO tutor) {
        try {
            apiService.create(tutor).id
        } catch (ValidationException e) {
            if (validateOnly.get() && tutor.id == null && e.validationResult?.failures?.find {it.description in IGNORE}) {
                //Ignore class/tutorRole FK for validation of new record
                return null
            } else {
                throw new RuntimeException("Can not save budget: ${tutor.toString()}", e)
            }
        }
    }

    @Override
    void delete(Long id) {
        apiService.remove(id)
    }

    @Override
    List<CourseClassTutorDTO> get(Long classId) {
        apiService.getList(classId)
    }

    @Override
    void update(Long id, CourseClassTutorDTO tutor) {
        apiService.update(id, tutor)
    }
}
