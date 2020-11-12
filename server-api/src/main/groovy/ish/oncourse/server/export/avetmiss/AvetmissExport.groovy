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

package ish.oncourse.server.export.avetmiss

import groovy.transform.CompileStatic

@CompileStatic
enum AvetmissExport {

    AVETMISS_VET("Fee for service VET (non-funded)"),
    AVETMISS_QLD("Queensland", "QLD"),
    AVETMISS_NSW("New South Wales", "NSW"),
    AVETMISS_VIC("Victoria", "VIC"),
    AVETMISS_TAS("Tasmania", "TAS"),
    AVETMISS_ACT("Australian Capital Territory", "ACT"),
    AVETMISS_WA("Western Australia", "WA"),
    AVETMISS_SA("South Australia", "SA"),
    AVETMISS_NT("Northern Territory", "NT"),
    AVETMISS_NO_STATE("No Australian state defined", null),
    AVETMISS_NON_VET("Non VET");

    private String displayName
    private String stateName
    private Boolean secondary = true

    AvetmissExport(String displayName) {
        this(displayName, null, false)
    }

    AvetmissExport(String displayName, String stateName) {
        this(displayName, stateName, true)
    }

    AvetmissExport(String displayName, String stateName, Boolean secondary) {
        this.displayName = displayName
        this.stateName = stateName
        this.secondary = secondary
    }

    String getDisplayName() {
        return displayName
    }

    String getStateName() {
        return stateName
    }

    Boolean isSecondary() {
        return secondary
    }
}




