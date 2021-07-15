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
import ish.common.types.QualificationType
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.oncourse.server.export.avetmiss.functions.GetQualificationLevelOfEducation
/**
 * AVETMISS export for qualifications - also known as file 030.
 */
@CompileStatic
class Avetmiss030Factory extends AvetmissFactory {


    Avetmiss030Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss030Line createLine(Qualification qualification) {
        def line = new Avetmiss030Line(jurisdiction)

        // ------------------
        // course identifier p21
        // must be uppercase
        // must not be blank
        // must match the NCIS course identifier if part of national training package
        line.setIdentifier(qualification.getNationalCode())

        // ------------------
        // course name p24
        // must match the NCIS course name if part of national training package
        line.setCourseName(String.format("%s %s", qualification.getLevel(), qualification.getTitle()))

        // ------------------
        // nominal hours p61
        // 0000-9999
        // only supervised hours should be counted
        line.setNominalHours((qualification.nominalHours == null) ? 0 : qualification.nominalHours.round(0).intValue())

        // ------------------
        // recognition identifier
        // 11: nationally accredited qualification designed to lead to a
        // qualification specified in a national Training Package
        // 12: nationally recognised accredited course, other than above
        // 13: skill set
        // 14: other courses
        // 15: higher level qual
        // 16: Local skill set
        switch (qualification.getType()) {
            case QualificationType.QUALIFICATION_TYPE:
                line.setRecognitionIdentifier(11)
                break
            case QualificationType.COURSE_TYPE:
                line.setRecognitionIdentifier(12)
                break
            case QualificationType.SKILLSET_TYPE:
                line.setRecognitionIdentifier(13)
                break
            case QualificationType.SKILLSET_LOCAL_TYPE:
                line.setRecognitionIdentifier(16)
                result.has_SKILLSET_LOCAL_TYPE = true
                break
            case QualificationType.HIGHER_TYPE:
                line.setRecognitionIdentifier(15)
                break
            default:
                line.setRecognitionIdentifier(14)
        }

        def level = GetQualificationLevelOfEducation.valueOf(qualification.getLevel().toLowerCase()).get()
        line.setQualLevel(level)

        // ------------------
        // qualification field of education identifier p87
        line.setFieldOfEducation(qualification.getFieldOfEducation())

        line.setHasLocalTypeSkillSet({  -> return result.has_SKILLSET_LOCAL_TYPE})

        // ------------------
        // ANZSCO identifer
        line.setAnzsco(qualification.getAnzsco())

        result.avetmiss030Lines.putIfAbsent(line.identifier, line)
        return line

    }
}
