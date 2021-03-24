/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.service.AssessmentSubmissionApiService
import ish.oncourse.server.api.service.EnrolmentApiService
import ish.oncourse.server.api.v1.model.AssessmentSubmissionDTO
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.Enrolment
import org.apache.cayenne.ObjectContext

class AssessmentSubmissionFunctions {

    static void updateSubmissions(AssessmentSubmissionApiService submissionApiService,
                                  EnrolmentApiService enrolmentApiService,
                                  List<AssessmentSubmissionDTO> dtoModels,
                                  List<AssessmentSubmission> cayenneModels,
                                  ObjectContext context) {

        context.deleteObjects(cayenneModels.findAll { !(it.id in dtoModels*.id) })

        dtoModels.each { submissionDto ->
            if (submissionDto.id) {
                submissionApiService.update(submissionDto.id, submissionDto)
            } else {
                submissionApiService.validateModelBeforeSave(submissionDto, context, null)
                AssessmentSubmission submission = context.newObject(AssessmentSubmission)
                Enrolment enrolment = enrolmentApiService.getEntityAndValidateExistence(context, submissionDto.enrolmentId)
                submission.enrolment = enrolment
                submission.assessmentClass = enrolment.courseClass.assessmentClasses.find { submissionDto.assessmentId == it.assessment.id }
                submissionApiService.toCayenneModel(submissionDto, submission)
            }
        }
    }
}
