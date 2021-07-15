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

package ish.oncourse.server.api.service

import ish.oncourse.server.api.dao.SurveyDao
import ish.oncourse.server.api.v1.function.StudentFeedbackFunctions
import ish.oncourse.server.api.v1.model.SurveyItemDTO
import ish.oncourse.server.api.v1.model.SurveyVisibilityDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Survey
import ish.oncourse.server.cayenne.SurveyCustomField
import org.apache.cayenne.ObjectContext

import java.util.stream.Collectors

import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.validateCustomFields
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

class SurveyApiService extends EntityApiService<SurveyItemDTO, Survey, SurveyDao> {

    @Override
    Class<Survey> getPersistentClass() {
        return Survey
    }

    @Override
    SurveyItemDTO toRestModel(Survey survey) {
        new SurveyItemDTO().with { feedbackDTO ->
            feedbackDTO.id = survey.id
            feedbackDTO.studentContactId = survey.enrolment.student.contact.id
            feedbackDTO.studentName = survey.enrolment.student.contact.with {it.getFullName() }
            feedbackDTO.netPromoterScore = survey.netPromoterScore
            feedbackDTO.venueScore = survey.venueScore
            feedbackDTO.courseScore = survey.courseScore
            feedbackDTO.tutorScore = survey.tutorScore
            feedbackDTO.visibility = StudentFeedbackFunctions.VISIBILITY_MAP[survey.visibility]
            feedbackDTO.testimonial = survey.testimonial
            feedbackDTO.comment = survey.comment
            feedbackDTO.customFields = survey.customFields.collectEntries { [(it.customFieldType.key): it.value] }
            feedbackDTO.siteId = survey.enrolment.courseClass.room?.site?.id
            feedbackDTO.siteName = survey.enrolment.courseClass.room?.site?.name
            feedbackDTO.roomId = survey.enrolment.courseClass.room?.id
            feedbackDTO.roomName = survey.enrolment.courseClass.room?.name
            feedbackDTO.classId = survey.enrolment.courseClass.id
            feedbackDTO.className = survey.enrolment.courseClass.with { "$it.uniqueCode $it.course.name" }
            Set<Contact> set = survey.enrolment.courseClass.tutorRoles*.tutor*.contact.stream().collect(Collectors.toSet())
            feedbackDTO.tutors = set.collectEntries {[ (it.id.toString()) :  it.getFullName() ]}
            feedbackDTO
        }
    }

    @Override
    Survey toCayenneModel(SurveyItemDTO restModel, Survey cayenneModel) {
        cayenneModel.visibility = StudentFeedbackFunctions.VISIBILITY_MAP.getByValue(restModel.visibility)
        cayenneModel.testimonial = trimToNull(restModel.testimonial)
        cayenneModel.netPromoterScore = restModel.netPromoterScore
        cayenneModel.courseScore = restModel.courseScore
        cayenneModel.venueScore = restModel.venueScore
        cayenneModel.tutorScore = restModel.tutorScore
        updateCustomFields(cayenneModel.context, cayenneModel, restModel.customFields, SurveyCustomField)
        cayenneModel
    }

    boolean validateScore(Integer score, Integer max, Boolean wasNull) {
        if ((score == null || score == 0) && wasNull) {
            return true
        }
        if (score > 0 && score <= max) {
            return true
        }
        return false
    }

    @Override
    void validateModelBeforeSave(SurveyItemDTO studentFeedbackDTO, ObjectContext context, Long id) {
        if (!id) {
            validator.throwClientErrorException('id', 'Cannot create student feedback.')
        } else {
            Survey survey = entityDao.getById(context, id)
            if (!survey) {
                validator.throwClientErrorException(id, 'id', "Student feedback with id = '$id' doesn't exist")
            }

            if (!studentFeedbackDTO.visibility) {
                validator.throwClientErrorException(id, 'visibility', 'Visibility is required.')
            }

            if (studentFeedbackDTO.visibility == SurveyVisibilityDTO.PUBLIC_TESTIMONIAL && isBlank(studentFeedbackDTO.testimonial)) {
                validator.throwClientErrorException(id, 'testimonial', 'Published testimonial cannot be blank.')
            }

            if(!validateScore(studentFeedbackDTO.netPromoterScore, 10, (survey.netPromoterScore == null || survey.netPromoterScore == 0))) {
                validator.throwClientErrorException(id, 'netPromoterScore', 'Not allowed value for score.')
            }

            if(!validateScore(studentFeedbackDTO.tutorScore, 5, (survey.tutorScore == null || survey.tutorScore == 0))) {
                validator.throwClientErrorException(id, 'tutorScore', 'Not allowed value for score.')
            }

            if(!validateScore(studentFeedbackDTO.venueScore, 5, (survey.venueScore == null || survey.venueScore == 0))) {
                validator.throwClientErrorException(id, 'venueScore', 'Not allowed value for score.')
            }

            if(!validateScore(studentFeedbackDTO.courseScore, 5, (survey.courseScore == null || survey.courseScore == 0))) {
                validator.throwClientErrorException(id, 'courseScore', 'Not allowed value for score.')
            }

        }

        validateCustomFields(context, Survey.class.simpleName, studentFeedbackDTO.customFields, id as String, validator)
    }

    @Override
    void validateModelBeforeRemove(Survey survey) {
        validator.throwClientErrorException(survey.id, 'id', 'Student feedback cannot be deleted.')
    }
}
