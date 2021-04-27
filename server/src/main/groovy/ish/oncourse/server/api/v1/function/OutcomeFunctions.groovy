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

package ish.oncourse.server.api.v1.function

import ish.common.types.OutcomeStatus
import static ish.common.types.OutcomeStatus.*
import ish.oncourse.function.CalculateOutcomeReportableHours
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.v1.model.ClassFundingSourceDTO
import ish.oncourse.server.api.v1.model.DeliveryModeDTO
import ish.oncourse.server.api.v1.model.OutcomeDTO
import ish.oncourse.server.api.v1.model.OutcomeStatusDTO
import ish.oncourse.server.api.validation.EntityValidator
import static ish.oncourse.server.api.validation.EntityValidator.validateLength
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Outcome
import ish.util.LocalDateUtils
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.LocalDate

class OutcomeFunctions {

    static final BidiMap<OutcomeStatus, OutcomeStatusDTO> STATUS_MAP = new BidiMap<OutcomeStatus, OutcomeStatusDTO>() {{
        put(STATUS_NOT_SET, OutcomeStatusDTO.NOT_SET)
        put(STATUS_ASSESSABLE_PASS, OutcomeStatusDTO.COMPETENCY_ACHIEVED_PASS_20_)
        put(STATUS_ASSESSABLE_FAIL, OutcomeStatusDTO.COMPETENCY_NOT_ACHIEVED_FAIL_30_)
        put(STATUS_ASSESSABLE_WITHDRAWN, OutcomeStatusDTO.WITHDRAWN_40_)
        put(STATUS_ASSESSABLE_WITHDRAWN_INCOMPLETE_DUE_TO_RTO, OutcomeStatusDTO.INCOMPLETE_DUE_TO_RTO_CLOSURE_41_)
        put(STATUS_ASSESSABLE_RPL_GRANTED, OutcomeStatusDTO.RPL_GRANTED_51_)
        put(STATUS_ASSESSABLE_RPL_NOT_GRANTED, OutcomeStatusDTO.RPL_NOT_GRANTED_52_)
        put(STATUS_ASSESSABLE_RCC_GRANTED, null)
        put(STATUS_ASSESSABLE_RCC_NOT_GRANTED, null)
        put(STATUS_ASSESSABLE_CREDIT_TRANSFER, OutcomeStatusDTO.CREDIT_TRANSFER_60_)
        put(STATUS_SUPERSEDED_QUALIFICATION_QLD, OutcomeStatusDTO.SUPERSEDED_QUALIFICATION_QLD_65_)
        put(STATUS_ASSESSABLE_DET_DID_NOT_START, OutcomeStatusDTO.DID_NOT_START_NSW_66_SA_)
        put(STATUS_ASSESSABLE_CONTINUING_ENROLMENT, OutcomeStatusDTO.CONTINUING_ENROLMENT_70_)
        put(STATUS_NON_ASSESSABLE_COMPLETED, OutcomeStatusDTO.SATISFACTORILY_COMPLETED_81_)
        put(STATUS_NON_ASSESSABLE_NOT_COMPLETED, OutcomeStatusDTO.WITHDRAWN_OR_NOT_SATISFACTORILY_COMPLETED_82_)
        put(STATUS_NO_RESULT_QLD, OutcomeStatusDTO.RESULT_NOT_AVAILABLE_90_)
        put(STATUS_WA_PARTICIPATING_WITH_EVIDENCE, OutcomeStatusDTO.PARTICIPATING_BUT_STUDIES_NOT_FINISHED_WITH_EVIDENCE_OF_TRAINING_WA_5_)
        put(STATUS_WA_PARTICIPATING_WITHOUT_EVIDENCE, OutcomeStatusDTO.PARTICIPATING_BUT_STUDIES_NOT_FINISHED_WITH_NO_EVIDENCE_OF_TRAINING_WA_55_)
        put(STATUS_WA_RCC_GRANTED, OutcomeStatusDTO.RECOGNITION_OF_CURRENT_COMPETENCIES_RCC_GRANTED_WA_15_)
        put(STATUS_WA_RCC_NOT_GRANTED, OutcomeStatusDTO.RECOGNITION_OF_CURRENT_COMPETENCIES_RCC_NOT_GRANTED_WA_16_)
        put(STATUS_WA_PROVISIONALLY_COMPETENT, OutcomeStatusDTO.PROVISIONALLY_COMPETENT_OFF_THE_JOB_APPRENTICES_ONLY_WA_8_)
        put(STATUS_WA_DISCONTINUED, OutcomeStatusDTO.DISCONTINUED_NO_FORMAL_WITHDRAWAL_AFTER_SOME_PARTICIPATION_WA_11_)
        put(STATUS_WA_NOT_YET_STARTED, OutcomeStatusDTO.NOT_YET_STARTED_WA_105_)
        put(SUPERSEDED_SUBJECT, OutcomeStatusDTO.SUPERSEDED_SUBJECT_61_)
    }}

    static OutcomeDTO toRestOutcome(Outcome outcome) {
        new OutcomeDTO().with { outcomeDTO ->
            outcomeDTO.id = outcome.id
            outcomeDTO.contactId = outcome.enrolment ? outcome.enrolment?.student?.contact?.id : outcome.priorLearning?.student?.contact?.id
            outcomeDTO.enrolmentId = outcome.enrolment?.id
            outcomeDTO.studentName = outcome.studentName
            outcomeDTO.moduleId = outcome.module?.id
            outcomeDTO.moduleCode = outcome.module?.nationalCode
            outcomeDTO.moduleName = outcome.module?.title

            outcomeDTO.trainingPlanStartDate = outcome.trainingPlanStartDate
            outcomeDTO.actualStartDate = outcome.actualStartDate
            outcomeDTO.startDate = outcome.startDate
            outcomeDTO.startDateOverridden = outcome.startDateOverridden

            outcomeDTO.trainingPlanEndDate = outcome.trainingPlanEndDate
            outcomeDTO.actualEndDate = outcome.actualEndDate
            outcomeDTO.endDate = outcome.endDate
            outcomeDTO.endDateOverridden = outcome.endDateOverridden

            outcomeDTO.reportableHours = outcome.reportableHours
            if (outcome.deliveryMode) {
                outcomeDTO.deliveryMode = DeliveryModeDTO.values()[0].fromDbType(outcome.deliveryMode)
            }
            if (outcome.outcomeFundingSource) {
                outcomeDTO.fundingSource = ClassFundingSourceDTO.values()[0].fromDbType(outcome.outcomeFundingSource)
            }
            outcomeDTO.status = STATUS_MAP.get(outcome.status)
            outcomeDTO.hoursAttended = outcome.hoursAttended
            outcomeDTO.vetPurchasingContractID = outcome.outcomeVetPurchasingContractID
            outcomeDTO.vetFundingSourceStateID = outcome.outcomeVetFundingSourceStateID
            outcomeDTO.specificProgramIdentifier = outcome.specificProgramIdentifier
            outcomeDTO.isPriorLearning = outcome.priorLearning != null
            outcomeDTO.hasCertificate = !outcome.certificateOutcomes?.empty
            outcomeDTO.printed = outcome.certificateOutcomes?.certificate?.find { it.printedOn && it.revokedOn == null } != null
            outcomeDTO.createdOn = LocalDateUtils.dateToTimeValue(outcome.createdOn)
            outcomeDTO.modifiedOn = LocalDateUtils.dateToTimeValue(outcome.modifiedOn)
            outcomeDTO.vetPurchasingContractScheduleID = outcome.vetPurchasingContractScheduleID

            outcomeDTO
        }
    }

    static Outcome toCayenneOutcome(ModuleDao moduleDao, EnrolmentDao enrolmentDao, OutcomeDTO outcomeDTO, Outcome outcome) {

        Module module = null
        if (outcomeDTO.moduleId != null) {
            module = moduleDao.getById(outcome.context, outcomeDTO.moduleId)
        }
        outcome.module = module

        if (outcomeDTO.enrolmentId != null) {
            Enrolment enrolment = enrolmentDao.getById(outcome.context, outcomeDTO.enrolmentId)
            outcome.enrolment = enrolment
        }

        outcome.startDateOverridden = outcome.priorLearning != null || outcomeDTO.startDateOverridden
        outcome.startDate = outcome.startDateOverridden ? outcomeDTO.startDate : outcomeDTO.actualStartDate

        outcome.endDateOverridden = outcome.priorLearning != null || outcomeDTO.endDateOverridden
        outcome.endDate = outcome.endDateOverridden ? outcomeDTO.endDate : outcomeDTO.actualEndDate


        if (outcomeDTO.reportableHours != null) {
            outcome.reportableHours = outcomeDTO.reportableHours
        } else {
            outcome.reportableHours = CalculateOutcomeReportableHours.valueOf(outcome).calculate()
        }

        outcome.deliveryMode = outcomeDTO.deliveryMode?.dbType
        outcome.fundingSource = outcomeDTO.fundingSource?.dbType
        if (outcomeDTO.status) {
            outcome.status = STATUS_MAP.getByValue(outcomeDTO.status)
        } else {
            outcome.status = STATUS_NOT_SET
        }
        outcome.hoursAttended = outcomeDTO.hoursAttended
        outcome.vetPurchasingContractID = trimToNull(outcomeDTO.vetPurchasingContractID)
        outcome.vetFundingSourceStateID = trimToNull(outcomeDTO.vetFundingSourceStateID)
        outcome.specificProgramIdentifier = trimToNull(outcomeDTO.specificProgramIdentifier)
        outcome.vetPurchasingContractScheduleID = trimToNull(outcomeDTO.vetPurchasingContractScheduleID)
        outcome
    }

    static void validateOutcomeStartEndDates(EntityValidator validator, OutcomeDTO outcomeDTO, Long id) {
        LocalDate startDate = outcomeDTO.startDate
        LocalDate endDate = outcomeDTO.endDate

        if (startDate != null && endDate != null && (outcomeDTO.endDateOverridden || outcomeDTO.startDateOverridden)) {
            if (endDate.isBefore(startDate)) {
                validator.throwClientErrorException(id, 'endDate', 'End date can not be before the start date.')
            }
        }
    }

    static void validateOutcomeFields(EntityValidator validator, OutcomeDTO outcomeDTO, Long id) {
        if (!outcomeDTO.status) {
            validator.throwClientErrorException(id, 'status', 'Status is required')
        }

        validateLength(id, trimToEmpty(outcomeDTO.vetFundingSourceStateID), 'vetFundingSourceStateID', 12)
        validateLength(id, trimToEmpty(outcomeDTO.vetPurchasingContractID), 'vetPurchasingContractID', 12)
        validateLength(id, trimToEmpty(outcomeDTO.vetPurchasingContractScheduleID), 'vetPurchasingContractScheduleID', 3)
        validateLength(id, trimToEmpty(outcomeDTO.specificProgramIdentifier), 'specificProgramIdentifier', 10)
    }

    static boolean isVetOutcome(Outcome o) {
        o.module != null
    }
}
