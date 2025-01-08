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
import ish.oncourse.server.api.service.AssessmentClassApiService
import ish.oncourse.server.api.v1.model.AssessmentClassDTO
import ish.oncourse.server.api.v1.service.AssessmentClassApi

class AssessmentClassApiImpl implements AssessmentClassApi {

    @Inject
    private AssessmentClassApiService apiService

    @Override
    void create(AssessmentClassDTO assessmentClass) {
        apiService.create(assessmentClass)
    }

    @Override
    List<AssessmentClassDTO> getClassAssessments(Long classId) {
        return apiService.getList(classId)
    }

    @Override
    void remove(Long id) {
        apiService.remove(id)
    }

    @Override
    void update(Long id, AssessmentClassDTO assessmentClass) {
        apiService.update(id, assessmentClass)
    }
}
