/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.api.service.GradingApiService
import ish.oncourse.server.api.v1.model.GradingTypeDTO
import ish.oncourse.server.api.v1.service.GradingApi

import static ish.oncourse.server.api.service.GradingApiService.validateDuplicates

class GradingApiImpl implements GradingApi {

    @Inject
    private GradingApiService gradingApiService

    @Override
    List<GradingTypeDTO> get() {
        return gradingApiService.getGradingTypes()
                .collect { gradingApiService.toRestModel(it) }
    }

    @Override
    void remove(String id) {
        gradingApiService.remove(Long.valueOf(id))
    }

    @Override
    void update(List<GradingTypeDTO> gradingTypes) {
        validateDuplicates(gradingTypes)
        gradingTypes.each { it ->
            if (!it.id) {
                gradingApiService.create(it)
            } else {
                gradingApiService.update(it.id, it)
            }
        }
    }
}
