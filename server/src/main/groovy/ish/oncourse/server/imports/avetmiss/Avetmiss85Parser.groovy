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

package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileDynamic
import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import static org.apache.commons.lang3.StringUtils.isBlank
import org.apache.logging.log4j.LogManager

@CompileDynamic
class Avetmiss85Parser extends AbstractAvetmissParser {

    static logger = LogManager.logger

    private Map<String, String> result = [
            clientId   : null,
            firstName  : null,
            lastName   : null,
            middleName : null,
            street     : null,
            suburb     : null,
            postcode   : null,
            homePhone  : null,
            workPhone  : null,
            mobilePhone: null,
            email      : null
    ]

    @Override
    Map<String, String> parse() {

        // ------------------
        // client identifier p9
        // Unique per college.
        result.clientId = line.readString(10)

        if (isBlank(result.clientId)) {
            errors.add("AVETMISS-85: record at line ${lineNumber + 1} doesn't contain student client identifier")
        }

        // ------------------
        // client title p14
        line.readString(4) // we don't store this

        // ------------------
        // client first name p8
        result.firstName = line.readString(40)

        // ------------------
        // client last name p13
        result.lastName = line.readString(40)

        def names = service.parseNames("${result.lastName}, $result.firstName", lineNumber)
        result = result << names

        // ------------------
        //address building/property name
        String building = line.readString(50)

        // ------------------
        //address flat/unit details
        String unit = line.readString(30)

        // address street number
        String streetNumber = line.readString(15)
        if ("NOT SPECIFIED".equalsIgnoreCase(streetNumber)) {
            streetNumber = null
        }

        // ------------------
        // address street name
        String streetName = line.readString(70)
        if ("NOT SPECIFIED".equalsIgnoreCase(streetName)) {
            streetName = null
        }

        // postal delivery box
        line.readString(22)

        result.street = [building, unit, streetNumber, streetName]
                .findAll { s -> StringUtils.isNotBlank(s) }.join(", ")

        // ------------------
        // address suburb or town or locality p4
        String suburb = line.readString(50)
        result.suburb = "NOT PROVIDED".equalsIgnoreCase(suburb) ? null : suburb

        // ------------------
        // postcode p71
        // may be 0000 (unknown)
        // 0001-9999
        // OSPC (overseas)
        // @@@@ (not stated)
        result.postcode = line.readString(4)

        // ------------------
        // state identifier p95
        line.readString(2) // ignore state identifier

        result.homePhone = line.readString(20)
        result.workPhone = line.readString(20)
        result.mobilePhone = line.readString(20)
        result.email = line.readString(80)
        return result
    }

    @Override
    def fill(Contact contact) {
        contact.firstName = result.firstName
        contact.lastName = result.lastName
        contact.street = result.street
        contact.suburb = result.suburb
        contact.postcode = result.postcode
        contact.homePhone = result.homePhone
        contact.workPhone = result.workPhone
        contact.mobilePhone = result.mobilePhone
        contact.email = result.email
    }

    static valueOf(InputLine line, Integer lineNumber,
                   ObjectContext context) {
        Avetmiss85Parser result = new Avetmiss85Parser()
        result.line = line
        result.lineNumber = lineNumber
        result.context = context
        result.service = new AvetmissImportService(fileType: "AVETMISS-85",
                context: context,
                errors: result.errors
        )
        return result
    }
}
