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
class Avetmiss010Line extends AvetmissLine {

    //position: 1; length: 10;
    private String trainingOrganisationIdentifier
    //position: 11; length: 100;
    private String trainingOrganisationName
    //blank space
    //position: 111; length: 158;
    //position: 269; length: 60;
    private String contactName
    //position: 329; length: 20;
    private String telephoneNumber
    //position: 349; length: 20;
    private String facsimileNumber
    //position: 369; length: 80;
    private String emailAddress

    private String postCode

    Avetmiss010Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {

        // ------------------
        // training organisation identifier p110
        if (ExportJurisdiction.VIC == this.jurisdiction) {
            append(10, trainingOrganisationIdentifier, '0', true)
        } else {
            append(10, trainingOrganisationIdentifier)
        }

        // ------------------
        // training organisation name p115
        // must match the name registered with NTIS (national training information service)
        append(100, trainingOrganisationName)

        // blank space
        append(158, "")

        // ======================================
        // end mandatory fields
        // ======================================

        if (!Arrays.asList(ExportJurisdiction.PLAIN, ExportJurisdiction.NTVETPP, ExportJurisdiction.AVETARS).contains(jurisdiction)) {

            // ------------------
            // contact name
            append(60, contactName)

            // ------------------
            // telephone
            telephoneAppend(telephoneNumber, postCode)

            // ------------------
            // fax
            telephoneAppend(facsimileNumber, postCode)

            // ------------------
            // email
            append(80, emailAddress)
        }

        if (ExportJurisdiction.VIC == this.jurisdiction) {
            append(20, "onCourse")
            append(80, "info@ish.com.au")
        }

        return toString()
    }

    void setTrainingOrganisationIdentifier(String trainingOrganisationIdentifier) {
        this.trainingOrganisationIdentifier = trainingOrganisationIdentifier
    }

    void setTrainingOrganisationName(String trainingOrganisationName) {
        this.trainingOrganisationName = trainingOrganisationName
    }

    void setContactName(String contactName) {
        this.contactName = contactName
    }

    void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber
    }

    void setFacsimileNumber(String facsimileNumber) {
        this.facsimileNumber = facsimileNumber
    }

    void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress
    }

    void setPostCode(String postCode) {
        this.postCode = postCode
    }
}
