/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1

import com.google.inject.Inject
import ish.oncourse.server.api.service.AssessmentSubmissionApiService
import ish.oncourse.server.api.v1.model.AssessmentSubmissionDTO
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.service.AssessmentSubmissionApi

class AssessmentSubmissionApiImpl implements AssessmentSubmissionApi {

    @Inject
    private AssessmentSubmissionApiService service

    @Override
    AssessmentSubmissionDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, AssessmentSubmissionDTO assessmentSubmission) {
        service.update(id, assessmentSubmission)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void bulkChange(DiffDTO diff) {
        service.bulkChange(diff)
    }
}
