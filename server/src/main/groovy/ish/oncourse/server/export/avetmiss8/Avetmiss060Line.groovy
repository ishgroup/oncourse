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
class Avetmiss060Line extends AvetmissLine {

    protected String name
    protected String foe
    protected boolean isVET
    protected int nominalHours

    Avetmiss060Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // module identifier p52
        // if accredited, must be national accredited module code
        append(12, identifier)

        // ------------------
        // module name p54
        append(100, name)

        // ------------------
        // module field of education identifier p55
        if (foe == null || foe.length() < 4) {
            append(6, "")
        } else {
            append(6, foe, '9')
        }

        // ------------------
        // VET flag p123
        append(isVET)

        // ------------------
        // nominal hours p61
        // 0000-9999
        append(4, nominalHours)


        return toString()
    }

    void setName(String name) {
        this.name = name
    }

    void setFoe(String foe) {
        this.foe = foe
    }

    void setVET(boolean VET) {
        isVET = VET
    }

    void setNominalHours(int nominalHours) {
        this.nominalHours = nominalHours
    }

}
