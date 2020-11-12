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
class Avetmiss085Line extends AvetmissLine {

    protected String title
    protected String firstName
    protected String lastName
    protected String address1
    protected String address2
    protected String suburb
    protected String postcode
    protected String homePhone
    protected String workPhone
    protected String mobilePhone
    protected String email
    private String buildingName
    private String unit
    private String streetNumber
    private String streetName


    /**
     * Creates a new instance of AvetmissLine
     */
    Avetmiss085Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // client identifier p9
        // Unique per college.
        append(10, identifier)

        // ------------------
        // client title p14
        append(4, title)

        // ------------------
        // client first name p8
        append(40, firstName)

        // ------------------
        // client last name p13
        append(40, lastName)

        // ------------------
        //address building/property name
        append(50, buildingName)

        // ------------------
        //address flat/unit details
        append(30, unit)

        // address street number
        if (streetNumber == null || streetNumber.isEmpty()) {
            append(15, "not specified")
        } else {
            append(15, streetNumber)
        }

        // ------------------
        // address street name
        append(70, streetName)

        // postal delivery box
        append(22, "")

        // ------------------
        // address suburb or town or locality p4
        append(50, suburb)

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
        append(2, AvetmissOutput.avetmissPostCodeID(postcode))

        telephoneAppend(homePhone, postcode)
        telephoneAppend(workPhone, postcode)
        telephoneAppend(mobilePhone, postcode)
        append(80, email)

        // Second email address
        append(80, "")

        return toString()
    }


    void setTitle(String title) {
        this.title = title
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }

    void setAddress1(String address1) {
        this.address1 = address1
    }

    void setAddress2(String address2) {
        this.address2 = address2
    }

    void setSuburb(String suburb) {
        this.suburb = suburb
    }

    void setPostcode(String postcode) {
        this.postcode = postcode
    }

    void setHomePhone(String homePhone) {
        this.homePhone = homePhone
    }

    void setWorkPhone(String workPhone) {
        this.workPhone = workPhone
    }

    void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone
    }

    void setEmail(String email) {
        this.email = email
    }

    void setBuildingName(String buildingName) {
        this.buildingName = buildingName
    }

    void setUnit(String unit) {
        this.unit = unit
    }

    void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber
    }

    void setStreetName(String streetName) {
        this.streetName = streetName
    }
}
