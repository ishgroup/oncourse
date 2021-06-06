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

package ish.oncourse.server.api.v1.function.avetmiss

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.common.types.AttendanceType
import ish.common.types.OutcomeStatus
import ish.oncourse.server.cayenne.AssessmentClass

import static ish.common.types.OutcomeStatus.*
import ish.common.types.UsiStatus
import ish.oncourse.server.api.v1.model.AvetmissExportOutcomeDTO
import ish.oncourse.server.api.v1.model.AvetmissExportOutcomeCategoryDTO
import ish.oncourse.server.api.v1.model.AvetmissExportOutcomeStatusDTO
import ish.oncourse.server.api.v1.model.AvetmissExportTypeDTO
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome

import java.time.LocalDate

/**
 * Builds a list of AvetmissExportOutcomeDTO records which are returned to the client
 * These show counts for different outcome and enrolments statuses in a preview
 * before the real export is run
 */
@CompileStatic
class AvetmissExportPreviewBuilder {

    private static final List<OutcomeStatus> NOT_FINAL_STATUSES = [
            STATUS_NOT_SET, //0
            STATUS_ASSESSABLE_CONTINUING_ENROLMENT, //70
            STATUS_NO_RESULT_QLD, //90
            STATUS_WA_NOT_YET_STARTED //105
    ]

    private Collection<Outcome> outcomes

    AvetmissExportPreviewBuilder(Collection<Outcome> outcomes) {
        this.outcomes = outcomes
    }

    List<AvetmissExportOutcomeDTO> build() {
        List<AvetmissExportOutcomeDTO> result = []
        LocalDate now = LocalDate.now()

        Collection<Enrolment> inProgressEnrolments = [] //but enrolments contain outcomes with final status

        def futureOutcomes = outcomes.findAll { it.startDate > now || it.status == STATUS_WA_NOT_YET_STARTED }
        if (!futureOutcomes.empty) {
            result += createFromOutcomes(futureOutcomes,
                    AvetmissExportOutcomeCategoryDTO.NOT_YET_STARTED,
                    AvetmissExportOutcomeStatusDTO.START_IN_FUTURE)

            def futureEnrolments = futureOutcomes*.enrolment.findAll().findAll { it.courseClass?.startDateTime && it.courseClass.startDateTime > new Date() }

            inProgressEnrolments+= futureOutcomes*.enrolment.findAll().findAll { !(it.courseClass?.startDateTime && it.courseClass.startDateTime > new Date()) }

            result += createFromEnrolments(futureEnrolments,
                    AvetmissExportOutcomeCategoryDTO.NOT_YET_STARTED,
                    AvetmissExportOutcomeStatusDTO.START_IN_FUTURE)
        }

        def pastOutcomes = outcomes.findAll { it.endDate < now && it.status != STATUS_WA_NOT_YET_STARTED }

        if (!pastOutcomes.empty) {

            def pendingOutcomes = pastOutcomes.findAll { [STATUS_NOT_SET, STATUS_ASSESSABLE_CONTINUING_ENROLMENT, STATUS_NO_RESULT_QLD].contains(it.status) }
            if (!pendingOutcomes.empty) {
                def absentOutcomes = pendingOutcomes.findAll { o ->
                    o.enrolment &&
                    (o.enrolment.courseClass.sessions.findAll { s -> s.modules.contains(o.module) }*.attendance.flatten() as List<Attendance>)
                            .findAll { a -> a.student != null && a.student.id == o.enrolment.student.id  && AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON == a.attendanceType }
                }

                if (!absentOutcomes.empty) {
                    result += createFromOutcomes(absentOutcomes,
                            AvetmissExportOutcomeCategoryDTO.NOT_YET_STARTED,
                            AvetmissExportOutcomeStatusDTO.COMMENCED_BUT_ABSENT)
                }

                def attendedPendingOutcomes = pendingOutcomes.findAll { !absentOutcomes.contains(it) }

                if (!attendedPendingOutcomes.empty) {
                    result += createFromOutcomes(attendedPendingOutcomes,
                            AvetmissExportOutcomeCategoryDTO.DELIVERED,
                            AvetmissExportOutcomeStatusDTO.NO_FINAL_STATUS)
                }

                result += createFromEnrolments(pendingOutcomes*.enrolment.findAll(),
                        AvetmissExportOutcomeCategoryDTO.DELIVERED,
                        AvetmissExportOutcomeStatusDTO.NO_FINAL_STATUS)
            }

            def finalOutcomes = pastOutcomes.findAll { !NOT_FINAL_STATUSES.contains(it.status) }
            if (!finalOutcomes.empty) {
                finalOutcomes.groupBy { it.status }.each { k, v ->
                    if (!v.empty) {
                        result += createFromOutcomes(v,
                                AvetmissExportOutcomeCategoryDTO.FINAL_STATUS,
                                toAvetmissExportOutcomeStatus(k))
                    }
                }

                def notIssuedEnrolments = finalOutcomes.findAll { it.certificateOutcomes.empty }*.enrolment.findAll().unique { it.id }

                def finishedEnrolments = notIssuedEnrolments.findAll { it.courseClass?.endDateTime && it.courseClass.endDateTime < new Date() && it.outcomes.findAll { o -> NOT_FINAL_STATUSES.contains(o.status) }.empty }

                inProgressEnrolments += notIssuedEnrolments.findAll { !(it.courseClass?.endDateTime && it.courseClass.endDateTime < new Date() && !it.outcomes.findAll { o -> NOT_FINAL_STATUSES.contains(o.status) }.empty) }

                //prevent clashes between final and in progress enrolments.
                //Put enrolment to 'final block' if it has all outcomes in final status.
                //Even if has outcomes start date in future

                inProgressEnrolments.removeAll(finishedEnrolments)

                if (!finishedEnrolments.empty) {
                    def vetNotIssuedEnrolments = finishedEnrolments.findAll { it.courseClass.course.isVET }
                    if (!vetNotIssuedEnrolments.empty) {
                        def usiVerifiedEnrolments = vetNotIssuedEnrolments.findAll { [UsiStatus.VERIFIED, UsiStatus.EXEMPTION].contains(it.student.usiStatus) }
                        if (!usiVerifiedEnrolments.empty) {
                            result += createFromEnrolments(usiVerifiedEnrolments,
                                    AvetmissExportOutcomeCategoryDTO.ALL_OUTCOMES_FINAL,
                                    AvetmissExportOutcomeStatusDTO.USI_VERIFIED)
                        }
                        def usiNotVerifiedEnrolments = vetNotIssuedEnrolments.findAll { !(it.student.usiStatus == UsiStatus.VERIFIED) }
                        if (!usiNotVerifiedEnrolments.empty) {
                            result += createFromEnrolments(usiNotVerifiedEnrolments,
                                    AvetmissExportOutcomeCategoryDTO.ALL_OUTCOMES_FINAL,
                                    AvetmissExportOutcomeStatusDTO.USI_NOT_VERIFIED)
                        }
                    }

                    def nonVetNotIssuedEnrolments = finishedEnrolments.findAll { !it.courseClass.course.isVET }
                    if (!nonVetNotIssuedEnrolments.empty) {
                        result += createFromEnrolments(nonVetNotIssuedEnrolments,
                                AvetmissExportOutcomeCategoryDTO.ALL_OUTCOMES_FINAL,
                                AvetmissExportOutcomeStatusDTO.NON_VET)
                    }
                }
                def issuedEnrolments = finalOutcomes.findAll { !it.certificateOutcomes.empty }*.enrolment.findAll()
                if (!issuedEnrolments.empty) {
                    result += createFromEnrolments(issuedEnrolments,
                            AvetmissExportOutcomeCategoryDTO.ISSUED,
                            AvetmissExportOutcomeStatusDTO.CERTIFICATE_SOA_ISSUED)
                }
            }
        }

        def currentOutcomes = outcomes.findAll { it.startDate <= now  && it.endDate >= now && it.status != STATUS_WA_NOT_YET_STARTED }
        if (!currentOutcomes.empty) {

            Collection<Collection<Outcome>> collections = currentOutcomes.split { o -> o.submissions.empty }
            def (notAssessed, commenced) = [collections[0], collections[1]]
            
            if (!notAssessed.empty) {
                result += createFromOutcomes(notAssessed,
                        AvetmissExportOutcomeCategoryDTO.STARTED_NOT_ASSESSED_,
                        AvetmissExportOutcomeStatusDTO.IN_PROGRESS)
            }

            if (!commenced.empty) {
                result += createFromOutcomes(commenced,
                        AvetmissExportOutcomeCategoryDTO.COMMENCED,
                        AvetmissExportOutcomeStatusDTO.IN_PROGRESS)
            }
            
            result += createFromEnrolments((currentOutcomes*.enrolment.findAll() + inProgressEnrolments).unique(),
                    AvetmissExportOutcomeCategoryDTO.COMMENCED,
                    AvetmissExportOutcomeStatusDTO.IN_PROGRESS)
        }

        return result
    }

    private static AvetmissExportOutcomeStatusDTO toAvetmissExportOutcomeStatus(OutcomeStatus status) {
        switch (status) {
            case STATUS_ASSESSABLE_PASS://20
                return AvetmissExportOutcomeStatusDTO.COMPETENCY_ACHIEVED_PASS_20_
            case STATUS_ASSESSABLE_FAIL://30
                return AvetmissExportOutcomeStatusDTO.COMPETENCY_NOT_ACHIEVED_FAIL_30_
            case STATUS_ASSESSABLE_WITHDRAWN://40
                return AvetmissExportOutcomeStatusDTO.WITHDRAWN_40_
            case STATUS_ASSESSABLE_RPL_GRANTED://51
                return AvetmissExportOutcomeStatusDTO.RPL_GRANTED_51_
            case STATUS_ASSESSABLE_CREDIT_TRANSFER://60
                return AvetmissExportOutcomeStatusDTO.CREDIT_TRANSFER_60_
            case STATUS_ASSESSABLE_RPL_NOT_GRANTED://52
                return AvetmissExportOutcomeStatusDTO.RPL_NOT_GRANTED_52_
            case STATUS_ASSESSABLE_RCC_GRANTED://53
                return AvetmissExportOutcomeStatusDTO.RCC_53_
            case STATUS_ASSESSABLE_RCC_NOT_GRANTED://54
                return AvetmissExportOutcomeStatusDTO.RCC_NOT_GRANTED_54_
            case STATUS_ASSESSABLE_WITHDRAWN_INCOMPLETE_DUE_TO_RTO://41
                return AvetmissExportOutcomeStatusDTO.INCOMPLETE_DUE_TO_RTO_CLOSURE_41_
            case STATUS_SUPERSEDED_QUALIFICATION_QLD://65
                return AvetmissExportOutcomeStatusDTO.SUPERSEDED_QUALIFICATION_QLD_65_
            case STATUS_ASSESSABLE_DET_DID_NOT_START://66
                return AvetmissExportOutcomeStatusDTO.DID_NOT_START_NSW_66_
//            case STATUS_ASSESSABLE_CONTINUING_ENROLMENT://70
//                return AvetmissExportOutcomeStatus.CONTINUING_ENROLMENT_70_
            case STATUS_NON_ASSESSABLE_COMPLETED://81
                return AvetmissExportOutcomeStatusDTO.SATISFACTORILY_COMPLETED_81_
            case STATUS_NON_ASSESSABLE_NOT_COMPLETED://82
                return AvetmissExportOutcomeStatusDTO.WITHDRAWN_OR_NOT_SATISFACTORILY_COMPLETED_82_
//            case STATUS_NO_RESULT_QLD://90
//                return AvetmissExportOutcomeStatus.RESULT_NOT_AVAILABLE_90_
            case STATUS_WA_PARTICIPATING_WITH_EVIDENCE://5
                return AvetmissExportOutcomeStatusDTO.PARTICIPATING_BUT_NOT_FINISHED_WITH_EVIDENCE_WA_5_
            case STATUS_WA_PARTICIPATING_WITHOUT_EVIDENCE://55
                return AvetmissExportOutcomeStatusDTO.PARTICIPATING_BUT_NOT_FINISHED_WITH_NO_EVIDENCE_WA_55_
            case STATUS_WA_RCC_GRANTED://15
                return AvetmissExportOutcomeStatusDTO.RCC_GRANTED_WA_15_
            case STATUS_WA_RCC_NOT_GRANTED://16
                return AvetmissExportOutcomeStatusDTO.RCC_NOT_GRANTED_WA_16_
            case STATUS_WA_PROVISIONALLY_COMPETENT://8
                return AvetmissExportOutcomeStatusDTO.PROVISIONALLY_COMPETENT_OFF_THE_JOB_WA_8_
            case STATUS_WA_DISCONTINUED://11
                return AvetmissExportOutcomeStatusDTO.DISCONTINUED_NO_FORMAL_WITHDRAWAL_WA_11_
//            case STATUS_WA_NOT_YET_STARTED://105
//                return AvetmissExportOutcomeStatus.NOT_YET_STARTED_WA_105_
            case SUPERSEDED_SUBJECT://61
                return AvetmissExportOutcomeStatusDTO.SUPERSEDED_SUBJECT_61_
            default:
                throw new IllegalArgumentException('Unexpected status')
        }
    }

    private static AvetmissExportOutcomeDTO createFrom(List<Long> idList, AvetmissExportTypeDTO type, AvetmissExportOutcomeCategoryDTO category, AvetmissExportOutcomeStatusDTO status) {
        new AvetmissExportOutcomeDTO().with { it ->
            it.ids = idList
            it.type = type
            it.category = category
            it.status = status
            it
        }
    }
    private static AvetmissExportOutcomeDTO createFromOutcomes(Collection<Outcome> outcomes, AvetmissExportOutcomeCategoryDTO category, AvetmissExportOutcomeStatusDTO status) {
        return createFrom(outcomes*.id,  AvetmissExportTypeDTO.OUTCOME, category, status)
    }
    private static AvetmissExportOutcomeDTO createFromEnrolments(Collection<Enrolment> enrolments, AvetmissExportOutcomeCategoryDTO category, AvetmissExportOutcomeStatusDTO status) {
        return createFrom(enrolments*.id,  AvetmissExportTypeDTO.ENROLMENT, category, status)
    }
}
