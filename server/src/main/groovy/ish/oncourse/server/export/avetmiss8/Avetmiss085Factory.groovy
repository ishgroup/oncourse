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
import ish.common.types.Gender
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.export.avetmiss.AddressParser
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import org.apache.commons.lang3.StringUtils
/**
 * AVETMISS export for students (location/postal) - also known as file 085.
 */
@CompileStatic
class Avetmiss085Factory extends AvetmissFactory {

    Avetmiss085Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss085Line createLine(Student student) {
        def line = new Avetmiss085Line(jurisdiction)

        // ------------------
        // client identifier p9
        // Unique per college.
        line.setIdentifier(student.getStudentNumber().toString())

        // ------------------
        // client title p14
        if (student.getContact().getTitle() != null) {
            line.setTitle(student.getContact().getTitle())
        } else {
            switch (student.getContact().getGender()) {
                case Gender.MALE:
                    line.setTitle("Mr")
                    break
                case Gender.FEMALE:
                    line.setTitle("Ms")
                    break
                default:
                    line.setTitle("")
            }
        }

        // ------------------
        // client first name p8
        line.setFirstName(student.getContact().getFirstName())

        // ------------------
        // client last name p13
        line.setLastName(student.getContact().getLastName())

        // ------------------
        // client address line 1
        def address = student.getContact().getStreet()
        String[] addressSplit = new String[2]
        if (address != null) {
            addressSplit = address.split("[\n\r]", 2)
        }
        if (addressSplit.length == 0 || addressSplit[0] == null || addressSplit[0].length() == 0) {
            addressSplit[0] = "NOT PROVIDED"
        }

        line.setAddress1(addressSplit[0])


        // ------------------
        // client address line 2
        // export 50 spaces as we do not keep a second line address
        if (addressSplit.length > 1) {
            line.setAddress2(addressSplit[1])
        }

        // ------------------
        // address suburb or town or locality p4
        if (student.getContact().getSuburb() == null || student.getContact().getSuburb().length() == 0) {
            line.setSuburb("NOT PROVIDED")
        } else {
            line.setSuburb(student.getContact().getSuburb())
        }

        // ------------------
        // postcode p71
        // may be 0000 (unknown)
        // 0001-9999
        // OSPC (overseas)
        // @@@@ (not stated)
        line.setPostcode(student.getContact().getPostcode())

        if (AvetmissOutput.avetmissPostCodeID(student.getContact().getPostcode()) == "99") {
            line.setPostcode(null)
        }

        if (student.getContact().getCountry() != null && !student.getContact().getCountry().isAustralia()) {
            line.setPostcode("OSPC")
        }

        line.setHomePhone(student.getContact().getHomePhone())
        line.setWorkPhone(student.getContact().getWorkPhone())
        line.setMobilePhone(student.getContact().getMobilePhone())
        line.setEmail(student.getContact().getEmail())

        if (ExportJurisdiction.SMART == this.jurisdiction && StringUtils.trimToNull(student.getContact().getEmail()) == null) {
            line.setEmail("notprovided@example.com")
        }

        def addressParser = new AddressParser(student.getContact().getStreet())

        line.setBuildingName(addressParser.getBuilding())
        line.setUnit(addressParser.getUnit())
        line.setStreetNumber(addressParser.getStreetNumber())
        line.setStreetName(addressParser.getStreetName())

        result.avetmiss085Lines.putIfAbsent(line.identifier, line)
        return line

    }

}
