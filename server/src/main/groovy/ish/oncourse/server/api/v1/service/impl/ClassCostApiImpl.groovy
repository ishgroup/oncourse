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

import ish.oncourse.server.api.service.ClassCostApiService
import ish.oncourse.server.api.v1.model.ClassCostDTO
import ish.oncourse.server.api.v1.service.ClassCostApi
import ish.oncourse.server.cayenne.ClassCost
import org.apache.cayenne.validation.ValidationException

import javax.inject.Inject

import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly

class ClassCostApiImpl implements ClassCostApi {

    @Inject
    private ClassCostApiService apiService

    private static final List<String> IGNORE = ["\"$ClassCost.COURSE_CLASS.name\"  is required.".toString(), "\"$ClassCost.TUTOR_ROLE.name\"  is required.".toString()]

    @Override
    void create(ClassCostDTO classCost) {
        try {
            apiService.create(classCost)
        } catch (ValidationException e) {
           if (validateOnly.get() && classCost.id == null && e.validationResult?.failures?.find {it.description in IGNORE}) {
               //Ignore class/tutorRole FK for validation of new record
           } else {
               throw new RuntimeException("Can not save budget: ${classCost.toString()}", e)
           }
        }
    }

    @Override
    void delete(Long id) {
        apiService.remove(id)
    }

    @Override
    List<ClassCostDTO> get(Long classId) {
        apiService.getList(classId)
    }

    @Override
    void update(Long id, ClassCostDTO classCost) {
        apiService.update(id, classCost)
    }
}
