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
import ish.common.types.EnrolmentStatus
import ish.oncourse.types.FundingStatus
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.OutcomeDao
import static ish.oncourse.server.api.v1.function.OutcomeFunctions.STATUS_MAP
import static ish.oncourse.server.api.v1.function.OutcomeFunctions.toCayenneOutcome
import static ish.oncourse.server.api.v1.function.OutcomeFunctions.toRestOutcome
import static ish.oncourse.server.api.v1.function.OutcomeFunctions.validateOutcomeFields
import static ish.oncourse.server.api.v1.function.OutcomeFunctions.validateOutcomeStartEndDates
import ish.oncourse.server.api.v1.model.ClassFundingSourceDTO
import ish.oncourse.server.api.v1.model.DeliveryModeDTO
import ish.oncourse.server.api.v1.model.OutcomeDTO
import ish.oncourse.server.api.v1.model.OutcomeStatusDTO
import static ish.oncourse.server.api.v1.model.OutcomeStatusDTO.NOT_SET
import static ish.oncourse.server.api.v1.model.OutcomeStatusDTO.SATISFACTORILY_COMPLETED_81_
import static ish.oncourse.server.api.v1.model.OutcomeStatusDTO.WITHDRAWN_OR_NOT_SATISFACTORILY_COMPLETED_82_
import static ish.oncourse.server.api.validation.EntityValidator.validateLength
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.LocalDate

class OutcomeApiService extends EntityApiService<OutcomeDTO, Outcome, OutcomeDao> {

    @Inject
    private ModuleDao moduleDao

    @Inject EnrolmentDao enrolmentDao

    @Override
    Class<Outcome> getPersistentClass() {
        return Outcome
    }

    @Override
    OutcomeDTO toRestModel(Outcome outcome) {
        toRestOutcome(outcome)
    }

    @Override
    Outcome toCayenneModel(OutcomeDTO outcomeDTO, Outcome outcome) {
        toCayenneOutcome(moduleDao, enrolmentDao, outcomeDTO, outcome)
    }

    @Override
    void validateModelBeforeSave(OutcomeDTO outcomeDTO, ObjectContext context, Long id) {

        Outcome outcome = null
        if (id != null) {
            outcome = entityDao.getById(context, id)
        }

        validateOutcomeFields(validator, outcomeDTO, id)
        validateOutcomeStartEndDates(validator, outcomeDTO, id)

        if (outcomeDTO.moduleId != null && !moduleDao.getById(context, outcomeDTO.moduleId)) {
            validator.throwClientErrorException(id, 'moduleId', "Module with id=$outcomeDTO.moduleId doesn't exist.")
        }

        if (outcomeDTO.enrolmentId != null) {
            Enrolment outcomeEnrolment = enrolmentDao.getById(context, outcomeDTO.enrolmentId)

            if (!outcomeEnrolment) {
                validator.throwClientErrorException(id, 'moduleId', "Enrolment with id=$outcomeDTO.enrolmentId doesn't exist.")
            }
            if (outcomeDTO.moduleId == null && outcomeEnrolment.outcomes.find{it.id != id && it.module?.id == null}) {
                validator.throwClientErrorException(id, 'moduleId', 'Enrolment should contain only one non-VET outcome.')
            }
            boolean isDuplicateModuleFound = outcomeEnrolment.outcomes?.find { outcomeDTO.moduleId != null && outcomeDTO.moduleId == it.module?.id && id != it.id} != null
            if (isDuplicateModuleFound) {
                validator.throwClientErrorException(id, 'moduleId', 'This module is already part of different outcome.')
            }
            boolean hasEnrolmentVetOutcomes = outcomeEnrolment.outcomes?.find{(id == null || it.id != id) && it.module != null} != null

            if (outcomeEnrolment.outcomes != null && outcomeEnrolment.outcomes.size() > 0 && ((id == null && outcomeDTO.moduleId != null && !hasEnrolmentVetOutcomes) || (id == null && outcomeDTO.moduleId == null && hasEnrolmentVetOutcomes))) {
                validator.throwClientErrorException(id, 'moduleId', 'VET and non-VET outcomes cannot be mixed.')
            }
        } else if (!outcomeDTO.isPriorLearning) {
            validator.throwClientErrorException(id, 'isPriorLearning', 'Outcome must be related to enrolment or prior learning')
        }

        if (outcome != null && !outcome.certificateOutcomes.findAll {!it.certificate.revokedOn}.empty) {
            if ((outcome.module == null && outcomeDTO.moduleId != null) || outcome.module.id != outcomeDTO.moduleId) {
                validator.throwClientErrorException(id, 'moduleId', 'Module cannot be changed for outcome used in certificate')
            }
            validateStatus(outcome, outcomeDTO.status)
        }

    }

    void validateStatus(Outcome outcome, OutcomeStatusDTO status) {

        if (status == null) {
            validator.throwClientErrorException(outcome.id, 'status', "Status is empty or don't match a list of valid values")
        }

        if (outcome.module == null && !(status in [NOT_SET, SATISFACTORILY_COMPLETED_81_, WITHDRAWN_OR_NOT_SATISFACTORILY_COMPLETED_82_])) {
            validator.throwClientErrorException(outcome.id, 'status', "Can not change status for Outcome Id: ${outcome.id}. " +
                    "Non vet outcome can has only: ${NOT_SET.toString()}, ${SATISFACTORILY_COMPLETED_81_.toString()}, ${WITHDRAWN_OR_NOT_SATISFACTORILY_COMPLETED_82_.toString()} choices")
        }

        if (outcome.certificateOutcomes.certificate.find { it.printedOn && it.revokedOn == null } && outcome.status != STATUS_MAP.getByValue(status)) {
            validator.throwClientErrorException(outcome.id, 'status', "Can not change status for Outcome Id:${outcome.id}. Status cannot be changed for outcome used in printed certificate")
        }
    }

    @Override
    void validateModelBeforeRemove(Outcome outcome) {
        if (outcome.certificateOutcomes.find { it.certificate.revokedOn == null }) {
            validator.throwClientErrorException(outcome.id, 'id', 'Cannot delete outcome used in certificate.')
        }
        if (outcome.fundingUploadOutcomes.find { [FundingStatus.SUCCESS, FundingStatus.EXPORTED].contains(it.fundingUpload.status) }) {
            validator.throwClientErrorException(outcome.id, 'id', 'For audit reasons, you cannot delete outcomes linked to a successful AVETMISS export. ' +
                    'Instead, mark the outcomes as \'do not report\' or set an appropriate outcome status.')
        }
        if (outcome.enrolment && !(outcome.enrolment?.status in [EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED]) && outcome.enrolment?.outcomes?.size() == 1 ) {
            validator.throwClientErrorException(outcome.id, 'id', 'Enrolment must have at least one outcome.')
        }
    }

    void remove(Outcome outcome, ObjectContext context) {
        context.deleteObjects(outcome.certificateOutcomes)
        context.deleteObject(outcome)
    }

    Closure getAction(String key, String value) {

        Closure action = null

        switch (key) {
            case Outcome.START_DATE.name:
                action = { Outcome o ->
                    LocalDate startDate = LocalDateUtils.stringToValue(value)
                    if (o.endDate != null && startDate.isAfter(o.endDate)) {
                        validator.throwClientErrorException(o.id, "startDate", "Can not change start date for Outcome Id:$o.id. Outcome start date should be defore outcome end date.")
                    } else {
                        o.startDate = startDate
                        o.startDateOverridden = true
                    }
                }
                break
            case Outcome.END_DATE.name:
                action = { Outcome o ->
                    LocalDate endDate = LocalDateUtils.stringToValue(value)
                    if (o.startDate != null && endDate.isBefore(o.startDate)) {
                        validator.throwClientErrorException(o.id, "endDate", "Can not change end date for Outcome Id:$o.id. Outcome end date should be after outcome start date.")
                    } else {
                        o.endDate = endDate
                        o.endDateOverridden = true
                    }
                }
                break
            case Outcome.STATUS.name:
                action = { Outcome o ->
                    OutcomeStatusDTO dtoStatus = OutcomeStatusDTO.fromValue(value)
                    validateStatus(o, dtoStatus)
                    o.status = STATUS_MAP.getByValue(dtoStatus)
                }
                break
            case Outcome.FUNDING_SOURCE.name:
                action = { Outcome o ->
                    o.fundingSource = ClassFundingSourceDTO.fromValue(value)?.dbType
                }
                break
            case Outcome.VET_FUNDING_SOURCE_STATE_ID.name:
                action = { Outcome o ->
                    validateLength(o.id, value, 'vetFundingSourceStateID', 12)
                    o.vetFundingSourceStateID = trimToNull(value)
                }
                break
            case Outcome.VET_PURCHASING_CONTRACT_ID.name:
                action = { Outcome o ->
                    validateLength(o.id, value, 'vetPurchasingContractID', 12)
                    o.vetPurchasingContractID = trimToNull(value)
                }
                break
            case Outcome.VET_PURCHASING_CONTRACT_SCHEDULE_ID.name:
                action = { Outcome o ->
                    validateLength(o.id, value, 'vetPurchasingContractScheduleID', 3)
                    o.vetPurchasingContractScheduleID = trimToNull(value)
                }
                break
            case Outcome.DELIVERY_MODE.name:
                action = { Outcome o ->
                    o.deliveryMode = DeliveryModeDTO.fromValue(value)?.dbType
                }
                break
            case Outcome.REPORTABLE_HOURS.name:
                action = { Outcome o ->
                    o.reportableHours = value as BigDecimal
                }
                break
            default:
                validator.throwClientErrorException(key, "Unsupported attribute")
        }

        return action
    }
}
