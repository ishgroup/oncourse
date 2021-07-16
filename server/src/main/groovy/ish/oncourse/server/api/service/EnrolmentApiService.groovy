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
import ish.cancel.EnrolmentCancelationRequest
import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.v1.function.CustomFieldFunctions
import ish.oncourse.server.api.v1.function.DocumentFunctions
import ish.oncourse.server.api.v1.function.TagFunctions
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cancel.CancelEnrolmentService
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.AssessmentSubmissionFunctions.updateSubmissions
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.EnrolmentFunctions.*
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.validation.EntityValidator.validateLength
import static org.apache.commons.lang3.StringUtils.trimToNull

class EnrolmentApiService extends TaggableApiService<EnrolmentDTO, Enrolment, EnrolmentDao> {

    @Inject
    private CancelEnrolmentService cancelEnrolmentService

    @Inject
    private AssessmentApiService assessmentApiService

    @Inject
    private AssessmentSubmissionApiService submissionApiService

    @Inject
    private SystemUserService systemUserService

    @Inject
    private DocumentService documentService

    @Inject
    private FundingSourceDao fundingSourceDao

    private static final String RELATED_FUNDING_SOURCE_ID = "relatedFundingSourceId"

    @Override
    Class<Enrolment> getPersistentClass() {
        return Enrolment
    }

    @Override
    EnrolmentDTO toRestModel(Enrolment enrolment) {
        new EnrolmentDTO().with { enrolmentDTO ->
            enrolmentDTO.feeHelpClass = enrolment.courseClass.course.feeHelpClass
            enrolmentDTO.id = enrolment.id
            enrolmentDTO.tags = enrolment.tags.collect { toRestTagMinimized(it) }
            enrolment.student.contact.with { contact ->
                enrolmentDTO.studentContactId = contact.id
                enrolmentDTO.studentName = contact.getFullName()
            }
            enrolment.courseClass.with { courseClass ->
                enrolmentDTO.courseClassId = courseClass.id
                enrolmentDTO.courseClassName = "$courseClass.course.name $courseClass.course.code-$courseClass.code"
            }
            enrolmentDTO.status = EnrolmentStatusDTO.values()[0].fromDbType(enrolment.status)
            enrolmentDTO.displayStatus = enrolment.displayStatus
            enrolmentDTO.source = PaymentSourceDTO.values()[0].fromDbType(enrolment.source)
            enrolmentDTO.confirmationStatus = ConfirmationStatusDTO.values()[0]
                    .fromDbType(enrolment.confirmationStatus)

            enrolmentDTO.eligibilityExemptionIndicator = enrolment.eligibilityExemptionIndicator
            enrolmentDTO.outcomeIdTrainingOrg = enrolment.outcomeIdTrainingOrg
            enrolmentDTO.studentIndustryANZSICCode = enrolment.studentIndustryANZSICCode
            enrolmentDTO.vetClientID = enrolment.vetClientID
            enrolmentDTO.vetFundingSourceStateID = enrolment.vetFundingSourceStateID
            enrolmentDTO.vetIsFullTime = enrolment.vetIsFullTime

            enrolmentDTO.relatedFundingSourceId = enrolment.relatedFundingSource?.id

            enrolmentDTO.studyReason = EnrolmentStudyReasonDTO.values()[0]
                    .fromDbType(enrolment.studyReason)
            enrolmentDTO.vetFeeExemptionType = EnrolmentExemptionTypeDTO.values()[0]
                    .fromDbType(enrolment.vetFeeExemptionType) as EnrolmentExemptionTypeDTO
            enrolmentDTO.fundingSource = ClassFundingSourceDTO.values()[0].fromDbType(enrolment.fundingSource)
            enrolmentDTO.associatedCourseIdentifier = enrolment.associatedCourseIdentifier
            enrolmentDTO.vetInSchools = enrolment.vetInSchools
            enrolmentDTO.suppressAvetmissExport = enrolment.suppressAvetmissExport
            enrolmentDTO.vetPurchasingContractID = enrolment.vetPurchasingContractID
            enrolmentDTO.outcomeIdTrainingOrg = enrolment.outcomeIdTrainingOrg
            enrolmentDTO.vetTrainingContractID = enrolment.vetTrainingContractID
            enrolmentDTO.cricosConfirmation = enrolment.cricosConfirmation
            enrolmentDTO.vetFeeIndicator = enrolment.vetFeeIndicator
            enrolmentDTO.trainingPlanDeveloped = enrolment.trainingPlanDeveloped

            enrolmentDTO.feeCharged = enrolment.feeCharged.toBigDecimal()

            enrolmentDTO.feeHelpAmount = enrolment.feeHelpAmount?.toBigDecimal()
            enrolmentDTO.creditOfferedValue = enrolment.creditOfferedValue
            enrolmentDTO.feeStatus = FEE_STATUS_MAP[enrolment.feeStatus]
            enrolmentDTO.creditUsedValue = enrolment.creditUsedValue
            enrolmentDTO.creditFOEId = enrolment.creditFOEId
            enrolmentDTO.creditProvider = enrolment.creditProvider
            enrolmentDTO.attendanceType = CourseClassAttendanceTypeDTO.values()[0].fromDbType(enrolment.attendanceType)
            enrolmentDTO.creditProviderType =  CREDIT_PROVIDER_TYPE_MAP[enrolment.creditProviderType]
            enrolmentDTO.creditTotal = CREDIT_TOTAL_MAP[enrolment.creditTotal]
            enrolmentDTO.creditType = CREDIT_TYPE_MAP[enrolment.creditType]
            enrolmentDTO.creditLevel = CREDIT_LEVEL_MAP[enrolment.creditLevel]
            enrolmentDTO.customFields = enrolment.customFields.collectEntries { [(it.customFieldType.key) : it.value] }
            enrolmentDTO.documents = enrolment.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            enrolmentDTO.invoicesCount = enrolment.invoiceLines.invoice.toSet().size()
            enrolmentDTO.outcomesCount = enrolment.outcomes.size()
            enrolmentDTO.createdOn = LocalDateUtils.dateToTimeValue(enrolment.createdOn)
            enrolmentDTO.modifiedOn = LocalDateUtils.dateToTimeValue(enrolment.modifiedOn)
            enrolmentDTO.assessments = enrolment.courseClass.assessmentClasses*.assessment.collect { assessmentApiService.toRestModel(it) }
            enrolmentDTO.submissions = enrolment.assessmentSubmissions.collect { submissionApiService.toRestMinimizedModel(it) }
            enrolmentDTO
        }
    }

    @Override
    Enrolment toCayenneModel(EnrolmentDTO dto, Enrolment enrolment) {
        ObjectContext context = enrolment.context

        if (dto.relatedFundingSourceId != null) {
            enrolment.relatedFundingSource = fundingSourceDao.getById(context, dto.relatedFundingSourceId)
        }
        enrolment.studyReason = dto.studyReason?.dbType
        enrolment.vetFeeExemptionType = dto.vetFeeExemptionType?.dbType
        enrolment.fundingSource = dto.fundingSource?.dbType
        enrolment.cricosConfirmation = dto.cricosConfirmation
        enrolment.vetIsFullTime = dto.vetIsFullTime
        enrolment.vetInSchools = dto.vetInSchools
        enrolment.suppressAvetmissExport = dto.suppressAvetmissExport
        enrolment.associatedCourseIdentifier = dto.associatedCourseIdentifier
        enrolment.vetPurchasingContractID = dto.vetPurchasingContractID
        enrolment.eligibilityExemptionIndicator = dto.eligibilityExemptionIndicator
        enrolment.outcomeIdTrainingOrg = dto.outcomeIdTrainingOrg
        enrolment.studentIndustryANZSICCode = dto.studentIndustryANZSICCode
        enrolment.vetClientID = dto.vetClientID
        enrolment.vetTrainingContractID = dto.vetTrainingContractID
        enrolment.vetFundingSourceStateID = dto.vetFundingSourceStateID
        enrolment.outcomeIdTrainingOrg = dto.outcomeIdTrainingOrg
        enrolment.vetFeeIndicator = dto.vetFeeIndicator
        enrolment.trainingPlanDeveloped = dto.trainingPlanDeveloped
        enrolment.feeHelpAmount = new Money(dto.feeHelpAmount)
        enrolment.feeStatus = FEE_STATUS_MAP.getByValue(dto.feeStatus)
        enrolment.attendanceType = dto.attendanceType.dbType
        enrolment.creditOfferedValue = dto.creditOfferedValue
        enrolment.creditUsedValue = dto.creditUsedValue
        enrolment.creditFOEId = dto.creditFOEId
        enrolment.creditProvider = dto.creditProvider
        enrolment.creditProviderType = CREDIT_PROVIDER_TYPE_MAP.getByValue(dto.creditProviderType)
        enrolment.creditTotal =  CREDIT_TOTAL_MAP.getByValue(dto.creditTotal)
        enrolment.creditType = CREDIT_TYPE_MAP.getByValue(dto.creditType)
        enrolment.creditLevel = CREDIT_LEVEL_MAP.getByValue(dto.creditLevel)

        updateSubmissions(submissionApiService, this, dto.submissions, enrolment.assessmentSubmissions, context)
        TagFunctions.updateTags(enrolment, enrolment.taggingRelations, dto.tags*.id, EnrolmentTagRelation, context)
        DocumentFunctions.updateDocuments(enrolment, enrolment.attachmentRelations, dto.documents, EnrolmentAttachmentRelation, context)
        CustomFieldFunctions.updateCustomFields(context, enrolment, dto.customFields, EnrolmentCustomField)
        return enrolment
    }

    @Override
    void validateModelBeforeSave(EnrolmentDTO dto, ObjectContext context, Long id) {
        if (id == null) {
            throw new IllegalAccessError('Forbidden operation - enrolment save')
        }

        Enrolment dbEnrolment = getRecordById(context, Enrolment, id)

        if ((dto.vetClientID != null && dto.vetClientID.length() > 0 && (dto.vetTrainingContractID == null || dto.vetTrainingContractID.length() == 0)) ||
                (dto.vetTrainingContractID != null && dto.vetTrainingContractID.length() > 0 && (dto.vetClientID == null || dto.vetClientID.length() == 0))) {
            validator.throwClientErrorException(id, 'vetTrainingContractID', 'If you enter data in either the Training Contract ID or the Training Contract Client ID, then you must fill out both.')
        }

        validateLength(id, dto.associatedCourseIdentifier, 'associatedCourseIdentifier', 10)
        validateLength(id, dto.creditFOEId, 'creditFOEId', 4)
        validateLength(id, dto.creditOfferedValue, 'creditOfferedValue', 4)
        validateLength(id, dto.creditProvider, 'creditProvider', 4)
        validateLength(id, dto.creditUsedValue, 'creditUsedValue', 4)
        validateLength(id, dto.cricosConfirmation, 'cricosConfirmation', 32)
        validateLength(id, dto.outcomeIdTrainingOrg, 'outcomeIdTrainingOrg', 3)
        validateLength(id, dto.vetClientID, 'vetClientID',10)
        validateLength(id, dto.vetFundingSourceStateID, 'vetFundingSourceStateID', 3)
        validateLength(id, dto.vetPurchasingContractID, 'vetPurchasingContractID', 12)
        validateLength(id, dto.vetTrainingContractID, 'vetTrainingContractID', 10)

        if (dto.studentIndustryANZSICCode && (dto.studentIndustryANZSICCode > 99 || dto.studentIndustryANZSICCode < 1)) {
            validator.throwClientErrorException(id, 'studentIndustryANZSICCode', 'Status code must be between 1-99.')
        }
    }

    @Override
    void validateModelBeforeRemove(Enrolment enrolment) {
        throw new IllegalAccessError('Forbidden operation - enrolment delete')
    }

    void cancelEnrolment(CancelEnrolmentDTO cancelEnrolmentDTO) {
        ObjectContext context = cayenneService.newContext

        if (cancelEnrolmentDTO.enrolmentIds == null) {
            validator.throwClientErrorException('enrolmentIds', 'Enrolment Id is required')
        }

        Long id = cancelEnrolmentDTO.enrolmentIds

        Enrolment cancelEnrolment = getEntityAndValidateExistence(context, id)
        if (EnrolmentStatus.CANCELLED.equals(cancelEnrolment.getStatus()) || EnrolmentStatus.REFUNDED.equals(cancelEnrolment.getStatus())) {
            validator.throwClientErrorException('status', "The selected enrolment was already cancelled or refunded.")
        }

        if (cancelEnrolment.getInvoiceLines().isEmpty()) {
            Student student = cancelEnrolment.getStudent();
            String message = String.format("The enrolment for %s could not be refunded since it is not linked to any invoice",
                    student.getContact() == null ? String.format("student with number %d", student.getStudentNumber()) : student.getContact().getName());
            validator.throwClientErrorException('status', message)
        }

        EnrolmentCancelationRequest request = new EnrolmentCancelationRequest().with {req ->
            req.enrolmentId = cancelEnrolmentDTO.enrolmentIds
            req.transfer = cancelEnrolmentDTO.transfer
            cancelEnrolmentDTO.invoiceLineParam.each { refundInvoice ->
                req.addLineToRefund(new EnrolmentCancelationRequest.InvoiceLineRequest().with { lineReq ->
                    lineReq.invoiceLineId = refundInvoice.invoiceLineId
                    lineReq.accountId = refundInvoice.accountId
                    lineReq.taxId = refundInvoice.taxId
                    lineReq.cancellationFee = refundInvoice.cancellationFee
                    lineReq.sendInvoice = refundInvoice.sendInvoice
                    lineReq
                })
            }
            req
        }

        CancelationResult cancelationResult = cancelEnrolmentService
                .cancelEnrolment(request, cancelEnrolmentDTO.deleteNotSetOutcomes, cancelEnrolment)

        if (cancelationResult.isFailed()) {
            cancelationResult.getFailures().each { fail ->
                validator.throwClientErrorException('Enrolment cancellation error',
                        fail.description) }
        }

    }


    Closure getAction(String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            switch (key) {
                case Enrolment.FUNDING_SOURCE.name:
                    action = { Enrolment e ->
                        e.fundingSource = ClassFundingSourceDTO.fromValue(value)?.dbType
                    }
                    break
                case RELATED_FUNDING_SOURCE_ID:
                    action = { Enrolment e ->
                        if (value != null) {
                            e.relatedFundingSource = fundingSourceDao.getById(e.objectContext, Long.valueOf(value))
                        } else {
                            validator.throwClientErrorException(key, "Funding Contract is mandatory")
                        }
                    }
                    break
                case Enrolment.VET_PURCHASING_CONTRACT_ID.name:
                    action = { Enrolment e ->
                        validateLength(e.id, value, 'vetPurchasingContractID', 12)
                        e.vetPurchasingContractID = trimToNull(value)
                    }
                    break
                case Enrolment.VET_FUNDING_SOURCE_STATE_ID.name:
                    action = { Enrolment e ->
                        validateLength(e.id, value, 'vetFundingSourceStateID', 3)
                        e.vetFundingSourceStateID = trimToNull(value)
                    }
                    break
                default:
                    validator.throwClientErrorException(key, "Unsupported attribute")
            }
        }
        action
    }
}
