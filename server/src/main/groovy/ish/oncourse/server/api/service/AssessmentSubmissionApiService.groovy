/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.AssessmentSubmissionDao
import ish.oncourse.server.api.v1.model.AssessmentSubmissionDTO
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.document.DocumentService
import ish.util.DateFormatter
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument

class AssessmentSubmissionApiService extends EntityApiService<AssessmentSubmissionDTO, AssessmentSubmission, AssessmentSubmissionDao> {

    @Inject
    private DocumentService documentService

    @Override
    Class<AssessmentSubmission> getPersistentClass() {
        return AssessmentSubmission
    }

    @Override
    AssessmentSubmissionDTO toRestModel(AssessmentSubmission cayenneModel) {
        new AssessmentSubmissionDTO().with { dtoModel ->
            dtoModel.id = cayenneModel.id
            dtoModel.createdOn = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            dtoModel.modifiedOn = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            dtoModel.submittedOn = LocalDateUtils.dateToTimeValue(cayenneModel.submittedOn)
            dtoModel.markedOn = LocalDateUtils.dateToTimeValue(cayenneModel.markedOn)
            dtoModel.studentName = cayenneModel.studentName
            dtoModel.courseClass = cayenneModel.courseClassName
            dtoModel.assessment = cayenneModel.assessmentName
            dtoModel.documents = cayenneModel.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            dtoModel
        }
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

    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        switch (key) {
            case AssessmentSubmission.SUBMITTED_ON.name:
                action = { AssessmentSubmission submission ->
                    submission.submittedOn = DateFormatter.parseDate(value, null)
                }
                break
            case AssessmentSubmission.MARKED_ON.name:
                action = { AssessmentSubmission submission ->
                    submission.markedOn = DateFormatter.parseDate(value, null)
                }
                break
            default:
                validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
