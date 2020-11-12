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
import ish.common.types.AvetmissStudentPriorEducation
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.export.avetmiss.AvetmissExportResult

/**
 * AVETMISS export for students (prior learning) - also known as file 100.
 */
@CompileStatic
class Avetmiss100Factory extends AvetmissFactory {

    Avetmiss100Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss100Line createLine(Student student) {
        def line = new Avetmiss100Line(jurisdiction)

        // ------------------
        // client identifier p9
        // Unique per college.
        line.setIdentifier(student.getStudentNumber().toString())

        // ------------------
        // prior educational achievement identifier p77
        // 008 Bachelor Degree or Higher Degree level (defined for AVETMISS use only)
        // 410 Advanced Diploma or Associate Degree Level
        // 420 Diploma Level
        // 511 Certificate IV
        // 514 Certificate III
        // 521 Certificate II
        // 524 Certificate I
        // 990 Miscellaneous Education
        switch (student.getPriorEducationCode()) {
            case AvetmissStudentPriorEducation.BACHELOR:
                line.setEducationalAchievement(8)
                break
            case AvetmissStudentPriorEducation.ADVANCED_DIPLOMA:
                line.setEducationalAchievement(410)
                break
            case AvetmissStudentPriorEducation.DIPLOMA:
                line.setEducationalAchievement(420)
                break
            case AvetmissStudentPriorEducation.CERTIFICATE_IV:
                line.setEducationalAchievement(511)
                break
            case AvetmissStudentPriorEducation.CERTIFICATE_III:
                line.setEducationalAchievement(514)
                break
            case AvetmissStudentPriorEducation.CERTIFICATE_II:
                line.setEducationalAchievement(521)
                break
            case AvetmissStudentPriorEducation.CERTIFICATE_I:
                line.setEducationalAchievement(524)
                break
            case AvetmissStudentPriorEducation.MISC:
                line.setEducationalAchievement(990)
                break
            default:
                line.setEducationalAchievement(990)
        }

        result.avetmiss100Lines.putIfAbsent(line.identifier, line)
        return line

    }
    
}
