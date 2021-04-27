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

import com.google.inject.Inject
import ish.oncourse.server.api.dao.AssessmentClassDao
import ish.oncourse.server.api.v1.model.AssessmentClassDTO
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.server.cayenne.CourseClass
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.v1.function.AssessmentSubmissionFunctions.updateSubmissions

class AssessmentClassApiService extends EntityApiService<AssessmentClassDTO, AssessmentClass, AssessmentClassDao> {

    @Inject
    private AssessmentApiService assessmentService

    @Inject
    private AssessmentSubmissionApiService submissionApiService

    @Inject
    private CourseClassApiService classService

    @Inject
    private ContactApiService contactService

    @Inject
    private EnrolmentApiService enrolmentApiService

    @Override
    Class<AssessmentClass> getPersistentClass() {
        return AssessmentClass
    }

    @Override
    AssessmentClassDTO toRestModel(AssessmentClass cayenneModel) {
        AssessmentClassDTO dto = new AssessmentClassDTO()
        dto.id = cayenneModel.id
        dto.assessmentId = cayenneModel.assessment.id
        dto.assessmentCode = cayenneModel.assessment.code
        dto.assessmentName = cayenneModel.assessment.name
        dto.gradingTypeId = cayenneModel.assessment.gradingType?.id
        dto.courseClassId = cayenneModel.courseClass.id
        dto.contactIds = cayenneModel.assessmentClassTutors*.tutor*.contact*.id.flatten() as List<Long>
        dto.releaseDate = LocalDateUtils.dateToTimeValue(cayenneModel.releaseDate)
        dto.dueDate = LocalDateUtils.dateToTimeValue(cayenneModel.dueDate)
        dto.submissions = cayenneModel.assessmentSubmissions.collect { submissionApiService.toRestMinimizedModel(it) }
        return dto
    }

    @Override
    AssessmentClass toCayenneModel(AssessmentClassDTO dto, AssessmentClass cayenneModel) {
        ObjectContext context = cayenneModel.context
        cayenneModel.assessment = assessmentService.getEntityAndValidateExistence(context, dto.assessmentId)
        cayenneModel.courseClass = classService.getEntityAndValidateExistence(context, dto.courseClassId)

        context.deleteObjects(cayenneModel.assessmentClassTutors.findAll {!(it.tutor.contact.id in dto.contactIds)})

        dto.contactIds.findAll { !(it in cayenneModel.assessmentClassTutors*.tutor.contact.id) }
                .each { contactId ->
            AssessmentClassTutor assessmentTutor = context.newObject(AssessmentClassTutor)
            assessmentTutor.assessmentClass = cayenneModel
            assessmentTutor.tutor = contactService.getEntityAndValidateExistence(context, contactId).tutor
        }

        cayenneModel.dueDate = LocalDateUtils.timeValueToDate(dto.dueDate)
        if (dto.releaseDate) {
            cayenneModel.releaseDate = LocalDateUtils.timeValueToDate(dto.releaseDate)
        } else {
            cayenneModel.releaseDate = null
        }

        updateSubmissions(submissionApiService, enrolmentApiService, dto.submissions, cayenneModel.assessmentSubmissions, context)

        return cayenneModel
    }

    @Override
    void validateModelBeforeSave(AssessmentClassDTO dto, ObjectContext context, Long id) {

        if (dto.assessmentId == null) {
            validator.throwClientErrorException(id, 'assessmentCode', 'Assessment is required')
        }

        if (dto.courseClassId == null) {
            validator.throwClientErrorException(id, 'courseClassId', 'Class is required')
        }

        Assessment assessment = assessmentService.getEntityAndValidateExistence(context, dto.assessmentId)
        CourseClass courseClass = classService.getEntityAndValidateExistence(context, dto.courseClassId)

        if (entityDao.hasDuplicates(id, courseClass, assessment)) {
            validator.throwClientErrorException(id, 'assessmentCode', "$assessment.name already added to class, please remove existing assessment task first")
        }

        if (id == null && !assessment.active) {
            validator.throwClientErrorException(id, 'assessmentCode', "$assessment.name assessment task isn't active")
        }

        List<Long> contactIds = courseClass.tutorRoles*.tutor.contact.id

        Long contactId = dto.contactIds.find { !(it in contactIds) }
        if (contactId != null) {
            validator.throwClientErrorException(id, 'contactIds', "contact with id: $contactId has no tutor role for class: $courseClass.code")
        }

        if (dto.dueDate == null) {
            validator.throwClientErrorException(id, 'dueDate', "Due date is required")
        } else if (dto.releaseDate != null && dto.releaseDate > dto.dueDate) {
            validator.throwClientErrorException(id, 'dueDate', "Due date should be after release date")
        }

    }

    void remove (AssessmentClass assessmentClass, ObjectContext context) {
        context.deleteObjects(assessmentClass.assessmentClassTutors)
        context.deleteObjects(assessmentClass.assessmentClassModules)
        context.deleteObject(assessmentClass)

    }

    @Override
    void validateModelBeforeRemove(AssessmentClass cayenneModel) {
        if (!cayenneModel.assessmentSubmissions.empty){
            validator.throwClientErrorException(cayenneModel.id, null, "Assessment has student submittions")
        }
    }

    @Override
    List<AssessmentClassDTO> getList(Long classId) {
        entityDao.getByClassId(cayenneService.newContext, classId).collect {toRestModel(it)}
    }
}
