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

import java.time.LocalDate

@CompileStatic
class Avetmiss080Line extends AvetmissLine {

    protected Integer highestSchoolLevel
    protected String name
    protected LocalDate birthDate
    protected int indigenous
    protected String postcode
    protected String gender
    protected Integer languageAtHome
    protected Boolean priorEducation
    protected int country
    protected Boolean stillAtSchool
    protected String suburb
    protected Integer labourForce
    protected Boolean disabilityFlag
    protected String uniqueStateLearnerIdentifier
    protected String usi
    protected String buildingName
    protected String unit
    protected String streetNumber
    protected String streetName
    protected String clientIndustryEmployment
    protected String clientOccupationIdentifier

    protected String ish_avetmiss_tasmania_industry
    protected String ish_avetmiss_tasmania_occupation

    /**
     * Creates a new instance of AvetmissLine
     */
    Avetmiss080Line(ExportJurisdiction jurisdiction) {
        super(jurisdiction)
    }

    @Override
    String export() {
        // ------------------
        // client identifier p9
        // Unique per college.
        append(10, identifier)

        // ------------------
        // name for encryption p59
        // scrambled name
        // standard mentions 'banking level encryption' but doesn't say what
        // that means
        append(60, name)

        // ------------------
        // highest school level completed p43
        append(2, highestSchoolLevel)

        // ------------------
        // sex p94
        // "F", "M" or "@"
        append(1, gender)

        // ------------------
        // date of birth p26
        if (birthDate == null) {
            append(8, "@@@@@@@@")
        } else {
            append(birthDate)
        }

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
        // indigenous status identifier p46
        // 1 (aboriginal)
        // 2 (Torres Strait)
        // 3 (Aboriginal and Torres Strait)
        // 4 (neither)
        // @ (not stated)
        if (indigenous == 0) {
            append(1, "@")
        } else {
            append(1, indigenous)
        }

        // ------------------
        // Main Language other than English Spoken at Home Identifier p50
        // 0000-9999 Australian standard classification of language code
        // (Aust Bureau of Stats: 1267.0)
        append(4, languageAtHome)

        // ------------------
        // labour force status identifer p48
        // 01 (full time)
        // 02 (part time)
        // 03 (self employed, not employing others)
        // 04 (employer)
        // 05 (employed unpaid in family business)
        // 06 (unemployed seeking full time)
        // 07 (unemployed seeking part time)
        // 08 (not employed, not seeking)
        // @@ (not stated)
        if (labourForce == null) {
            append(2, "@@")
        } else {
            append(2, labourForce)
        }

        // ------------------
        // country identifier p19
        // 0000-9999 Aust Bureau of Stats 1269.0
        if (country == 0) {
            append(4, "@@@@")
        } else {
            append(4, country)
        }

        // ------------------
        // disability flag p30
        // Y/N/@
        append(disabilityFlag)

        // ------------------
        // prior educational achievement flag p75
        // Y/N/@
        // beyond year 12 (sort of)
        append(priorEducation)

        // ------------------
        // at school p7
        // still at secondary school
        append(stillAtSchool)

        // ------------------
        // address suburb or town or locality p4
        append(50, suburb)

        // ------------------
        // unique student identifier
        appendCaseSensitive(10, usi)

        // ------------------
        //state identifier
        append(2, AvetmissOutput.avetmissPostCodeID(postcode))

        // ------------------
        //address building/property name
        append(50, buildingName)

        // ------------------
        //address flat/unit details
        append(30, unit)

        // ------------------
        // address street number
        if (streetNumber == null || streetNumber.isEmpty()) {
            append(15, "not specified")
        } else {
            append(15, streetNumber)
        }

        // ------------------
        // address street name
        append(70, streetName)

        // ------------------
        // survey status
        append(1, "A")

        switch (jurisdiction) {
            case ExportJurisdiction.TAS:
                append(11, "") //Statistical Area Level 1 Identifier
                append(9, "") //Statistical Area Level 2 Identifier

                //Client Industry of Employment
                append(1, ish_avetmiss_tasmania_industry)

                //Client Occupation Identifier
                append(1, ish_avetmiss_tasmania_occupation)
                break

            case ExportJurisdiction.QLD:
                append(10, uniqueStateLearnerIdentifier)
                break

            case ExportJurisdiction.VIC:
                append(11, "") //Statistical Area Level 1 Identifier
                append(9, "") //Statistical Area Level 2 Identifier
                append(9, uniqueStateLearnerIdentifier)
                append(1, clientIndustryEmployment)
                append(1, clientOccupationIdentifier)
                break
        }

        return toString()
    }

    void setHighestSchoolLevel(Integer highestSchoolLevel) {
        this.highestSchoolLevel = highestSchoolLevel
    }

    void setName(String name) {
        this.name = name
    }

    void setGender(String gender) {
        this.gender = gender
    }

    void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate
    }

    void setIndigenous(int indigenous) {
        this.indigenous = indigenous
    }

    void setPostcode(String postcode) {
        this.postcode = postcode
    }

    void setLanguageAtHome(Integer languageAtHome) {
        this.languageAtHome = languageAtHome
    }

    void setPriorEducation(Boolean priorEducation) {
        this.priorEducation = priorEducation
    }

    void setCountry(int country) {
        this.country = country
    }

    void setStillAtSchool(Boolean stillAtSchool) {
        this.stillAtSchool = stillAtSchool
    }

    void setSuburb(String suburb) {
        this.suburb = suburb
    }

    void setLabourForce(Integer labourForce) {
        this.labourForce = labourForce
    }

    void setDisabilityFlag(Boolean disabilityFlag) {
        this.disabilityFlag = disabilityFlag
    }

    void setUniqueStateLearnerIdentifier(String uniqueStateLearnerIdentifier) {
        this.uniqueStateLearnerIdentifier = uniqueStateLearnerIdentifier
    }

    void setUsi(String usi) {
        this.usi = usi
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

    void setClientIndustryEmployment(String clientIndustryEmployment) {
        this.clientIndustryEmployment = clientIndustryEmployment
    }

    void setClientOccupationIdentifier(String clientOccupationIdentifier) {
        this.clientOccupationIdentifier = clientOccupationIdentifier
    }
}
