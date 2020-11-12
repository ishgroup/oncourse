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
import ish.common.types.AvetmissStudentDisabilityType
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.export.avetmiss.AvetmissExportResult

/**
 * AVETMISS export for students (disability) - also known as file 090.
 */
@CompileStatic
class Avetmiss090Factory extends AvetmissFactory {

    Avetmiss090Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss090Line createLine(Student student) {
        def line = new Avetmiss090Line(jurisdiction)

        // ------------------
        // client identifier p9
        // Unique per college.
        line.setIdentifier(student.getStudentNumber().toString())

        // ------------------
        // disability type identifier p31
        switch (student.getDisabilityType()) {
            case AvetmissStudentDisabilityType.HEARING:
                line.setDisabilityType(11)
                break
            case AvetmissStudentDisabilityType.PHYSICAL:
                line.setDisabilityType(12)
                break
            case AvetmissStudentDisabilityType.INTELLECTUAL:
                line.setDisabilityType(13)
                break
            case AvetmissStudentDisabilityType.LEARNING:
                line.setDisabilityType(14)
                break
            case AvetmissStudentDisabilityType.MENTAL:
                line.setDisabilityType(15)
                break
            case AvetmissStudentDisabilityType.BRAIN_IMPAIRMENT:
                line.setDisabilityType(16)
                break
            case AvetmissStudentDisabilityType.VISION:
                line.setDisabilityType(17)
                break
            case AvetmissStudentDisabilityType.MEDICAL_CONDITION:
                line.setDisabilityType(18)
                break
            case AvetmissStudentDisabilityType.OTHER:
                line.setDisabilityType(19)
                break
            default:
                line.setDisabilityType(99)
        }

        result.avetmiss090Lines.putIfAbsent(line.identifier, line)
        return line

    }

}
