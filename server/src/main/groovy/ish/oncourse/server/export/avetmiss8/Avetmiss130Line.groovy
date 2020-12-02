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
import ish.oncourse.server.PreferenceController
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate

@CompileStatic
class Avetmiss130Line extends AvetmissLine {

    protected String courseId
    protected String clientId
    protected LocalDate endDate
    protected LocalDate startDate
    protected Date issuedDate
    protected Long certificateNumber
    protected boolean qualificationIssued

    protected LocalDate commencement_date
    protected String tasmania_programme_enrolment_identifier

    Avetmiss130Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // training organisation identifier
        // uppercase
        if (ExportJurisdiction.VIC == this.jurisdiction) {
            append(10, PreferenceController.getController().getAvetmissID(), '0', true)
        } else if (ExportJurisdiction.QLD == jurisdiction && StringUtils.isNotBlank(PreferenceController.getController().getAvetmissQldIdentifier())) {
            append(10, PreferenceController.getController().getAvetmissQldIdentifier())
        } else {
            append(10, PreferenceController.getController().getAvetmissID())
        }

        // ------------------
        // course identifier
        // must be uppercase
        // must not be blank
        // must match the NTIS course identifier if part of national training package
        append(10, courseId)

        // ------------------
        // client identifier
        // Unique per college.
        append(10, clientId)

        // ------------------
        // program completed
        append(endDate)

        // ------------------
        // qualification issued flag
        append(qualificationIssued)

        // ------------------
        // parchment issued
        append(issuedDate)

        // ------------------
        // parchment number
        append(25, certificateNumber)

        switch (jurisdiction) {

            case ExportJurisdiction.VIC:
                //Program (Course) Commencement Date
                append(startDate)
                break

            case ExportJurisdiction.TAS:
                //Income Contingent Loan Indicator
                //Since we have no onCourse customers in Tasmania offering Diploma, this is always N
                append(1, "N")

                append(commencement_date)

                append(50, tasmania_programme_enrolment_identifier)

                //Program Status Identifier
                // since onCourse does not create certificate records for the 130 export until the qualification is complete, this will always be 10
                append(2, "10")

                //Client Resource Fee
                // We will always report these fees in the 120
                append(5, 0)

                //Client Tuition Fee
                // We will always report these fees in the 120
                append(5, 0)
        }

        return toString()
    }

    void setCourseId(String courseId) {
        this.courseId = courseId
    }

    void setClientId(String clientId) {
        this.clientId = clientId
    }

    void setEndDate(LocalDate endDate) {
        this.endDate = endDate
    }

    void setStartDate(LocalDate startDate) {
        this.startDate = startDate
    }

    void setQualificationIssued(boolean qualificationIssued) {
        this.qualificationIssued = qualificationIssued
    }

    void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate
    }

    void setCertificateNumber(Long certificateNumber) {
        this.certificateNumber = certificateNumber
    }

}
