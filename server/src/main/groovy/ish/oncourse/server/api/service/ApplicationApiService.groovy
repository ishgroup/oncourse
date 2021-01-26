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
import groovy.transform.CompileStatic
import ish.common.types.ApplicationStatus
import ish.common.types.ConfirmationStatus
import ish.common.types.CourseEnrolmentType
import ish.common.types.PaymentSource
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.api.dao.ApplicationDao
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.document.DocumentService
import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ish.oncourse.server.api.v1.function.ApplicationFunctions.APPLICATION_SOURCE_MAP
import static ish.oncourse.server.api.v1.function.ApplicationFunctions.APPLICATION_STATUS_MAP
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.validateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static ish.oncourse.server.api.v1.function.TagFunctions.validateRelationsForSave
import ish.oncourse.server.api.v1.model.ApplicationDTO
import ish.oncourse.server.api.v1.model.ApplicationStatusDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.ApplicationAttachmentRelation
import ish.oncourse.server.cayenne.ApplicationCustomField
import ish.oncourse.server.cayenne.ApplicationTagRelation
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

@CompileStatic
class ApplicationApiService extends TaggableApiService<ApplicationDTO, Application, ApplicationDao> {

    @Inject
    private SystemUserService systemUserService

    @Inject
    private DocumentService documentService

    @Inject
    private ContactDao contactDao

    @Inject
    private CourseDao courseDao

    @Override
    Class<Application> getPersistentClass() {
        return Application
    }

    @Override
    ApplicationDTO toRestModel(Application application) {
        new ApplicationDTO().with { applicationDTO ->
            applicationDTO.id = application.id
            applicationDTO.contactId = application.student.contact.id
            applicationDTO.studentName = application.student.contact.with { GetContactFullName.valueOf(it, true).get() }
            applicationDTO.courseId = application.course.id
            applicationDTO.courseName = application.course.with { "$it.name $it.code" }
            applicationDTO.applicationDate = application.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()
            applicationDTO.status = APPLICATION_STATUS_MAP[(ApplicationStatus) application.getValueForKey("status")]
            applicationDTO.source = APPLICATION_SOURCE_MAP[application.source]
            applicationDTO.feeOverride = application.feeOverride?.toBigDecimal()
            applicationDTO.enrolBy = application.enrolBy?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()
            applicationDTO.createdBy = application.createdByUser ? "$application.createdByUser.firstName $application.createdByUser.lastName" : null
            applicationDTO.reason = application.reason
            applicationDTO.tags =  application.tags.collect { toRestTagMinimized(it) }
            applicationDTO.documents = application.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            applicationDTO.customFields = application.customFields.collectEntries { [(it.customFieldType.key) : it.value] }
            applicationDTO.createdOn = application.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            applicationDTO.modifiedOn = application.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            applicationDTO
        }
    }

    @Override
    Application toCayenneModel(ApplicationDTO applicationDTO, Application application) {
        SystemUser currentUser = application.context.localObject(systemUserService.currentUser)


        application.status = APPLICATION_STATUS_MAP.getByValue(applicationDTO.status)
        application.feeOverride = toMoneyValue(applicationDTO.feeOverride)
        application.enrolBy = applicationDTO.enrolBy?.atStartOfDay(ZoneOffset.UTC)?.toDate()
        application.reason = trimToNull(applicationDTO.reason)
        if (application.newRecord) {
            application.confirmationStatus = ConfirmationStatus.NOT_SENT
            application.course = courseDao.getById(application.context, applicationDTO.courseId)
            application.source = PaymentSource.SOURCE_ONCOURSE
            application.createdByUser = currentUser
            application.student = contactDao.getById(application.context, applicationDTO.contactId).student
        }

        updateTags(application, application.taggingRelations, applicationDTO.tags*.id, ApplicationTagRelation, application.context)
        updateDocuments(application, application.attachmentRelations, applicationDTO.documents, ApplicationAttachmentRelation, application.context)
        updateCustomFields(application.context, application, applicationDTO.customFields, ApplicationCustomField)
        application
    }

    @Override
    void validateModelBeforeSave(ApplicationDTO applicationDTO, ObjectContext context, Long id) {

        if (!applicationDTO.contactId) {
            validator.throwClientErrorException(id, 'studentContact', 'Student is required.')
        } else if (!contactDao.getById(context, applicationDTO.contactId)?.student) {
            validator.throwClientErrorException(id, 'studentContact', 'Contact is not a student.')
        }

        if (!applicationDTO.courseId) {
            validator.throwClientErrorException(id, 'course', 'Course is required.')
        } else {
            Course course = courseDao.getById(context, applicationDTO.courseId)
            if (!course){
                validator.throwClientErrorException(id, 'course', "Course with id=$applicationDTO.courseId doesn't exist.")
            } else if (course.enrolmentType != CourseEnrolmentType.ENROLMENT_BY_APPLICATION) {
                validator.throwClientErrorException(id, 'course', 'Only courses with enrolment type by application can be used for application.')
            }
        }

        if (id) {
            Application application = entityDao.getById(context, id)
            if (!application) {
                validator.throwClientErrorException(id, 'id', "Application with id=$id doesn't exist.")
            }

            if (application.student.contact.id != applicationDTO.contactId) {
                validator.throwClientErrorException(id, 'studentContact', "Cannot change student for application.")
            }

            if (application.status == ApplicationStatus.ACCEPTED && applicationDTO.status != ApplicationStatusDTO.ACCEPTED) {
                validator.throwClientErrorException(id, 'status', "Cannot change status for accepted applications.")
            }
        }

        ValidationErrorDTO error = validateRelationsForSave(Application,
                context, applicationDTO.tags*.id, TaggableClasses.APPLICATION)

        if (error) {
            validator.throwClientErrorException(error)
        }

        validateCustomFields(context, Application.class.simpleName, applicationDTO.customFields, id as String, validator)
    }

    @Override
    void validateModelBeforeRemove(Application application) {
        if (!application.attachmentRelations.empty) {
            validator.throwClientErrorException(application.id, 'documents', "Cannot delete application with attached documents. Check removed documents.")
        }
        if (ApplicationStatus.NEW != application.status) {
            validator.throwClientErrorException(application.id, 'status', "Cannot delete application with not NEW status")
        }
    }

    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
