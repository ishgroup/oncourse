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
class Avetmiss030Line extends AvetmissLine {

    private String courseName
    private int nominalHours

    private Closure<Boolean> hasLocalTypeSkillSet

    private int recognitionIdentifier
    private int qualLevel
    private String fieldOfEducation
    private String anzsco

    /**
     * Creates a new instance of AvetmissLine
     */
    Avetmiss030Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // course identifier p21
        // must be uppercase
        // must not be blank
        // must match the NCIS course identifier if part of national training package
        append(10, identifier)

        // ------------------
        // course name p24
        // must match the NCIS course name if part of national training package
        append(100, courseName)

        // ------------------
        // nominal hours p61
        // 0000-9999
        // only supervised hours should be counted
        append(4, nominalHours)

        if (hasLocalTypeSkillSet.call()) {
            append(2, recognitionIdentifier)

            append(3, qualLevel)

            append(4, fieldOfEducation)

            append(6, anzsco)

            append(true)
        } else {
            //blank space
            append(16, "")
        }

        return toString()
    }

    void setCourseName(String courseName) {
        this.courseName = courseName
    }

    void setNominalHours(int nominalHours) {
        this.nominalHours = nominalHours
    }

    void setRecognitionIdentifier(int recognitionIdentifier) {
        this.recognitionIdentifier = recognitionIdentifier
    }

    void setQualLevel(int qualLevel) {
        this.qualLevel = qualLevel
    }

    void setFieldOfEducation(String fieldOfEducation) {
        this.fieldOfEducation = fieldOfEducation
    }

    void setAnzsco(String anzsco) {
        this.anzsco = anzsco
    }

    void setHasLocalTypeSkillSet(Closure<Boolean> hasLocalTypeSkillSet) {
        this.hasLocalTypeSkillSet = hasLocalTypeSkillSet
    }
}
