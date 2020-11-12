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

@CompileStatic
class Avetmiss100Line extends AvetmissLine {

    protected int educationalAchievement

    /**
     * Creates a new instance of AvetmissLine
     */
    Avetmiss100Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // client identifier p9
        // Unique per college.
        append(10, identifier)

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
        append(3, educationalAchievement)

        if (ExportJurisdiction.VIC == this.jurisdiction) {
            // Prior Education Achievement Recognition Identifier
            // A: Australian qualification
            // E: Australian equivalent
            // I: International
            append(1, "A")
        }

        return toString()
    }


    void setEducationalAchievement(int educationalAchievement) {
        this.educationalAchievement = educationalAchievement
    }

}
