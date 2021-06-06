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

package ish.oncourse.server.export.avetmiss8

import groovy.transform.CompileStatic
import ish.common.types.ClassFundingSource
import ish.common.types.DeliveryMode
import ish.common.types.OutcomeStatus
import ish.common.types.StudyReason
import ish.common.types.VETFeeExemptionType
import ish.math.Money
import ish.math.MoneyRounding
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.oncourse.server.export.avetmiss.functions.GetPredominantDeliveryMode

import ish.util.LocalDateUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.Month
/**
 * AVETMISS export for outcomes - also known as file 120.
 */
@CompileStatic
class Avetmiss120Factory extends AvetmissFactory {


    Avetmiss120Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss120Line createLine(Outcome outcome) {
        LocalDate yearStartDate = null
        if (result.exportEndDate != null) {
            yearStartDate = LocalDate.of(result.exportEndDate.getYear(), Month.JANUARY, 1)  // the first day of the year
        }

        if (result.adminSite == null) {
            throw new IllegalStateException("120 export: Administration site is missing.")
        }

        def line = new Avetmiss120Line(jurisdiction)

        def toi = ExportJurisdiction.QLD == jurisdiction && StringUtils.isNotBlank(preferenceController.getAvetmissQldIdentifier()) ?
                preferenceController.getAvetmissQldIdentifier() : preferenceController.getAvetmissID()
        line.setTrainingOrganisationIdentifier(toi)

        // ------------------
        // training organisation delivery location identifier p112 unique
        // within the college
        Site site
        try {
            site = outcome.getEnrolment().getCourseClass().getRoom().getSite()
            // if site is virtual then exporting info of admin site
            if (site.getIsVirtual()) {
                site = result.adminSite
            }
        } catch (Exception ignored) {
            site = result.adminSite
        }
        def line20 = new Avetmiss020Factory(result, jurisdiction, preferenceController).createLine(site)
        line.setLocation(line20.identifier)

        // ------------------
        // client identifier p9
        // Unique per college.
        def s = outcome.getEnrolment() ? outcome.getEnrolment().getStudent() : outcome.getPriorLearning().getStudent()
        def line80 = new Avetmiss080Factory(result, jurisdiction, preferenceController).createLine(s)
        line.setStudentNumber(line80.identifier)

        // ------------------
        // module identifier p52
        // must not be blank
        // if accredited, must be national accredited module code
        def line60 = new Avetmiss060Factory(result, jurisdiction, preferenceController).createLine(outcome)
        line.setModuleIdentifier(line60.identifier)

        String courseId = null
        // ------------------
        // course identifier p21
        // must be uppercase
        // must match the NCIS course identifier if part of national
        // training package
        if (outcome.getModule()) {
            if (outcome.getEnrolment()) {
                def c = outcome.getEnrolment().getCourseClass().getCourse()
                if (jurisdiction in [ExportJurisdiction.QLD, ExportJurisdiction.NSW, ExportJurisdiction.SMART] || c.getIsSufficientForQualification()) {
                    if (c.getQualification() != null) {
                        courseId = c.getQualification().getNationalCode()
                        line.setCourseId(courseId)
                        new Avetmiss030Factory(result, jurisdiction, preferenceController).createLine(c.getQualification())
                    }
                }
            } else {
                def pl = outcome.getPriorLearning()
                if (jurisdiction in [ExportJurisdiction.QLD, ExportJurisdiction.NSW] || pl.getQualification()) {
                    if (pl.getQualification()) {
                        courseId = pl.getQualification().getNationalCode()
                        line.setCourseId(courseId)
                        new Avetmiss030Factory(result, jurisdiction, preferenceController).createLine(pl.getQualification())
                    }
                }
            }
        }

        // ------------------
        // enrolment activity start date p37
        // must be valid date and must be before end of collection period
        line.setStartDate(outcome.getStartDate())

        // ------------------
        // enrolment activity end date p35
        // must be after start date, must be valid date and must be after start of collection period
        line.setEndDate(outcome.getEndDate())

        // ------------------
        // delivery mode identifier p 28
        // 10: college/campus based
        // 20: online or remote
        // 30: employment based
        // 40: other
        // 90: N/A
        // 90 is only valid where outcome identifier is 50 or 60

        def deliveryMode = outcome.getDeliveryMode()

        if (deliveryMode == null) {
            line.setDeliveryMode("YNN")
        } else {
            switch (deliveryMode) {
                case DeliveryMode.CLASSROOM_AND_ONLINE:
                    line.setDeliveryMode("YYN")
                    break
                case DeliveryMode.ONLINE:
                    line.setDeliveryMode("NYN")
                    break
                case DeliveryMode.WORKPLACE:
                    line.setDeliveryMode("NNY")
                    break
                case DeliveryMode.CLASSROOM_AND_WORKSPACE:
                    line.setDeliveryMode("YNY")
                    break
                case DeliveryMode.ONLINE_AND_WORKSPACE:
                    line.setDeliveryMode("NYY")
                    break
                case DeliveryMode.CLASSROOM_ONLINE_AND_WORKSPACE:
                    line.setDeliveryMode("YYY")
                    break
                case DeliveryMode.OTHER:
                case DeliveryMode.NA:
                    line.setDeliveryMode("NNN")
                    break
                case DeliveryMode.CLASSROOM:
                    line.setDeliveryMode("YNN")
                    break
                default:
                    line.setDeliveryMode("YNN")
                    break
            }
        }

        line.setPredominantDelivery(GetPredominantDeliveryMode.valueOf(deliveryMode).get())

        if (outcome.getStatus() in [OutcomeStatus.STATUS_ASSESSABLE_RPL_GRANTED, OutcomeStatus.STATUS_ASSESSABLE_RPL_NOT_GRANTED,
                OutcomeStatus.STATUS_ASSESSABLE_RCC_GRANTED, OutcomeStatus.STATUS_ASSESSABLE_RCC_NOT_GRANTED,
                OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER]) {

            line.setDeliveryMode("NNN")
        }

        if (ExportJurisdiction.QLD == jurisdiction && OutcomeStatus.STATUS_NOT_SET == outcome.getStatus()) {
            line.setDeliveryMode("NNN")
        }

        if (ExportJurisdiction.SA == jurisdiction && OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START == outcome.getStatus()) {
            line.setDeliveryMode(null)
        }

        // ------------------
        // outcome identifier national
        // 00: not set
        // 20: compentency achieved
        // 30: competency not achieved
        // 40: withdrawn
        // 50: recognition of prior learning
        // 51: RPL granted
        // 52: RPL not granted
        // 53: RCC granted
        // 54: RCC not granted
        // 60: credit transfer
        // 65: superseded qualification (QLD only)
        // 66: did not start (NSW only)
        // 70: continuing enrolment
        // 81: non-assessable - satisfactory
        // 82: non-assessable - withdrawn
        // 90: result not available (QLD only)
        // @@: did not start (SA only)

        def exportEffectiveDate = result.exportEndDate ?: LocalDate.now()
        def isVetFlag = outcome.getEnrolment() && outcome.getEnrolment().getCourseClass().getCourse().getIsVET()

        line.setResult(outcome.getStatus().getDatabaseValue().toString())

        if (outcome.getStatus() == null ||
                [OutcomeStatus.STATUS_NOT_SET, OutcomeStatus.STATUS_ASSESSABLE_CONTINUING_ENROLMENT].contains(outcome.getStatus())) {
            if (isVetFlag) {
                if (result.defaultOutcome == null) {
                    line.setResult("00")
                } else {
                    line.setResult(result.defaultOutcome.getDatabaseValue().toString())
                    if (result.overriddenEndDate.isAfter(outcome.getEndDate())) {
                        line.setEndDate(result.overriddenEndDate)
                    }
                }
            } else {
                line.setResult(OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED.getDatabaseValue().toString())
            }
        } 
        
        // outcomes which have not ended
        // if no submittions (and 'ignore' flag is false) - export outcomes as future (not yet started)
        // if the 'ignore assessments' flag enabled - export as commenced
        if (exportEffectiveDate.isBefore(outcome.getEndDate())) {
            if (result.ignoreAssessments || !outcome.submissions.empty) {
                line.setResult("70")
            } else {
                line.setResult("85")
                if (exportEffectiveDate.isAfter(outcome.startDate)) {
                    line.setStartDate(result.overriddenEndDate)
                }
            }
        }

        // if outcomes starts in the future
        if (exportEffectiveDate.isBefore(outcome.getStartDate())) {
            line.setResult("85")
        }

        if (OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START == outcome.getStatus() && ExportJurisdiction.SMART == jurisdiction) {
            line.setResult("85")
        }
        if (OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START == outcome.getStatus() && ExportJurisdiction.SA == jurisdiction) {
            line.setResult(null)
        }


        // ------------------
        // scheduled hours p92
        // 0000-9999 but only allowed to be up to 1500
        if (OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER == outcome.getStatus()) {
            line.setScheduledHours(0)
        } else {
            line.setScheduledHours(Math.round(outcome.getReportableHours().floatValue()))
        }

        // ------------------
        // funding source national p40
        // 11 Commonwealth and state general purpose recurrent
        // 13 Commonwealth specific purpose programs
        // 15 State specific purpose programs
        // 20 Domestic full fee-paying client
        // 30 International full fee-paying client
        // 80 Revenue earned from another registered training organisation
        def vfssId = outcome.getVetFundingSourceStateID()
        if (vfssId != null && (vfssId.startsWith("CS") || vfssId.startsWith("TSS"))) {
            line.setFundingSource(ClassFundingSource.COMMONWEALTH_AND_STATE_GENERAL.getAvetmissCode())
        } else if (outcome.getFundingSource() != null) {
            line.setFundingSource(outcome.getFundingSource().getAvetmissCode())
        } else {
            line.setFundingSource(ClassFundingSource.DOMESTIC_FULL_FEE.getAvetmissCode())
        }

        def overseas = Arrays.asList(ClassFundingSource.INTERNATIONAL_FULL_FEE, ClassFundingSource.INTERNATIONAL_OFFSHORE, ClassFundingSource.INTERNATIONAL_ONSHORE)
        if (outcome.getFundingSource() != null && overseas.contains(outcome.getFundingSource())) {
            def line85 = (Avetmiss085Line) result.avetmiss085Lines.get(line.studentNumber)
            line80.setPostcode("OSPC")
            line85.setPostcode("OSPC")
        }

        // ------------------
        // commencing course enrolment identifier p15
        // 3 started the qualification this year
        // 4 continuing from a previous year
        // 8 module only enrolment
        // ------------------
        // The rules are as follows:
        // If the NAT00120 record has no program identifier, but a subject identifier the output is always 8,
        // If the NAT00120 record has a subject identifier that is not a module, the output is always 8. This type of record should never have a program identifier either, so 1st rule applies.
        // If the NAT00120 record has a program identifier AND a subject identifier, the output is always 3 or 4
        // All NAT00120 records for a student with the same program identifier, must always be the same e.g. ALL 3 or ALL 4

        if (courseId == null) {
            line.setCommencingCourse(8)
        } else {
            // get the earliest start date for all outcomes from this student with the same Qualification
            def qualificationLearningStart = getStartDateOfLearningQualification(outcome)

            if (yearStartDate != null && qualificationLearningStart != null && qualificationLearningStart.isBefore(yearStartDate)) {
                line.setCommencingCourse(4)
            } else {
                line.setCommencingCourse(3)
            }
        }

        // ------------------
        // training contract identifier - new apprenticeships p108
        line.setTrainingContractId(outcome.getEnrolment()?.getVetTrainingContractID())

        // ------------------
        // client identifier - new apprenticeships p11
        line.setVetClientID(outcome.getEnrolment()?.getVetClientID())

        // ------------------
        // study reason (not mandatory) - "AVETMISS data element defns Ed1.pdf" p111
        // 01 To get a job
        // 02 To develop my existing business
        // 03 To start my own business
        // 04 To try for a different career
        // 05 To get a better job or promotion
        // 06 It was a requirement of my job
        // 07 I wanted extra skills for my job
        // 08 To get into another course of study
        // 11 Other reasons
        // 12 For personal interest or self-development
        line.setStudyReason(11) // Enrolment.STUDY_REASON_OTHER

        def studyReason = outcome.getEnrolment()?.getStudyReason()
        if (StudyReason.STUDY_REASON_JOB == studyReason) {
            line.setStudyReason(1)
        } else if (StudyReason.STUDY_REASON_DEVELOP_BUSINESS == studyReason) {
            line.setStudyReason(2)
        } else if (StudyReason.STUDY_REASON_START_BUSINESS == studyReason) {
            line.setStudyReason(3)
        } else if (StudyReason.STUDY_REASON_CAREER_CHANGE == studyReason) {
            line.setStudyReason(4)
        } else if (StudyReason.STUDY_REASON_BETTER_JOB == studyReason) {
            line.setStudyReason(5)
        } else if (StudyReason.STUDY_REASON_JOB_REQUIREMENT == studyReason) {
            line.setStudyReason(6)
        } else if (StudyReason.STUDY_REASON_EXTRA_JOB_SKILLS == studyReason) {
            line.setStudyReason(7)
        } else if (StudyReason.STUDY_REASON_FOR_ANOTHER_COURSE == studyReason) {
            line.setStudyReason(8)
        } else if (StudyReason.STUDY_REASON_PERSONAL_INTEREST == studyReason) {
            line.setStudyReason(12)
        }

        if (StringUtils.isBlank(line.courseId)) {
            line.setStudyReason(null)
        }

        // -------------------
        // specific program identifier
        line.setSpecificProgram(outcome.getSpecificProgramIdentifier())

        // ======================================
        // end mandatory fields
        // ======================================

        if (!Arrays.asList(ExportJurisdiction.PLAIN, ExportJurisdiction.NTVETPP, ExportJurisdiction.AVETARS).contains(jurisdiction)) {
            // ------------------
            // outcome identifier - training organisation
            line.setOutcomeId(outcome.getEnrolment() != null ?
                    outcome.getEnrolment().getOutcomeIdTrainingOrg() : outcome.getPriorLearning().getOutcomeIdTrainingOrg())

            // ------------------
            // funding source - state training authority
            line.setFundingSourceState("CSO" == vfssId ? "CSD" : vfssId)

            // ------------------
            // student tuition fee

            // get the total hours for all outcomes related to the same enrolment
            BigDecimal totalHours = new BigDecimal(0)
            if (outcome.getEnrolment() != null) {
                for (def o : outcome.getEnrolment().getOutcomes()) {
                    if (o.getReportableHours() != null
                            && OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER != o.getStatus()
                            && OutcomeStatus.STATUS_ASSESSABLE_RCC_GRANTED != o.getStatus()
                            && OutcomeStatus.STATUS_ASSESSABLE_RCC_NOT_GRANTED != o.getStatus()
                            && OutcomeStatus.STATUS_SUPERSEDED_QUALIFICATION_QLD != o.getStatus()) {
                        totalHours = totalHours.add(o.getReportableHours())
                    }
                }
            }

            Money hourlyFee, fee
            if (totalHours.intValue() == 0) {
                hourlyFee = Money.ZERO
            } else {
                hourlyFee = (outcome.enrolment.invoiceLines*.finalPriceToPayIncTax.inject(Money.ZERO, { a, b -> a.add(b)}) as Money).divide(totalHours)
            }

            switch (jurisdiction) {
                case ExportJurisdiction.NSW:
                case ExportJurisdiction.SMART:
                case ExportJurisdiction.OLIV:
                    fee = Money.ZERO
                    break

                case ExportJurisdiction.VIC:
                    fee = hourlyFee.multiply(100) // hourly fee in cents
                    break

                default:
                    fee = hourlyFee.multiply(outcome.reportableHours) // fee in dollars for this unit
                    def cents = fee.getCents()
                    fee = fee.round(MoneyRounding.ROUNDING_1D)
                    if (cents > 0 && cents < 50) {
                        fee = fee.add(Money.ONE)
                    }
                    break
            }

            // If bad outcome, then set the fee back to 0
            if (outcome.getStatus() in [OutcomeStatus.STATUS_SUPERSEDED_QUALIFICATION_QLD,
                           OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER,
                           OutcomeStatus.STATUS_ASSESSABLE_RCC_GRANTED,
                           OutcomeStatus.STATUS_ASSESSABLE_RCC_NOT_GRANTED]
            ) {
                fee = Money.ZERO
            }

            line.setStudentFee(fee.intValue())


            // ------------------
            // fee exemption/concession type identifier
            if (vfssId != null && (vfssId.startsWith("CS") || vfssId.startsWith("TSS"))) {
                line.setFeeExemption("Y")
            } else {
                switch (jurisdiction) {

                    case ExportJurisdiction.NSW:
                    case ExportJurisdiction.SMART:
                    case ExportJurisdiction.OLIV:
                        if (VETFeeExemptionType.YES == (outcome.getEnrolment() != null ? outcome.getEnrolment().getVetFeeExemptionType() : null)) {
                            line.setFeeExemption("Y")
                        } else {
                            line.setFeeExemption("N")
                        }
                        break

                    case ExportJurisdiction.VIC:

                        try {
                            switch (outcome.getEnrolment().getVetFeeExemptionType()) {
                                case VETFeeExemptionType.NO:
                                    line.setFeeExemption("Z")
                                    break
                                case VETFeeExemptionType.YES:
                                    line.setFeeExemption("Y")
                                    break
                                case VETFeeExemptionType.G:
                                    line.setFeeExemption("G")
                                    break
                                case VETFeeExemptionType.H:
                                    line.setFeeExemption("H")
                                    break
                                case VETFeeExemptionType.M:
                                    line.setFeeExemption("M")
                                    break
                                case VETFeeExemptionType.O:
                                    line.setFeeExemption("O")
                                    break
                                case VETFeeExemptionType.P:
                                    line.setFeeExemption("P")
                                    break
                                case VETFeeExemptionType.V:
                                    line.setFeeExemption("V")
                                    break
                                case VETFeeExemptionType.Z:
                                    line.setFeeExemption("Z")
                                    break
                                case VETFeeExemptionType.UNSET:
                                    line.setFeeExemption("Z")
                                    break
                                default:
                                    line.setFeeExemption("Z")
                                    break
                            }
                        } catch (NullPointerException ignored) {
                            line.setFeeExemption("Z") // switch(null) doesn't work
                        }
                        break

                    case ExportJurisdiction.QLD:

                        if (VETFeeExemptionType.C == outcome.getEnrolment()?.getVetFeeExemptionType()) {
                            line.setFeeExemption("C")
                        } else if (VETFeeExemptionType.N == outcome.getEnrolment()?.getVetFeeExemptionType()) {
                            line.setFeeExemption("N")
                        } else if (fee == Money.ZERO) {
                            line.setFeeExemption("Y")
                        } else if (VETFeeExemptionType.YES == outcome.getEnrolment()?.getVetFeeExemptionType()) {
                            line.setFeeExemption("Y")
                        } else {
                            line.setFeeExemption("N")
                        }

                        break

                    default:
                        if (fee == Money.ZERO) {
                            line.setFeeExemption("Y")
                        } else if (outcome.getEnrolment() && VETFeeExemptionType.YES == outcome.getEnrolment().getVetFeeExemptionType()) {
                            line.setFeeExemption("Y")
                        } else {
                            line.setFeeExemption("N")
                        }
                }
            }


            // ------------------
            // purchasing contract identifier
            line.setPurchasingContract(outcome.getVetPurchasingContractID())

            // ------------------
            // purchasing contract schedule identifier
            line.setPurchasingContractSchedule(outcome.getVetPurchasingContractScheduleID())

            // ------------------
            // hours attended
            line.setHoursAttended(outcome.getHoursAttended())

            // ------------------
            // associated course
            line.setAssociatedCourse(outcome.getEnrolment()?.getAssociatedCourseIdentifier())

            switch (jurisdiction) {

                case ExportJurisdiction.NSW:
                    // ------------------
                    // purchasing contract identifier
                    line.setPurchasingContract("")

                    // ------------------
                    // purchasing contract schedule identifier
                    line.setPurchasingContractSchedule("")

                    // ------------------
                    // booking ID
                    try {
                        line.setBookingId(Integer.parseInt(outcome.getEnrolment().getCourseClass().getDetBookingId()))
                    } catch (Exception ignored) {
                    }

                    // ------------------
                    // course site ID
                    try {
                        line.setCourseSiteId(outcome.getEnrolment().getCourseClass().getVetCourseSiteID())
                    } catch (Exception ignored) {
                    }

                    // ------------------
                    // Training Plan Developed
                    line.setTrainingPlan(outcome.getEnrolment() != null && outcome.getEnrolment().getTrainingPlanDeveloped() != null ?
                            outcome.getEnrolment().getTrainingPlanDeveloped() : false)

                    break

                case ExportJurisdiction.QLD:
                    // ------------------
                    // hours attended
                    if (OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN == outcome.getStatus()) {
                        line.setHoursAttended(outcome.getHoursAttended())
                    } else {
                        line.setHoursAttended(null)
                    }

                    // ------------------
                    // full time learning option
                    if (outcome.getEnrolment() == null || outcome.getEnrolment().getVetIsFullTime() == null) {
                        line.setFullTime(null)
                    } else if (outcome.getEnrolment().getVetIsFullTime()) {
                        line.setFullTime(true)
                    } else {
                        line.setFullTime(false)
                    }
                    break

                case ExportJurisdiction.TAS:
                    // Client Identifier, Program Identifier, Program Commencement Date and Purchasing Contract Identifier
                    line.tasmania_programme_enrolment_identifier = outcome.enrolment?.student?.studentNumber?.toString() ?: "" +
                            outcome.enrolment?.student?.studentNumber?.toString() ?: "" +
                            outcome.enrolment?.courseClass?.course?.qualification?.nationalCode ?: "" +
                            outcome.priorLearning?.qualification?.nationalCode ?: "" +
                            outcome.enrolment?.courseClass?.startDateTime?.format("ddMMyyyy") +
                            outcome.vetPurchasingContractID
                    try {
                        line.ish_avetmiss_tasmania_resourcefee = outcome.enrolment.getCustomFieldValue("ish_avetmiss_tasmania_resourcefee") as int
                    } catch (Exception ignored) {}
                    break

                case ExportJurisdiction.VIC:
                    // ------------------
                    // hours attended
                    if (OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN == outcome.getStatus()) {
                        line.setHoursAttended(outcome.getHoursAttended())
                    }

                    // ------------------
                    // course commenced date
                    if (outcome.getEnrolment() != null && outcome.getEnrolment().getCourseClass().getStartDateTime() != null) {
                        line.setCourseCommenceDate(outcome.getEnrolment().getCourseClass().getStartDateTime())
                    }

                    // ------------------
                    // eligibility exemption indicator
                    if (outcome.getEnrolment() != null && Boolean.TRUE == outcome.getEnrolment().getEligibilityExemptionIndicator()) {
                        line.setEligibilityExemption(true)
                    } else {
                        line.setEligibilityExemption(false)
                    }

                    // ------------------
                    // VET fee HELP indicator
                    if (outcome.getEnrolment() != null && Boolean.TRUE == outcome.getEnrolment().getVetFeeIndicator()) {
                        line.setVetFeeHelp(true)
                    } else {
                        line.setVetFeeHelp(false)
                    }

                    // ------------------
                    // Industry code indicator (ANZSIC)
                    def validCodes = Arrays.asList("LSG", "PSG", "SSG")
                    if (validCodes.contains(outcome.getVetFundingSourceStateID()) && outcome.getEnrolment().getStudentIndustryANZSICCode() != null) {
                        line.setIndustryCode(outcome.getEnrolment().getStudentIndustryANZSICCode().toString())
                    }

                    // ------------------
                    // Enrolment date
                    if (outcome.getEnrolment() != null) {
                        if (outcome.getEnrolment().getCourseClass().getStartDateTime() != null &&
                                outcome.getEnrolment().getCreatedOn().after(outcome.getEnrolment().getCourseClass().getStartDateTime())) {
                            def enrolmentDate = LocalDateUtils.dateToValue(outcome.getEnrolment().getCourseClass().getStartDateTime())
                            line.setEnrolmentDate(enrolmentDate)
                        } else {
                            def enrolmentDate = LocalDateUtils.dateToValue(outcome.getEnrolment().getCreatedOn())
                            line.setEnrolmentDate(enrolmentDate)
                        }
                    } else {
                        line.setEnrolmentDate(outcome.getStartDate())
                    }

                    // ------------------
                    // Enrolment identifier
//				line.setEnrolmentIdentifier(outcome.getEnrolment().getStudent().getStudentNumber()
//						+ outcome.getModule().getNationalCode() + outcome.getStatus()
//						+ outcome.getEnrolment().getCourseClass().getCourse().getCode()
//						+ outcome.getEnrolment().getCourseClass().getCode());
                    // TODO review the above (it throws NPE) and compare to the simpler option below
                    line.setEnrolmentIdentifier(outcome.getId().toString())

                    // ------------------
                    // Client fees - other
                    line.setOtherFees(0)

                    // ------------------
                    // Delivering training RTO ABN
                    line.setProviderABN(preferenceController.getCollegeABN())

                    break

            }

        }

        line.setIdentifier(line.studentNumber + line.moduleIdentifier + line.result + line.startDate)

        result.avetmiss120Lines.putIfAbsent(line.identifier, line)
        return line

    }

    void setOverriddenEndDate(LocalDate overriddenEndDate) {
        this.overriddenEndDate = overriddenEndDate
    }

    void setAdminSite(Site adminSite) {
        this.adminSite = adminSite
    }

    private static LocalDate getStartDateOfLearningQualification(Outcome outcome) {
        Qualification qualification
        Student student

        if (outcome.getEnrolment()) {
            qualification = outcome.getEnrolment().getCourseClass().getCourse().getQualification()
            student = outcome.getEnrolment().getStudent()
        } else {
            qualification = outcome.getPriorLearning().getQualification()
            student = outcome.getPriorLearning().getStudent()
        }

        def relatedOutcomes = student.getEnrolments()
                .findAll { it.getCourseClass().getCourse().getQualification() == qualification }
                *.getOutcomes().flatten() as List<Outcome>

        relatedOutcomes += student.getPriorLearnings()
                .findAll { it.getQualification() == qualification }
                *.getOutcomes().flatten() as List<Outcome>

        return relatedOutcomes.collect { o -> o.startDate }.sort().first()
    }

}
