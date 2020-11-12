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
import ish.oncourse.common.ExportJurisdiction

import java.time.LocalDate

@CompileStatic
class Avetmiss120Line extends AvetmissLine {

    private String trainingOrganisationIdentifier
    protected String location
    protected String studentNumber
    protected String moduleIdentifier
    protected String courseId
    protected LocalDate startDate
    protected LocalDate endDate
    protected String deliveryMode
    protected String result
    protected int scheduledHours
    protected String fundingSource
    protected int commencingCourse
    protected String trainingContractId
    protected String vetClientID
    protected Integer studyReason
    protected String fundingSourceState
    protected Integer studentFee
    protected String feeExemption
    protected String purchasingContract
    protected String purchasingContractSchedule
    protected Integer hoursAttended
    protected Boolean trainingPlan
    protected Integer bookingId
    protected String specificProgram
    protected Date courseCommenceDate
    protected Boolean eligibilityExemption
    protected Boolean vetFeeHelp
    protected String industryCode
    protected Boolean fullTime
    private Integer courseSiteId
    private String outcomeId
    private String associatedCourse
    protected LocalDate enrolmentDate
    protected String enrolmentIdentifier
    protected Integer otherFees
    protected String providerABN
    protected String predominantDelivery

    Avetmiss120Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // training organisation identifier
        if (ExportJurisdiction.VIC == this.jurisdiction) {
            append(10, trainingOrganisationIdentifier, '0', true)
        } else {
            append(10, trainingOrganisationIdentifier)
        }

        // ------------------
        // training organisation delivery location identifier unique
        // within the college
        append(10, location)

        // ------------------
        // client identifier p9
        // Unique per college.
        append(10, studentNumber)

        // ------------------
        // module identifier p52
        // must not be blank
        // if accredited, must be national accredited module code
        append(12, moduleIdentifier)

        // ------------------
        // course identifier p21
        // must be uppercase
        // must match the NCIS course identifier if part of national
        // training package
        append(10, courseId)

        // ------------------
        // enrolment activity start date p37
        // must be valid date and must be before end of collection period
        append(startDate)

        // ------------------
        // enrolment activity end date p35
        // must be after start date, must be valid date and must be after start of collection period
        append(endDate)

        // ------------------
        // delivery mode identifier
        //	Internal only
        //	External only
        //	Workplace-based only
        //	Combination of internal and external Combination of internal and workplace-based Combination of external and workplace-based Combination of all modes
        //	Not applicable (RPL or credit transfer)
        append(3, deliveryMode)

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
        append(2, result)

        // ------------------
        // funding source national p40
        // 11 Commonwealth and state general purpose recurrent
        // 13 Commonwealth specific purpose programs
        // 15 State specific purpose programs
        // 20 Domestic full fee-paying client
        // 30 International full fee-paying client
        // 80 Revenue earned from another registered training organisation
        append(2, fundingSource)

        // ------------------
        // commencing course enrolment identifier p15
        // 3 started the qualification this year
        // 4 continuing from a previous year
        // 8 module only enrolment
        append(1, commencingCourse)

        // ------------------
        // training contract identifier - new apprenticeships p108
        append(10, trainingContractId)

        // ------------------
        // client identifier - new apprenticeships p11
        append(10, vetClientID)

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
        if (studyReason == null) {
            append(2, "")
        } else {
            append(2, studyReason)
        }

        // ------------------
        // VET in schools flag
        append(false)

        // -------------------
        // specific funding identifier
        append(10, specificProgram)

        // -------------------
        // school type
        append(2, "")

        // ======================================
        // end mandatory fields
        // ======================================

        if (!Arrays.asList(ExportJurisdiction.PLAIN, ExportJurisdiction.NTVETPP, ExportJurisdiction.AVETARS).contains(jurisdiction)) {
            // ------------------
            // outcome identifier - training organisation
            append(3, outcomeId, ' ')

            // ------------------
            // funding source - state training authority
            append(3, fundingSourceState)

            // ------------------
            // student tuition fee
            append(5, studentFee)

            // ------------------
            // fee exemption/concession type identifier
            append(2, feeExemption)

            // ------------------
            // purchasing contract identifier
            append(12, purchasingContract)

            // ------------------
            // purchasing contract schedule identifier
            append(3, purchasingContractSchedule)

            // ------------------
            // hours attended
            append(4, hoursAttended)

            // ------------------
            // associated course identifier
            append(10, associatedCourse)

            // ------------------
            // scheduled hours
            // 0000-9999 but only allowed to be up to 1500
            append(4, scheduledHours)

            // ------------------
            // predominant delivery mode
            append(1, predominantDelivery)

            switch (jurisdiction) {

                case ExportJurisdiction.NSW:
                    // ------------------
                    // booking ID
                    append(10, bookingId, '0', true)

                    // ------------------
                    // course site ID
                    append(10, courseSiteId, '0', true)

                    // ------------------
                    // Training Plan Developed
                    append(trainingPlan)

                    break

                case ExportJurisdiction.QLD:
                    // ------------------
                    // full time learning option
                    append(fullTime)
                    break

                case ExportJurisdiction.VIC:
                    // ------------------
                    // course commenced date
                    append(courseCommenceDate)

                    // ------------------
                    // eligibility exemption indicator
                    append(eligibilityExemption)

                    // ------------------
                    // VET fee HELP indicator
                    append(vetFeeHelp)

                    // ------------------
                    // Industry code indicator (ANZSIC)
                    append(2, industryCode)

                    // ------------------
                    // Enrolment date
                    append(enrolmentDate)

                    // ------------------
                    // Enrolment identifier
                    append(50, enrolmentIdentifier)

                    // ------------------
                    // Client fees - other
                    append(5, otherFees)

                    // ------------------
                    // Delivering training RTO ABN
                    append(11, providerABN)

            }
        }
        return toString()
    }

    void setTrainingOrganisationIdentifier(String trainingOrganisationIdentifier) {
        this.trainingOrganisationIdentifier = trainingOrganisationIdentifier
    }

    void setLocation(String location) {
        this.location = location
    }

    void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber
    }

    void setModuleIdentifier(String moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier
    }

    void setCourseId(String courseId) {
        this.courseId = courseId
    }

    void setStartDate(LocalDate startDate) {
        this.startDate = startDate
    }

    void setEndDate(LocalDate endDate) {
        this.endDate = endDate
    }

    void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode
    }

    void setResult(String result) {
        this.result = result
    }

    void setScheduledHours(int scheduledHours) {
        this.scheduledHours = scheduledHours
    }

    void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource
    }

    void setCommencingCourse(int commencingCourse) {
        this.commencingCourse = commencingCourse
    }

    void setTrainingContractId(String trainingContractId) {
        this.trainingContractId = trainingContractId
    }

    void setVetClientID(String vetClientID) {
        this.vetClientID = vetClientID
    }

    void setStudyReason(Integer studyReason) {
        this.studyReason = studyReason
    }

    void setFundingSourceState(String fundingSourceState) {
        this.fundingSourceState = fundingSourceState
    }

    void setStudentFee(Integer studentFee) {
        this.studentFee = studentFee
    }

    void setFeeExemption(String feeExemption) {
        this.feeExemption = feeExemption
    }

    void setPurchasingContract(String purchasingContract) {
        this.purchasingContract = purchasingContract
    }

    void setPurchasingContractSchedule(String purchasingContractSchedule) {
        this.purchasingContractSchedule = purchasingContractSchedule
    }

    void setHoursAttended(Integer hoursAttended) {
        this.hoursAttended = hoursAttended
    }

    void setTrainingPlan(Boolean trainingPlan) {
        this.trainingPlan = trainingPlan
    }

    void setBookingId(Integer bookingId) {
        this.bookingId = bookingId
    }

    void setSpecificProgram(String specificProgram) {
        this.specificProgram = specificProgram
    }

    void setCourseCommenceDate(Date courseCommenceDate) {
        this.courseCommenceDate = courseCommenceDate
    }

    void setEligibilityExemption(Boolean eligibilityExemption) {
        this.eligibilityExemption = eligibilityExemption
    }

    void setVetFeeHelp(Boolean vetFeeHelp) {
        this.vetFeeHelp = vetFeeHelp
    }

    void setIndustryCode(String industryCode) {
        this.industryCode = industryCode
    }

    void setFullTime(Boolean fullTime) {
        this.fullTime = fullTime
    }

    void setCourseSiteId(Integer courseSiteId) {
        this.courseSiteId = courseSiteId
    }

    void setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId
    }

    void setAssociatedCourse(String associatedCourse) {
        this.associatedCourse = associatedCourse
    }

    void setEnrolmentDate(LocalDate enrolmentDate) {
        this.enrolmentDate = enrolmentDate
    }

    void setEnrolmentIdentifier(String enrolmentIdentifier) {
        this.enrolmentIdentifier = enrolmentIdentifier
    }

    void setOtherFees(Integer otherFees) {
        this.otherFees = otherFees
    }

    void setProviderABN(String providerABN) {
        this.providerABN = providerABN
    }

    void setPredominantDelivery(String predominantDelivery) {
        this.predominantDelivery = predominantDelivery
    }
}
