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
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.export.avetmiss.AvetmissExportResult

/**
 * AVETMISS export for units of competency - also known as file 060.
 */
@CompileStatic
class Avetmiss060Factory extends AvetmissFactory {

    private static final String ISH = "ISH"

    Avetmiss060Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss060Line createLine(Outcome outcome) {
        def line = new Avetmiss060Line(jurisdiction)

        // ------------------
        // module identifier p52
        // if accredited, must be national accredited module code

        // ------------------
        // module name p54

        if (outcome.getModule()) {
            line.setIdentifier(outcome.getModule().getNationalCode())
            line.setName(outcome.getModule().getTitle())
        } else {
            if (outcome.getEnrolment()) {
                line.setIdentifier(ISH + outcome.getEnrolment().getCourseClass().getCourse().getId().toString())
                def course = outcome.getEnrolment().getCourseClass().getCourse()
                line.setName(String.format("%s %s", course.getCode(), course.getName()))
            } else {
                line.setIdentifier(ISH + outcome.getPriorLearning().getId().toString())
                line.setName(outcome.getPriorLearning().getTitle())
            }
        }

        // ------------------
        // module field of education identifier p55
        line.setFoe("129999")
        if (outcome.getModule()) {
            if (outcome.getModule().getFieldOfEducation()) {
                line.setFoe(outcome.getModule().getFieldOfEducation())
            }
        } else if (outcome.getEnrolment()?.getCourseClass()?.getCourse()?.getFieldOfEducation()?.length() >= 4) {
            line.setFoe(outcome.getEnrolment().getCourseClass().getCourse().getFieldOfEducation())
        }

        // ------------------
        // VET flag p123
        if (outcome.getEnrolment()) {
            line.setVET(outcome.getEnrolment().getCourseClass().getCourse().getIsVET())
        } else {
            line.setVET(true)
        }

        // ------------------
        // nominal hours p61
        // 0000-9999
        line.setNominalHours(0)
        if (outcome.getModule()) {
            if (outcome.getModule().getNominalHours()) {
                line.setNominalHours(outcome.getModule().getNominalHours().intValue())
            }
        } else if (outcome.getEnrolment()?.getCourseClass()?.getCourse()?.getReportableHours()) {
            line.setNominalHours(outcome.getEnrolment().getCourseClass().getCourse().getReportableHours().intValue())
        }

        result.avetmiss060Lines.putIfAbsent(line.identifier, line)
        return line

    }
    
}
