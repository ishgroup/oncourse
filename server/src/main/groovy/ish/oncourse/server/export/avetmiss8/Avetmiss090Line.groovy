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
class Avetmiss090Line extends AvetmissLine {

    protected int disabilityType

    /**
     * Creates a new instance of AvetmissLine
     */
    Avetmiss090Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // client identifier p9
        // Unique per college.
        append(10, identifier)

        // ------------------
        // disability type identifier p31
        append(2, disabilityType)

        return toString()
    }

    void setDisabilityType(int disabilityType) {
        this.disabilityType = disabilityType
    }
}
