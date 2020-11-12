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
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class Avetmiss020Line extends AvetmissLine {

    private static final Logger logger = LogManager.getLogger()

    protected String name
    protected String postcode
    protected String suburb
    protected int countryCode
    protected String buildingName
    protected String streetNumber
    protected String streetName
    protected String unit

    /**
     * Creates a new instance of AvetmissLine
     */
    Avetmiss020Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // training organisation identifier p110
        // uppercase
        if (ExportJurisdiction.VIC == this.jurisdiction) {
            append(10, PreferenceController.getController().getAvetmissID(), '0', true)
        } else if (ExportJurisdiction.QLD == jurisdiction && StringUtils.isNotBlank(PreferenceController.getController().getAvetmissQldIdentifier())) {
            append(10, PreferenceController.getController().getAvetmissQldIdentifier())
        } else {
            append(10, PreferenceController.getController().getAvetmissID())
        }

        // ------------------
        // training organisation delivery location identifier p112
        // unique within the college
        append(10, identifier)

        // ------------------
        // training organisation delivery location name p114
        append(100, name)

        // ------------------
        // postcode p71
        // may be 0000 (unknown)
        // 0001-9999
        // OSPC (overseas)
        // @@@@ (not stated)
        if (postcode == null) {
            append(4, "", '@')
        } else {
            append(4, postcode)
        }

        // ------------------
        // state identifier p95
        // 01 NSW
        // 02 VIC
        // 03 QLD
        // 04 SA
        // 05 WA
        // 06 TAS
        // 07 NT
        // 08 ACT
        // 09 other Australian territories
        // 99 other
        append(2, AvetmissOutput.avetmissPostCodeID(postcode))

        // ------------------
        // address suburb or town or locality p4
        append(50, suburb)

        // ------------------
        // country identifier
        append(4, countryCode)

        // ------------------
        // Special Victorian requirements
        if (ExportJurisdiction.VIC == this.jurisdiction) {
            append(50, buildingName)
            append(30, unit)
            append(15, streetNumber)
            append(70, streetName)
        }

        return toString()
    }

    void setName(String name) {
        this.name = name
    }

    void setPostcode(String postcode) {
        this.postcode = postcode
    }

    void setCountryCode(int countryCode) {
        this.countryCode = countryCode
    }

    void setSuburb(String suburb) {
        this.suburb = suburb
    }

    void setBuildingName(String buildingName) {
        this.buildingName = buildingName
    }

    void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber
    }

    void setStreetName(String streetName) {
        this.streetName = streetName
    }

    void setUnit(String unit) {
        this.unit = unit
    }
}
