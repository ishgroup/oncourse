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
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.export.avetmiss.AddressParser
import ish.oncourse.server.export.avetmiss.AvetmissExportResult

/**
 * AVETMISS export for sites - also known as file 020.
 */
@CompileStatic
class Avetmiss020Factory extends AvetmissFactory {

    Avetmiss020Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss020Line createLine(Site site) {
        def line = new Avetmiss020Line(jurisdiction)

        // ------------------
        // training organisation delivery location identifier p112
        // unique within the college
        line.setIdentifier(site.getId().toString())

        // ------------------
        // training organisation delivery location name p114
        line.setName(site.getName())

        // ------------------
        // postcode p71
        line.setPostcode(site.getPostcode())

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
        // must be consistent with the postcode field above, so we should just convert postcode -> id
        // NOTE this is not correct for a college which crosses state boundaries *************


        // ------------------
        // address suburb or town or locality p4
        line.setSuburb(site.getSuburb())


        // for virtual sites
        if (site.getPostcode() == null || site.getPostcode().length() == 0) {
            line.setSuburb(PreferenceController.getController().getAvetmissSuburb())
            line.setPostcode(PreferenceController.getController().getAvetmissPostcode())
        }


        // ------------------
        // country identifier
        if (site.getCountry() == null) {
            line.setCountryCode(1101)
        } else {
            line.setCountryCode(site.getCountry().getSaccCode())
            if (!site.getCountry().isAustralia()) {
                line.setPostcode("OSPC")
            }
        }


        // ------------------
        // Special Victorian requirements
        def addressParser = new AddressParser(site.getStreet())

        line.setBuildingName(addressParser.getBuilding())
        line.setUnit(addressParser.getUnit())
        line.setStreetNumber(addressParser.getStreetNumber())
        line.setStreetName(addressParser.getStreetName())

        result.avetmiss020Lines.putIfAbsent(line.identifier, line)
        return line


    }

}
