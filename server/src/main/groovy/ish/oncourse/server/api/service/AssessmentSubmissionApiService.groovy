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
import ish.oncourse.server.cayenne.AssessmentSubmissionAttachmentRelation
import ish.oncourse.server.document.DocumentService
import ish.util.DateFormatter
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments

class AssessmentSubmissionApiService extends EntityApiService<AssessmentSubmissionDTO, AssessmentSubmission, AssessmentSubmissionDao> {

    @Inject
    private ContactApiService contactService

    @Inject
    private DocumentService documentService

    @Override
    Class<AssessmentSubmission> getPersistentClass() {
        return AssessmentSubmission
    }

    @Override
    AssessmentSubmissionDTO toRestModel(AssessmentSubmission cayenneModel) {
        toRestMinimizedModel(cayenneModel).with { dtoModel ->
            dtoModel.studentId = cayenneModel.enrolment.student.contact.id
            dtoModel.studentName = cayenneModel.studentName
            dtoModel.classId = cayenneModel.assessmentClass.courseClass.id
            dtoModel.courseClassName = cayenneModel.courseClassName
            dtoModel.assessment = cayenneModel.assessmentName
            dtoModel.documents = cayenneModel.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            dtoModel
        }
    }

    AssessmentSubmissionDTO toRestMinimizedModel(AssessmentSubmission cayenneModel) {
        new AssessmentSubmissionDTO().with { dtoModel ->
            dtoModel.id = cayenneModel.id
            dtoModel.enrolmentId = cayenneModel.enrolment.id
            dtoModel.assessmentId = cayenneModel.assessmentClass.assessment.id
            dtoModel.submittedById = cayenneModel.submittedBy.id
            dtoModel.tutorName = cayenneModel.submittedBy.fullName
            dtoModel.submittedOn = LocalDateUtils.dateToTimeValue(cayenneModel.submittedOn)
            dtoModel.markedOn = LocalDateUtils.dateToTimeValue(cayenneModel.markedOn)
            dtoModel.createdOn = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            dtoModel.modifiedOn = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            dtoModel
        }
    }

    @Override
    AssessmentSubmission toCayenneModel(AssessmentSubmissionDTO dto, AssessmentSubmission cayenneModel) {
        cayenneModel.submittedOn = LocalDateUtils.timeValueToDate(dto.submittedOn)
        cayenneModel.markedOn = LocalDateUtils.timeValueToDate(dto.markedOn)
        if (dto.submittedById) {
            cayenneModel.submittedBy = contactService.getEntityAndValidateExistence(cayenneModel.context, dto.submittedById)
        }

        updateDocuments(cayenneModel, cayenneModel.attachmentRelations, dto.documents, AssessmentSubmissionAttachmentRelation, cayenneModel.context)

        return cayenneModel
    }

    @Override
    void validateModelBeforeSave(AssessmentSubmissionDTO dto, ObjectContext context, Long id) {
        if (dto.submittedOn == null) {
            validator.throwClientErrorException(id, 'submittedOn', "Submitted on date couldn't be null")
        }
    }

    @Override
    void validateModelBeforeRemove(AssessmentSubmission cayenneModel) {
        // N/A
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
