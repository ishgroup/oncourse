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
import ish.common.types.OutcomeStatus
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.entity.services.CertificateService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.util.LocalDateUtils

import java.time.LocalDate
/**
 * AVETMISS export for completed outcomes - also known as file 130.
 */
@CompileStatic
class Avetmiss130Factory extends AvetmissFactory {

    private CertificateService certificateService

    Avetmiss130Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    void setCertificateService(CertificateService certificateService) {
        this.certificateService = certificateService
    }

    void createLine(Outcome outcome) {
        for (def certificate : outcome.getCertificates()) {
            if (certificate.getIsQualification() && certificate.getIssuedOn() && !certificate.getRevokedOn()) {
                if (result.exportEndDate == null || !certificate.getIssuedOn().isAfter(result.exportEndDate)) {
                    createLine(certificate)
                }
            }
        }

        // special NSW and TAS export of a cluster of outcomes which don't make a full qualification
        if (result.exportEndDate && [ExportJurisdiction.SMART, ExportJurisdiction.TAS].contains(jurisdiction) && outcome.enrolment && outcome.module) {
            def bookingID = outcome.getEnrolment().getCourseClass().getDetBookingId()
            def siteID = outcome.getEnrolment().getCourseClass().getVetCourseSiteID()
            def lastOutcomeEndDate = getLastOutcomeEndDate(outcome.getEnrolment())
            boolean sufficientForQualification = outcome.getEnrolment().getCourseClass().getCourse().getIsSufficientForQualification()

            if ((siteID != null && siteID > 0) || (bookingID != null && bookingID.length() > 0)) {
                if (!containsNotSetOutcome(outcome.getEnrolment()) && lastOutcomeEndDate.isBefore(result.exportEndDate)) {
                    def line = new Avetmiss130Line(jurisdiction)

                    def line30 = new Avetmiss030Factory(result, jurisdiction, preferenceController).createLine(outcome.getEnrolment().getCourseClass().getCourse().getQualification())
                    line.setCourseId(line30.identifier)

                    def line80 = new Avetmiss080Factory(result, jurisdiction, preferenceController).createLine(outcome.getEnrolment().getStudent())
                    line.setClientId(line80.identifier)

                    line.setEndDate(lastOutcomeEndDate)
                    line.setQualificationIssued(sufficientForQualification && !containsBadOutcome(outcome.getEnrolment()))

                    // the following code will not work if the qualification is split across several classes
                    if (jurisdiction == ExportJurisdiction.TAS) {
                        def first_outcome = outcome.enrolment.outcomes.sort{it.startDate}.first()
                        setTasmaniaProperties(line, first_outcome)
                    }

                    line.setIdentifier(line.courseId + line.clientId)

                    result.avetmiss130Lines.putIfAbsent(line.identifier, line)
                }
            }
        }
    }

    Avetmiss130Line createLine(Certificate certificate) {
        def line = new Avetmiss130Line(jurisdiction)

        def line30 = new Avetmiss030Factory(result, jurisdiction, preferenceController).createLine(certificate.getQualification())
        line.setCourseId(line30.identifier)

        def line80 = new Avetmiss080Factory(result, jurisdiction, preferenceController).createLine(certificate.getStudent())
        line.setClientId(line80.identifier)

        line.setEndDate(certificateService.getCompletedOn(certificate))
        line.setStartDate(certificateService.getCommencedOn(certificate))

        line.setQualificationIssued(true)

        line.setCertificateNumber(certificate.getCertificateNumber())
        line.setIssuedDate(LocalDateUtils.valueToDate(certificate.getIssuedOn()))

        if (jurisdiction == ExportJurisdiction.TAS) {
            def first_outcome = certificate.outcomes.sort{it.startDate}.first()
            setTasmaniaProperties(line, first_outcome)
            line.tasmania_programme_status = !certificate.revokedOn && certificate.printedOn ? 10 : 20
        }

        line.setIdentifier(line.courseId + line.clientId)
        result.avetmiss130Lines.putIfAbsent(line.identifier, line)
        return line

    }

    static setTasmaniaProperties(Avetmiss130Line line, Outcome first_outcome) {
        // Client Identifier, Program Identifier, Program Commencement Date and Purchasing Contract Identifier
        line.tasmania_programme_enrolment_identifier = first_outcome.enrolment.student.studentNumber.toString() +
                first_outcome.enrolment?.courseClass?.course?.qualification?.nationalCode ?: "" +
                first_outcome.enrolment?.courseClass?.startDateTime?.format("ddMMyyyy") +
                first_outcome.vetPurchasingContractID
        line.commencement_date = first_outcome.startDate
        if (first_outcome.startDate > LocalDate.now()) {
            line.tasmania_programme_status = 85
        } else if (first_outcome.status == OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN) {
            line.tasmania_programme_status = 40
        }
        // ongoing training is the default until we issue a certificate
        line.tasmania_programme_status = 30
        line.setEndDate(LocalDate.of(9999, 1, 1))
    }

    /**
     * Special process for creating certificate before they are issued for Victoria. These certificates are marked as 'not issued' and have no year.
     * Once a real certificate is created, then they should take precedence (since they are created before these fake certificates)
     * @param outcomeList
     */
    void createForVIC(Outcome outcome) {
        if (outcome.getEnrolment() != null && outcome.getEnrolment().getCourseClass().getCourse().getIsSufficientForQualification()) {
            def line = new Avetmiss130Line(jurisdiction)

            def line30 =  new Avetmiss030Factory(result, jurisdiction, preferenceController).createLine(outcome.getEnrolment().getCourseClass().getCourse().getQualification())
            line.setCourseId(line30.identifier)

            def line80 = new Avetmiss080Factory(result, jurisdiction, preferenceController).createLine(outcome.getEnrolment().getStudent())
            line.setClientId(line80.identifier)

            if (outcome.getEnrolment().getCourseClass().getStartDateTime() != null) {
                def startDate = LocalDateUtils.dateToValue(outcome.getEnrolment().getCourseClass().getStartDateTime())
                line.setStartDate(startDate)
            } else {
                // get the first outcome for this enrolment
                line.setStartDate(outcome.getEnrolment().getOutcomes().sort { o -> o.startDate }.first().startDate)
            }

            line.setEndDate(null)
            line.setQualificationIssued(false)

            line.setIdentifier(line.courseId + line.clientId)

            result.avetmiss130Lines.putIfAbsent(line.identifier,line)
        }
    }

    private boolean containsBadOutcome(Enrolment e) {
        for (def o : e.getOutcomes()) {
            if (badOutcomes.contains(o.getStatus())) {
                return true
            }
            if (result.exportEndDate != null && result.exportEndDate.isBefore(o.getEndDate())) {
                return true
            }
        }
        return false
    }

    private boolean containsNotSetOutcome(Enrolment e) {
        for (def o : e.getOutcomes()) {
            if (OutcomeStatus.STATUS_NOT_SET == o.getStatus()) {
                return true
            }
        }
        return false
    }

    private LocalDate getLastOutcomeEndDate(Enrolment e) {
        LocalDate lastDate = null
        for (def o : e.getOutcomes()) {
            def outcomeEndDate = o.getEndDate()
            if (outcomeEndDate != null &&
                    (lastDate == null || outcomeEndDate.isAfter(lastDate))) {
                lastDate = outcomeEndDate
            }
        }
        return lastDate
    }

    void setExportEndDate(LocalDate exportEndDate) {
        this.exportEndDate = exportEndDate
    }

}
