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
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.duplicate.CourseDuplicationRequest
import ish.messaging.ICourse
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.CourseModuleDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.FieldConfigurationSchemeDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.dao.QualificationDao
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.v1.function.CourseFunctions.ENROLMENT_TYPE_MAP
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestFromEntityRelation
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestToEntityRelation
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.validateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.updateAvailabilityRules
import static ish.oncourse.server.api.v1.function.HolidayFunctions.validateFoUpdate
import static ish.oncourse.server.api.v1.function.ModuleFunctions.bidiModuleType
import ish.oncourse.server.api.v1.function.TagFunctions
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.CourseDTO
import ish.oncourse.server.api.v1.model.CourseEnrolmentTypeDTO
import ish.oncourse.server.api.v1.model.CourseStatusDTO
import static ish.oncourse.server.api.v1.model.CourseStatusDTO.COURSE_DISABLED
import static ish.oncourse.server.api.v1.model.CourseStatusDTO.ENABLED
import static ish.oncourse.server.api.v1.model.CourseStatusDTO.ENABLED_AND_VISIBLE_ONLINE
import ish.oncourse.server.api.v1.model.HolidayDTO
import ish.oncourse.server.api.v1.model.ModuleDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseAttachmentRelation
import ish.oncourse.server.cayenne.CourseCustomField
import ish.oncourse.server.cayenne.CourseTagRelation
import ish.oncourse.server.cayenne.CourseUnavailableRuleRelation
import ish.oncourse.server.duplicate.DuplicateCourseService
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class CourseApiService extends TaggableApiService<CourseDTO, Course, CourseDao>  {

    @Inject
    private DocumentService documentService

    @Inject
    private SystemUserService systemUserService

    @Inject
    private FieldConfigurationSchemeDao fieldConfigurationSchemeDao

    @Inject
    private QualificationDao qualificationDao

    @Inject
    private CourseClassDao courseClassDao

    @Inject
    private CourseModuleDao courseModuleDao

    @Inject
    private ModuleDao moduleDao

    @Inject
    EntityRelationDao entityRelationDao

    @Inject
    private ProductDao productDao

    @Inject
    private DuplicateCourseService duplicateCourseService

    @Inject
    private IPermissionService permissionService

    @Override
    Class<Course> getPersistentClass() {
        return Course
    }

    @Override
    CourseDTO toRestModel(Course course) {
        new CourseDTO().with { courseDTO ->
            courseDTO.id = course.id
            courseDTO.createdOn = LocalDateUtils.dateToTimeValue(course.createdOn)
            courseDTO.modifiedOn = LocalDateUtils.dateToTimeValue(course.modifiedOn)
            courseDTO.name = course.name
            courseDTO.code = course.code
            courseDTO.tags = course.tags.collect { toRestTagMinimized(it) }
            courseDTO.enrolmentType = ENROLMENT_TYPE_MAP[course.enrolmentType]
            courseDTO.allowWaitingLists = course.allowWaitingLists
            courseDTO.dataCollectionRuleId = course.fieldConfigurationSchema.id
            courseDTO.dataCollectionRuleName = course.fieldConfigurationSchema.name
            courseDTO.currentlyOffered = course.currentlyOffered
            courseDTO.status = course.status
            courseDTO.brochureDescription = course.printedBrochureDescription
            courseDTO.currentClassesCount = course.currentClassesCount
            courseDTO.futureClasseCount = course.futureClasseCount
            courseDTO.unscheduledClasseCount = course.unscheduledClassesCount
            courseDTO.passedClasseCount = course.passedClassesCount
            courseDTO.selfPacedclassesCount = course.selfPacedClassesCount
            courseDTO.cancelledClassesCount = course.cancelledClassesCount
            courseDTO.studentWaitingListCount = course.waitingLists.size()
            courseDTO.hasEnrolments = course.courseClasses.find { c -> !c.enrolments.empty} != null
            courseDTO.webDescription = course.webDescription
            courseDTO.documents = course.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            courseDTO.relatedSellables = (EntityRelationDao.getRelatedFrom(course.context, Course.simpleName, course.id).collect { it -> toRestFromEntityRelation(it) } +
                    EntityRelationDao.getRelatedTo(course.context, Course.simpleName, course.id).collect { it -> toRestToEntityRelation(it) })
            courseDTO.qualificationId = course.qualification?.id
            courseDTO.qualNationalCode = course.qualification?.nationalCode
            courseDTO.qualTitle = course.qualification?.title
            courseDTO.qualLevel = course.qualification?.level
            courseDTO.isSufficientForQualification = course.isSufficientForQualification
            courseDTO.isVET = course.isVET
            courseDTO.isTraineeship = course.isTraineeship
            courseDTO.fieldOfEducation = course.fieldOfEducation
            courseDTO.reportableHours = course.reportableHours
            courseDTO.modules = course.modules.collect { module ->
                new ModuleDTO().with { moduleDTO ->
                    moduleDTO.id = module.id
                    moduleDTO.nationalCode = module.nationalCode
                    moduleDTO.title = module.title
                    moduleDTO.type = bidiModuleType[module.type]
                    moduleDTO.isOffered = module.isOffered
                    moduleDTO.nominalHours = module.nominalHours
                    moduleDTO
                }
            }
            courseDTO.customFields = course.customFields.collectEntries { [(it.customFieldType.key) : it.value] }
            courseDTO.rules = course.unavailableRuleRelations.collect{ toRestHoliday(it.rule) }
            courseDTO.feeHelpClass = course.feeHelpClass
            courseDTO.fullTimeLoad = course.fullTimeLoad
            courseDTO
        }
    }

    @Override
    Course toCayenneModel(CourseDTO courseDTO, Course course) {
        course.name = trimToNull(courseDTO.name)
        course.code = trimToNull(courseDTO.code)
        course.enrolmentType = ENROLMENT_TYPE_MAP.getByValue(courseDTO.enrolmentType)
        course.allowWaitingLists = courseDTO.allowWaitingLists
        course.fieldConfigurationSchema = fieldConfigurationSchemeDao.getById(course.context, courseDTO.dataCollectionRuleId)
        course.isTraineeship = courseDTO.isTraineeship
        course.fullTimeLoad = courseDTO.fullTimeLoad
        course.feeHelpClass = courseDTO.feeHelpClass
        if (course.isTraineeship) {
            course.currentlyOffered = courseDTO.currentlyOffered
        } else {
           switch (courseDTO.status) {
               case COURSE_DISABLED:
                   course.currentlyOffered = false
                   course.isShownOnWeb = false
                   break
               case ENABLED:
                   course.isShownOnWeb = false
                   course.currentlyOffered = true
                   break
               case ENABLED_AND_VISIBLE_ONLINE:
                   course.isShownOnWeb = true
                   course.currentlyOffered = true
                   break
           }
        }
        course.printedBrochureDescription = trimToNull(courseDTO.brochureDescription)
        course.webDescription = trimToNull(courseDTO.webDescription)
        if (courseDTO.qualificationId != null) {
            course.qualification =  qualificationDao.getById(course.context, courseDTO.qualificationId)
            course.fieldOfEducation = trimToNull(course.qualification.fieldOfEducation)
        } else {
            course.qualification = null
            course.fieldOfEducation = trimToNull(courseDTO.fieldOfEducation)
        }
        course.isSufficientForQualification = courseDTO.isSufficientForQualification
        course.isVET = courseDTO.isVET

        updateTags(course, course.taggingRelations, courseDTO.tags*.id, CourseTagRelation, course.context)
        updateDocuments(course, course.attachmentRelations, courseDTO.documents, CourseAttachmentRelation, course.context)
        updateModules(course, courseDTO.modules)
        course.reportableHours = courseDTO.reportableHours

        updateCustomFields(course.context, course, courseDTO.customFields, CourseCustomField)
        updateAvailabilityRules(course, course.unavailableRuleRelations*.rule, courseDTO.rules, CourseUnavailableRuleRelation)
        course
    }

    private void checkPermission(CourseDTO dto, Course course) {
        if (!permissionService.currentUserCan(KeyCode.VET_COURSE, Mask.VIEW)) {
            if (course != null)  {
                if ( (course.qualification != null && course.qualification.id != dto.qualificationId) ||
                        (course.qualification == null && dto.qualificationId != null) ||
                        (course.isVET != null &&  course.isVET != dto.isVET) ||
                        (course.isSufficientForQualification != null && course.isVET != dto.isVET) ||
                        (course.reportableHours != null  && course.reportableHours != dto.reportableHours)) {
                    validator.throwForbiddenErrorException(course.id, 'vet', "Sorry, you have no permissions to edit VET details. Please contact your administrator.")
                }
            } else if (dto.qualificationId != null ) {
                validator.throwForbiddenErrorException(null, 'vet', "Sorry, you have no permissions to edit VET details. Please contact your administrator.")
            }
        }
    }

    @Override
    void validateModelBeforeSave(CourseDTO courseDTO, ObjectContext context, Long id) {

        if (isBlank(courseDTO.code)) {
            validator.throwClientErrorException(id, 'code', 'Code is required.')
        } else if (!trimToNull(courseDTO.code).matches('^\\w+(\\.\\w+)*$')) {
            validator.throwClientErrorException(id, 'code', 'Course code must start and end with letters or numbers and can contain full stops.')
        } else if (trimToNull(courseDTO.code).length() > 128) {
            validator.throwClientErrorException(id, 'code', 'Course code cannot be greater than 128 characters.')
        } else {
            Long courseId = entityDao.getCourseByCode(context, trimToNull(courseDTO.code))?.id
            if (courseId && courseId != id) {
                validator.throwClientErrorException(id, 'code', 'Code must be unique.')
            }
        }

        if (courseDTO.enrolmentType == null) {
            validator.throwClientErrorException(id, 'enrolmentType', 'Enrolment type is required.')
        }
        if (courseDTO.reportableHours == null) {
            validator.throwClientErrorException(id, 'reportableHours', 'Reportable hours is required.')
        }
        if (courseDTO.feeHelpClass == null) {
            validator.throwClientErrorException(id, 'feeHelpClass', 'Fee help class flag is required')
        }
        if (courseDTO.allowWaitingLists == null) {
            validator.throwClientErrorException(id, 'allowWaitingLists', 'Allow waiting lists flag is required.')
        }

        if (courseDTO.status == null) {
            validator.throwClientErrorException(id, 'status', 'Status is required.')
        }

        if (isBlank(courseDTO.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        }

        if(courseDTO.name.length() > ICourse.COURSE_NAME_MAX_LENGTH) {
            validator.throwClientErrorException(id, 'name',
                    "Course name cannot be greater than " + ICourse.COURSE_NAME_MAX_LENGTH + " characters.")
        }

        if (courseDTO.isSufficientForQualification == null) {
            validator.throwClientErrorException(id, 'isSufficientForQualification', 'Sufficient for qualification flag is required.')
        }

        if (courseDTO.isVET == null) {
            validator.throwClientErrorException(id, 'isVET', 'VET flag is required.')
        }

        if (courseDTO.qualificationId == null) {
            if (courseDTO.isSufficientForQualification) {
                validator.throwClientErrorException(id, 'isSufficientForQualification', 'Course cannot be sufficient for a qualification if there is no qualification set.')
            }
        } else {
            if (!courseDTO.isVET) {
                validator.throwClientErrorException(id, 'isVET', 'Course must be VET if there is a qualification set.')
            }
        }

        if (isNotBlank(courseDTO.fieldOfEducation) && trimToNull(courseDTO.fieldOfEducation).length() != 6) {
            validator.throwClientErrorException(id, 'fieldOfEducation', 'The field of education must be empty or 6 characters long.')
        }

        if (trimToEmpty(courseDTO.webDescription).length() > 32000) {
            validator.throwClientErrorException(id, 'webDescription', 'Web description must be shorter then 32000 characters.')
        }

        if (trimToEmpty(courseDTO.brochureDescription).length() > 32000) {
            validator.throwClientErrorException(id, 'brochureDescription', 'Brochure description must be shorter then 32000 characters.')
        }

        if(courseDTO.dataCollectionRuleId == null) {
            validator.throwClientErrorException(id, 'dataCollectionRuleId', 'Data collection rule is required.')
        }

        if (courseDTO.dataCollectionRuleId && !fieldConfigurationSchemeDao.getById(context, courseDTO.dataCollectionRuleId)) {
            validator.throwClientErrorException(id, 'dataCollectionRuleId', "Data collection rule with id=$courseDTO.dataCollectionRuleId not found.")
        }

        if (courseDTO.qualificationId && !qualificationDao.getById(context, courseDTO.qualificationId)) {
            validator.throwClientErrorException(id, 'qualificationId', "Qualification with id=$courseDTO.qualificationId not found.")
        }

        if (courseDTO.qualificationId && isNotBlank(courseDTO.fieldOfEducation)) {
            validator.throwClientErrorException(id, 'fieldOfEducation', "Field of education should be empty for course with qualification")
        }

        courseDTO.modules.findAll { it.id != null }.each { module ->
            if (!moduleDao.getById(context, module.id)) {
                validator.throwClientErrorException(id, 'modules', "Module with id=$module.id not found.")
            }
        }

        if (courseDTO.isTraineeship == null) {
            validator.throwClientErrorException(id, 'isTraineeship', 'Course/Traineeship flag is required.')
        } else {
            if (courseDTO.isTraineeship) {
                if (courseDTO.qualificationId == null) {
                    validator.throwClientErrorException(id, 'qualificationId', 'Traineeship requires qualification to be set.')
                }
                if (courseDTO.currentlyOffered == null) {
                    validator.throwClientErrorException(id, 'currentlyOffered', 'Currently offered flag required for traineeship.')
                }

                if (courseDTO.reportableHours == null) {
                    validator.throwClientErrorException(id, 'reportableours', 'Traineeship requires reportable hours field to be set.')
                }
            }
        }

        courseDTO.rules.each {
            ValidationErrorDTO error = validateFoUpdate(context, it as HolidayDTO, false)
            if (error != null) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
        }

        TagFunctions.validateTagForSave(Course, context, courseDTO.tags*.id)
                ?.with { validator.throwClientErrorException(it) }

        TagFunctions.validateRelationsForSave(Course, context, courseDTO.tags*.id, TaggableClasses.COURSE)
                ?.with { validator.throwClientErrorException(it) }

        validateCustomFields(context, Course.class.simpleName, courseDTO.customFields, id as String, validator)

        if (id != null) {
            Course course = entityDao.getById(context, id)
            checkPermission(courseDTO, course)
            if (!course) {
                validator.throwClientErrorException(id, 'id', "Course with id=$id  does not exist.")
            }

            if (course.courseClasses.find { c -> !c.enrolments.empty} && course.modules?.id?.sort() != courseDTO.modules?.id?.sort()) {
                validator.throwClientErrorException(id, 'modules', "There are enrolments in this course. Modifying the modules is not allowed.")
            }

            if (course.isTraineeship != courseDTO.isTraineeship) {
                validator.throwClientErrorException(id, 'isTraineeship', 'Existed course type can not be changed')
            }
        } else {
            checkPermission(courseDTO, null)
        }
    }

    @Override
    void validateModelBeforeRemove(Course course) {
        if (!course.courseClasses.empty) {
            if (!course.isTraineeship) {
                validator.throwClientErrorException(course.id, 'courseClasses', 'Course cannot be deleted because it has classes.')
            } else {
                validator.throwClientErrorException(course.id, 'courseClasses', 'Traineeship course cannot be deleted because it has traineeships.')
            }
        }
        if (!course.waitingLists.empty) {
            validator.throwClientErrorException(course.id, 'waitingLists', 'Course cannot be deleted because it has waiting lists.')
        }
        if (!course.applications.empty) {
            validator.throwClientErrorException(course.id, 'applications', 'Course cannot be deleted because it has applications.')
        }
        if (!course.voucherProductCourses.empty) {
            validator.throwClientErrorException(course.id, 'voucherProductCourses', 'Course cannot be deleted because it has relations with voucher products.')
        }
    }


    void updateModules(Course course, List<ModuleDTO> modules) {
        List<Long> relationsToSave = modules*.id ?: [] as List<Long>
        course.context.deleteObjects(course.courseModules.findAll { !relationsToSave.contains(it.module.id) })
        modules.findAll { !course.modules*.id.contains(it.id) }.each { module ->
            courseModuleDao.newObject(course.context).with { courseModule ->
                courseModule.course = course
                courseModule.module = moduleDao.getById(course.context, module.id)
                courseModule
            }
        }
    }


    void duplicateCourse(List<Long> courseIds) {
        ObjectContext context = cayenneService.newContext

        courseIds.each {
            if (!entityDao.getById(context, it)) {
                validator.throwClientErrorException(it, 'id', "Course with id=$it not found.")
            }
        }

        duplicateCourseService.duplicateCourses(new CourseDuplicationRequest().with { req ->
            req.ids = courseIds
            req
        })

    }


    Closure getAction(String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            switch (key) {
                case Course.IS_SHOWN_ON_WEB.name:
                    validator.validateBoolean(value, key)
                    action = { Course c ->
                        c.isShownOnWeb = Boolean.valueOf(value)
                        if (c.isShownOnWeb) {
                            c.currentlyOffered = true
                        }
                    }
                    break
                case Course.ENROLMENT_TYPE.name:
                    action = { Course c ->
                        c.enrolmentType = ENROLMENT_TYPE_MAP.getByValue(CourseEnrolmentTypeDTO.fromValue(value))
                        if (!c.enrolmentType) {
                            validator.throwClientErrorException(c.id, key, "Unrecognized enrolment type value ${value}")
                        }
                    }
                    break
                case Course.COURSE_STATUS:
                    action = { Course c ->
                        switch (CourseStatusDTO.fromValue(value)) {
                            case COURSE_DISABLED:
                                c.currentlyOffered = false
                                c.isShownOnWeb = false
                                break
                            case ENABLED:
                                c.isShownOnWeb = false
                                c.currentlyOffered = true
                                break
                            case ENABLED_AND_VISIBLE_ONLINE:
                                c.isShownOnWeb = true
                                c.currentlyOffered = true
                                break
                            default:
                                validator.throwClientErrorException(c.id, key, "Unrecognized course status value ${value}")
                        }
                    }
                    break
                case Course.COURSE_DATA_COLLECTION_RULE_ID:
                    action = { Course c ->
                        c.fieldConfigurationSchema = fieldConfigurationSchemeDao.getById(c.context, value as Long)
                        if (!c.fieldConfigurationSchema) {
                            validator.throwClientErrorException(c.id, key, "Unrecognized data collection rule")
                        }
                    }
                    break
                default:
                    validator.throwClientErrorException(key, "Unsupported attribute")
            }
        }
        action
    }

    @Override
    void remove(Course course, ObjectContext context) {
        context.deleteObjects(EntityRelationDao.getRelatedFrom(course.context, Course.simpleName, course.id))
        context.deleteObjects(EntityRelationDao.getRelatedTo(course.context, Course.simpleName, course.id))
        context.deleteObject(course)
    }
}
