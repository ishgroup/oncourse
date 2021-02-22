/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import ish.oncourse.server.api.dao.AssessmentSubmissionDao
import ish.oncourse.server.api.v1.model.AssessmentSubmissionDTO
import ish.oncourse.server.cayenne.AssessmentSubmission
import org.apache.cayenne.ObjectContext

class AssessmentSubmissionApiService extends EntityApiService<AssessmentSubmissionDTO, AssessmentSubmission, AssessmentSubmissionDao> {

    @Override
    Class<AssessmentSubmission> getPersistentClass() {
        return null
    }

    @Override
    AssessmentSubmissionDTO toRestModel(AssessmentSubmission cayenneModel) {
        return null
    }

    @Override
    AssessmentSubmission toCayenneModel(AssessmentSubmissionDTO dto, AssessmentSubmission cayenneModel) {
        return null
    }

    @Override
    void validateModelBeforeSave(AssessmentSubmissionDTO dto, ObjectContext context, Long id) {

    }

    @Override
    void validateModelBeforeRemove(AssessmentSubmission cayenneModel) {

    }
}
