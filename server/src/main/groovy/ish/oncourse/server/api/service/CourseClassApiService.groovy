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
import ish.cancel.CancelationResult
import ish.common.types.EnrolmentStatus
import ish.common.types.OutcomeStatus
import ish.duplicate.ClassDuplicationRequest
import ish.duplicate.DuplicationResult
import ish.oncourse.entity.services.CourseClassService
import ish.oncourse.server.api.dao.AssessmentClassDao
import ish.oncourse.server.api.dao.ClassCostDao
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.SessionModuleDao
import ish.oncourse.server.api.dao.SiteDao
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import ish.oncourse.server.api.v1.function.DocumentFunctions
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.CancelCourseClassDTO
import ish.oncourse.server.api.v1.model.ClassFundingSourceDTO
import ish.oncourse.server.api.v1.model.CourseClassAttendanceTypeDTO
import ish.oncourse.server.api.v1.model.CourseClassDTO
import ish.oncourse.server.api.v1.model.CourseClassDuplicateDTO
import ish.oncourse.server.api.v1.model.DeliveryModeDTO
import ish.oncourse.server.api.v1.model.TrainingPlanDTO
import static ish.oncourse.server.api.validation.EntityValidator.validateLength
import ish.oncourse.server.cancel.CancelClassHelper
import ish.oncourse.server.cancel.CancelEnrolmentService
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassAttachmentRelation
import ish.oncourse.server.cayenne.CourseClassCustomField
import ish.oncourse.server.cayenne.CourseClassTagRelation
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.SessionModule
import ish.oncourse.server.duplicate.DuplicateClassService
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CourseClassApiService extends TaggableApiService<CourseClassDTO, CourseClass, CourseClassDao> {

    public static final String CLASS_HAS_ENROLMENTS_MESSAGE = "This class has enrolled students."

    public static final String CLASS_HAS_INVOICES_MESSAGE = "This class has custom invoices."

    public static final String CLASS_HAS_PAYSLIPS = "This class has tutor pay lines."

    public static final String CLASS_HAS_QUEUED_RECORDS_MESSAGE = "Synchronisation is not completed for this CourseClass. Please wait for it and then try again, or just cancel the class instead."

    public static final String CLASS_PUBLISHED_MESSAGE = "You may not delete a class which has been published to the website. Please unpublish it or cancel this class instead."

    @Inject
    private ModuleDao moduleDao

    @Inject
    private AssessmentClassDao assessmentClassDao

    @Inject
    private AssessmentClassApiService assessmentClassService

    @Inject
    private CourseDao courseDao

    @Inject
    private ClassCostDao classCostDao

    @Inject
    private SessionModuleDao sessionModuleDao

    @Inject
    private FundingSourceDao fundingSourceDao

    @Inject
    private SiteDao siteDao

    @Inject
    private DuplicateClassService duplicateClassService

    @Inject
    private EventService eventService;

    @Inject
    private CancelEnrolmentService cancelEnrolmentService;

    @Inject
    private CourseClassService classService

    @Inject
    private SessionApiService sessionService

    @Inject
    private DocumentService documentService

    @Inject
    private CourseApiService courseService

    @Inject
    private ClassCostApiService classCostService

    @Inject
    private SystemUserService systemUserService

    private static final Logger logger = LogManager.getLogger(CourseClassApiService)

    @Override
    Class<CourseClass> getPersistentClass() {
        return CourseClass
    }

    @Override
    CourseClassDTO toRestModel(CourseClass cc) {
        CourseClassDTO dto =  new CourseClassDTO()
        dto.id = cc.id
        dto.courseId = cc.course.id
        dto.code = cc.code
        dto.courseCode = cc.course.code
        dto.courseName = cc.course.name
        dto.attendanceType = CourseClassAttendanceTypeDTO.values()[0].fromDbType(cc.attendanceType)
        dto.createdOn = LocalDateUtils.dateToTimeValue(cc.createdOn)
        dto.modifiedOn = LocalDateUtils.dateToTimeValue(cc.modifiedOn)
        dto.budgetedPlaces = cc.budgetedPlaces
        dto.censusDate = cc.censusDate
        dto.fundingSource = ClassFundingSourceDTO.values()[0].fromDbType(cc.fundingSource)
        dto.deliveryMode = DeliveryModeDTO.values()[0].fromDbType(cc.deliveryMode)
        dto.deposit = cc.deposit?.toBigDecimal()
        dto.detBookingId = cc.detBookingId
        dto.expectedHours = cc.expectedHours
        dto.feeExcludeGST = cc.feeExGst?.toBigDecimal()
        dto.finalDetExport = cc.finalDETexport
        dto.relatedFundingSourceId = cc.relatedFundingSource?.id
        dto.initialDetExport = cc.initialDETexport
        dto.isActive = cc.isActive
        dto.isCancelled = cc.isCancelled
        dto.isDistantLearningCourse = cc.isDistantLearningCourse
        dto.isShownOnWeb = cc.isShownOnWeb
        dto.isTraineeship = cc.isTraineeship
        dto.maximumDays = cc.maximumDays
        dto.maximumPlaces = cc.maximumPlaces
        dto.maxStudentAge = cc.maxStudentAge
        dto.message = cc.message
        dto.midwayDetExport = cc.midwayDETexport
        dto.minimumPlaces = cc.minimumPlaces
        dto.minStudentAge = cc.minStudentAge
        dto.reportingPeriod = cc.reportingPeriod
        dto.roomId = cc.room?.id
        dto.virtualSiteId = (cc.room?.site?.isVirtual ? cc.room.site.id : null) as Long
        dto.sessionsCount = cc.sessionsCount
        dto.startDateTime = LocalDateUtils.dateToTimeValue(cc.startDateTime)
        dto.endDateTime =  LocalDateUtils.dateToTimeValue(cc.endDateTime)
        dto.suppressAvetmissExport = cc.suppressAvetmissExport
        dto.vetCourseSiteID = cc.vetCourseSiteID
        dto.vetFundingSourceStateID = cc.vetFundingSourceStateID
        dto.vetPurchasingContractID = cc.vetPurchasingContractID
        dto.vetPurchasingContractScheduleID = cc.vetPurchasingContractScheduleID
        dto.webDescription = cc.webDescription
        dto.feeHelpClass = cc.course.feeHelpClass
        int toProceed = classService.getEnrolmentsToProceed(cc)
        dto.enrolmentsToProfitLeftCount =  toProceed > 0 ? toProceed : 0

        dto.taxId = cc.costs.find(ClassCostDao.studentFee)?.tax?.id
        dto.reportableHours = cc.reportableHours
        dto.qualificationHours = cc.qualificationHours
        dto.nominalHours = cc.nominalHours
        dto.classroomHours = cc.classroomHours
        dto.studentContactHours = cc.studentContactHours
        dto.documents = cc.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
        dto.customFields = cc.customFields.collectEntries { [(it.customFieldType.key) : it.value] }

        List<Enrolment> enrolments = cc.enrolments
        dto.allEnrolmentsCount = enrolments.size()
        dto.successAndQueuedEnrolmentsCount = enrolments.findAll { it.status in EnrolmentStatus.STATUSES_LEGIT }.size()
        dto.canceledEnrolmentsCount = enrolments.findAll { it.status in EnrolmentStatus.STATUSES_CANCELLATIONS }.size()
        dto.failedEnrolmentsCount =  enrolments.findAll { it.status in EnrolmentStatus.STATUSES_FAILED }.size()

        List<Outcome> outcomes = enrolments*.outcomes.flatten() as List<Outcome>
        dto.allOutcomesCount  = outcomes.size()
        dto.passOutcomesCount = outcomes.findAll { it.status in [OutcomeStatus.STATUS_ASSESSABLE_PASS, OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED] }.size()
        dto.failedOutcomesCount = outcomes.findAll { it.status in [OutcomeStatus.STATUS_ASSESSABLE_FAIL, OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED] }.size()
        dto.inProgressOutcomesCount = outcomes.findAll { it.status == OutcomeStatus.STATUS_NOT_SET }.size()
        dto.withdrawnOutcomesCount = outcomes.findAll { it.status == OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN }.size()
        dto.otherOutcomesCount = dto.allOutcomesCount  - dto.passOutcomesCount - dto.failedOutcomesCount - dto.inProgressOutcomesCount - dto.withdrawnOutcomesCount
        dto.tags = cc.tags.collect { toRestTagMinimized(it) }
        return dto
    }

    @Override
    CourseClass toCayenneModel(CourseClassDTO dto, CourseClass courseClass) {

        ObjectContext context = courseClass.context

        if (courseClass.newRecord) {
            courseClass.course = courseDao.getById(context, dto.courseId)
        }

        courseClass.code = dto.code

        if (dto.isDistantLearningCourse) {
            courseClass.isDistantLearningCourse = true
            courseClass.maximumDays = dto.maximumDays
            courseClass.expectedHours = dto.expectedHours
            if (dto.virtualSiteId != null) {
                courseClass.room = siteDao.getById(courseClass.context, dto.virtualSiteId).rooms[0]
            }
        } else {
            courseClass.isDistantLearningCourse = false
        }
        courseClass.isActive = dto.isActive
        courseClass.isShownOnWeb = dto.isShownOnWeb
        courseClass.message = dto.message
        courseClass.minStudentAge = dto.minStudentAge
        courseClass.maxStudentAge = dto.maxStudentAge
        courseClass.minimumPlaces = dto.minimumPlaces
        courseClass.maximumPlaces = dto.maximumPlaces
        courseClass.budgetedPlaces = dto.budgetedPlaces
        courseClass.attendanceType = dto.attendanceType?.dbType
        courseClass.suppressAvetmissExport = dto.suppressAvetmissExport
        courseClass.deliveryMode = dto.deliveryMode?.dbType
        if (dto.relatedFundingSourceId != null) {
            courseClass.relatedFundingSource = fundingSourceDao.getById(context, dto.relatedFundingSourceId)
        }
        courseClass.fundingSource = dto.fundingSource?.dbType
        courseClass.vetFundingSourceStateID = dto.vetFundingSourceStateID
        courseClass.vetCourseSiteID = dto.vetCourseSiteID
        courseClass.vetPurchasingContractID = dto.vetPurchasingContractID
        courseClass.vetPurchasingContractScheduleID = dto.vetPurchasingContractScheduleID
        courseClass.detBookingId = dto.detBookingId
        courseClass.reportableHours = dto.reportableHours
        courseClass.censusDate = dto.censusDate
        courseClass.reportingPeriod = dto.reportingPeriod
        courseClass.webDescription = dto.webDescription
        courseClass.initialDETexport = dto.initialDetExport
        courseClass.midwayDETexport = dto.midwayDetExport
        courseClass.finalDETexport = dto.finalDetExport
        updateTags(courseClass, courseClass.taggingRelations, dto.tags*.id, CourseClassTagRelation, courseClass.context)
        DocumentFunctions.updateDocuments(courseClass, courseClass.attachmentRelations, dto.documents, CourseClassAttachmentRelation, context)
        updateCustomFields(courseClass.context, courseClass, dto.customFields, CourseClassCustomField)
        courseClass
    }

    @Override
    void validateModelBeforeSave(CourseClassDTO dto, ObjectContext context, Long id) {

        if (dto.code == null) {
            validator.throwClientErrorException(id, 'code', 'You need to enter a class code.')
        } else if (!dto.code.matches ('^\\w+(\\.\\w+)*$')) {
            validator.throwClientErrorException(id, 'code', 'Class code must start and end with letters or numbers and may contain full stops.')
        } else if (dto.code.length() > 50 ) {
            validator.throwClientErrorException(id, 'code', 'Class code cannot be greater than 50 characters.')
        }
        if (dto.courseId == null) {
            validator.throwClientErrorException(id, 'courseId', 'You must link this class to a course.')
        }

        List<CourseClass> classes =  entityDao.getByCode(context, dto.code, dto.courseId)

        if (!classes.empty && classes[0].id != id) {
            validator.throwClientErrorException(id, 'code', 'The class code must be unique within the course.')
        }

        if (dto.deliveryMode == null) {
            validator.throwClientErrorException(id, 'deliveryMode', 'Delivery mode is required')
        }

        if (dto.fundingSource == null) {
            validator.throwClientErrorException(id, 'fundingSource', 'Funding source is required')
        }

        if (dto.minimumPlaces == null || dto.minimumPlaces < 0) {
            validator.throwClientErrorException(id, 'minimumPlaces', 'Minimum places is wrong')
        }
        if (dto.maximumPlaces == null || dto.maximumPlaces < 0) {
            validator.throwClientErrorException(id, 'maximumPlaces', 'Maximum places is wrong')
        }
        if (dto.budgetedPlaces == null || dto.budgetedPlaces < 0)  {
            validator.throwClientErrorException(id, 'budgetedPlaces', 'Budgeted places is required')
        }
        if (dto.maximumPlaces < dto.minimumPlaces ) {
            validator.throwClientErrorException(id, 'maximumPlaces', 'Maximum places cannot be less than minimum places.')
        }
        if (dto.isDistantLearningCourse == null) {
            validator.throwClientErrorException(id, 'isDistantLearningCourse', 'Is self paced flag is required')
        }
        if (dto.isActive == null) {
            validator.throwClientErrorException(id, 'isActive', 'Is active flag required')
        }
        if (dto.isShownOnWeb == null) {
            validator.throwClientErrorException(id, 'isShownOnWeb', 'Is shown on web flag is required')
        }
        if (dto.suppressAvetmissExport == null) {
            validator.throwClientErrorException(id, 'suppressAvetmissExport', 'Suppress avetmiss export flag is required')
        }
        if (dto.attendanceType == null) {
            validator.throwClientErrorException(id, 'attendanceType', 'Attendance type is required')
        }
        if (dto.reportableHours == null) {
            validator.throwClientErrorException(id, 'reportableHours', 'Reportable hours is required')
        }

        validateLength(id, trimToEmpty(dto.vetFundingSourceStateID), 'vetFundingSourceStateID', 3)
        validateLength(id, trimToEmpty(dto.vetPurchasingContractID), 'vetPurchasingContractID', 12)
        validateLength(id, trimToEmpty(dto.vetPurchasingContractScheduleID), 'vetPurchasingContractScheduleID', 3)

        Course dbCourse = courseService.getEntityAndValidateExistence(context, dto.courseId)
        if (dto.expectedHours == null && dto.isDistantLearningCourse && (dbCourse.modules == null || dbCourse.modules.size() == 0)) {
            validator.throwClientErrorException(id, 'expectedHours', 'Expected study hours is required for self-paced non-VET class')
        }
    }

    @Override
    void validateModelBeforeRemove(CourseClass c) {

        if (c.isShownOnWeb) {
            validator.throwClientErrorException(c.id, null, CLASS_PUBLISHED_MESSAGE)
        }

        if (!c.enrolments.empty) {
            validator.throwClientErrorException(c.id, null, CLASS_HAS_ENROLMENTS_MESSAGE)
        }

        if (!c.invoiceLines.empty) {
            validator.throwClientErrorException(c.id, null, CLASS_HAS_INVOICES_MESSAGE)
        }
        if (!c.costs*.paylines.flatten().empty) {
            validator.throwClientErrorException(c.id, null, CLASS_HAS_PAYSLIPS)
        }
    }

    void remove (CourseClass persistent, ObjectContext context) {
        context.deleteObjects(persistent.costs)
        context.deleteObjects(persistent.sessions*.sessionModules.flatten())
        context.deleteObjects(persistent.sessions*.sessionTutors.flatten())
        context.deleteObjects(persistent.assessmentClasses*.assessmentClassModules.flatten())
        context.deleteObjects(persistent.assessmentClasses*.assessmentClassTutors.flatten())
        context.deleteObjects(persistent.assessmentClasses)
        context.deleteObjects(persistent.tutorRoles)
        context.deleteObjects(persistent.sessions)
        context.deleteObjects(persistent.attachmentRelations)
        context.deleteObjects(persistent.taggingRelations)
        context.deleteObjects(persistent.corporatePassCourseClass)
        context.deleteObjects(persistent.discountCourseClasses)
        context.deleteObjects(persistent.noteRelations*.note)
        context.deleteObjects(persistent.noteRelations)
        context.deleteObject(persistent)
    }

    List<Long> duplicateClass(CourseClassDuplicateDTO courseClassDuplicate) {
        ObjectContext context = cayenneService.newContext

        if (courseClassDuplicate.classIds == null) {
            validator.throwClientErrorException('classIds', 'Class Ids is required')
        } else {
            courseClassDuplicate.classIds.each {
                if (!entityDao.getById(context, it)) {
                    validator.throwClientErrorException('classIds', "Class with id=$it not found.")
                }
            }
        }
        if (courseClassDuplicate.daysTo == null) {
            validator.throwClientErrorException('daysTo', "Invalid days to value.")
        }
        if (courseClassDuplicate.copyTutors == null) {
            validator.throwClientErrorException('copyTutors', 'copyTutors  is required')
        }
        if (courseClassDuplicate.copyTrainingPlans == null) {
            validator.throwClientErrorException('copyTrainingPlans', 'copyTrainingPlans is required')
        }
        if (courseClassDuplicate.applyDiscounts == null) {
            validator.throwClientErrorException('applyDiscounts', 'applyDiscounts is required')
        }
        if (courseClassDuplicate.copyCosts == null) {
            validator.throwClientErrorException('copyCosts', 'copyCosts is required')
        }
        if (courseClassDuplicate.copySitesAndRooms == null) {
            validator.throwClientErrorException('copySitesAndRooms', 'copySitesAndRooms is required')
        }
        if (courseClassDuplicate.copyPayableTimeForSessions == null) {
            validator.throwClientErrorException('copyPayableTimeForSessions', 'copyPayableTimeForSessions is required')
        }
        if (courseClassDuplicate.copyVetData == null) {
            validator.throwClientErrorException('copyVetData', 'copyVetData is required')
        }
        if (courseClassDuplicate.copyNotes == null) {
            validator.throwClientErrorException('copyNotes', 'copyNotes is required')
        }
        if (courseClassDuplicate.copyAssessments == null) {
            validator.throwClientErrorException('copyAssessments', 'copyAssessments is required')
        }
        if (courseClassDuplicate.copyOnlyMandatoryTags == null) {
            validator.throwClientErrorException('copyOnlyMandatoryTags', 'copyOnlyMandatoryTags is required')
        }

        DuplicationResult result = duplicateClassService
                .duplicateClasses(new ClassDuplicationRequest().with { req ->
            req.ids = courseClassDuplicate.classIds
            req.courseId = courseClassDuplicate.courseId
            req.daysTo = courseClassDuplicate.daysTo
            req.copyTutors = courseClassDuplicate.copyTutors
            req.copyTrainingPlans = courseClassDuplicate.copyTrainingPlans
            req.applyDiscounts = courseClassDuplicate.applyDiscounts
            req.copyCosts = courseClassDuplicate.copyCosts
            req.copySitesAndRooms = courseClassDuplicate.copySitesAndRooms
            req.copyPayableTimeForSessions = courseClassDuplicate.copyPayableTimeForSessions
            req.copyVetData = courseClassDuplicate.copyVetData
            req.copyNotes = courseClassDuplicate.copyNotes
            req.copyAssessments = courseClassDuplicate.copyAssessments
            req.copyOnlyMandatoryTags = !courseClassDuplicate.copyOnlyMandatoryTags
            req
        }, courseClassDuplicate.classCost)

        if (result.isFailed()) {
            validator.throwClientErrorException('Duplicate error', result.getFailure().getDescription())
        }

        result.newIds
    }

    void cancelClass(CancelCourseClassDTO cancelCourseClassDTO) {
        ObjectContext context = cayenneService.newContext

        if (cancelCourseClassDTO.classIds == null) {
            validator.throwClientErrorException('classIds', 'Class Id is required')
        } else if (cancelCourseClassDTO.classIds.size() != 1) {
            validator.throwClientErrorException('classIds', 'Can not cancel more then one class')
        }

        Long id = cancelCourseClassDTO.classIds[0]

        CourseClass courseClass = getEntityAndValidateExistence(context, id)
        if(courseClass.isCancelled) {
            validator.throwClientErrorException('isCancelled', "Class with id=$id was already cancelled.")
        }

        Enrolment enrolment = courseClass.successAndQueuedEnrolments.find {it.invoiceLines.size() > 1}
        if  (enrolment) {
            String error = "You cannot cancel $courseClass.uniqueCode because it has enrolment for $enrolment.student.contact.fullName  which linked to more than one invoice." +
                    " Instead cancel the enrolment separately (through Enrolments list), review the credit notes and then process the class cancellation."
            validator.throwClientErrorException(id, null, error)
        }

        if(cancelCourseClassDTO.refundManualInvoices == null) {
            validator.throwClientErrorException('refundManualInvoices', 'RefundManualInvoices is required.')
        }

        if(cancelCourseClassDTO.sendEmail == null) {
            validator.throwClientErrorException('sendEmail', 'SendEmail  is required.')
        }

        CancelClassHelper classHelper = new CancelClassHelper()
        classHelper.context = context
        classHelper.courseCLass = courseClass
        classHelper.cancelEnrolmentService = cancelEnrolmentService
        classHelper.eventService = eventService
        classHelper.refundManualInvoices = cancelCourseClassDTO.refundManualInvoices
        classHelper.sendEmail = cancelCourseClassDTO.sendEmail
        classHelper.systemUser = systemUserService.currentUser

        CancelationResult cancelationResult = classHelper.cancel()

        if (cancelationResult.isFailed()) {
            String message = "Class cancellation failed \n"
            cancelationResult.getFailures().each { failure ->
                message + failure.description + '\n'
            }

            logger.error(message)

            validator.throwClientErrorException(null, message)
        }
    }

    List<TrainingPlanDTO> getTrainingPlan(Long classId) {
        ObjectContext context = cayenneService.newContext
        CourseClass courseClass = getEntityAndValidateExistence(context, classId)
        return courseClass.course.modules.collect { module ->
            TrainingPlanDTO dto = new TrainingPlanDTO()
            dto.moduleId = module.id
            dto.moduleName = module.nationalCode
            dto.moduleTitle = module.title
            dto.sessionIds = sessionModuleDao.getTrainingPlan(context, courseClass, module)*.session.id
            dto.assessmentIds = assessmentClassDao.getTrainingPlan(context, courseClass, module)*.assessmentClass.id
            dto
        }

    }

    void updateTrainingPlan(Long classId, List<TrainingPlanDTO> trainingPlans) {

        ObjectContext context = cayenneService.newContext
        CourseClass courseClass = getEntityAndValidateExistence(context, classId)
        trainingPlans.each {  trainingPlan ->
            ish.oncourse.server.cayenne.Module module = moduleDao.getById(context, trainingPlan.moduleId)

            List<SessionModule> moduleSessions = sessionModuleDao.getTrainingPlan(context, courseClass, module)
            context.deleteObjects(moduleSessions.findAll { !(it.session.id in trainingPlan.sessionIds) })

            trainingPlan.sessionIds.findAll { !(it in moduleSessions*.session.id)}.each { id ->
                Session session = sessionService.getEntityAndValidateExistence(context, id)
                sessionModuleDao.newObject(context, session, module)
            }

            List<AssessmentClassModule> assessmentClassModules = assessmentClassDao.getTrainingPlan(context, courseClass, module)
            context.deleteObjects(assessmentClassModules.findAll { !(it.assessmentClass.id in trainingPlan.assessmentIds) })

            trainingPlan.assessmentIds.findAll { !(it in assessmentClassModules*.assessmentClass.id)}.each { id ->
                AssessmentClass assessmentClass = assessmentClassService.getEntityAndValidateExistence(context, id)
                assessmentClassDao.newObject(context, assessmentClass, module)
            }
            // update course class and related outcome start/end dates
            courseClass.modifiedOn = new Date()
        }

        save(context)
    }

    @Override
    Closure getAction(String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            switch (key) {
                case CourseClass.IS_SHOWN_ON_WEB.name:
                    validator.validateBoolean(value, key)
                    action = { CourseClass c ->
                        c.isShownOnWeb = Boolean.valueOf(value)
                    }
                    break
                case CourseClass.IS_ACTIVE.name:
                    validator.validateBoolean(value, key)
                    action = { CourseClass c ->
                        c.isActive = Boolean.valueOf(value)
                    }
                    break
                default:
                    validator.throwClientErrorException(key, "Unsupported attribute")
            }
        }
        return action
    }

}
