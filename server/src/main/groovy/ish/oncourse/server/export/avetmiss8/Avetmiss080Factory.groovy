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
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.Gender
import ish.common.types.UsiStatus
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.export.avetmiss.AddressParser
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate
import java.time.Period
/**
 * AVETMISS export for students - also known as file 080.
 */
@CompileStatic
class Avetmiss080Factory extends AvetmissFactory {


    Avetmiss080Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss080Line createLine(Student student) {
        Integer studentAge = null
        if (result.exportEndDate == null) {
            throw new IllegalStateException("Export 80: Export date is missing.")
        }

        // if student is an orphaned record, ignore it
        if (student.getContact() == null) {
            throw new IllegalStateException("Export 80: Student is missing contact.")
        }

        def line = new Avetmiss080Line(jurisdiction)

        new Avetmiss085Factory(result, jurisdiction, preferenceController).createLine(student)

        // Calculate the student's age

        if (student.getContact().getBirthDate() != null) {
            studentAge = Period.between(student.getContact().getBirthDate(), result.exportEndDate).getYears()
        }

        // ------------------
        // client identifier p9
        // Unique per college.
        line.setIdentifier(student.getStudentNumber().toString())

        // ------------------
        // name for encryption p59
        def nameForEncryption = student.getContact().getLastName() + ", " + student.getContact().getFirstName()
        if (StringUtils.trimToNull(student.getContact().getMiddleName()) != null) {
            nameForEncryption += " " + student.getContact().getMiddleName()
        }
        line.setName(nameForEncryption)

        // ------------------
        // highest school level completed p43
        def highestSchoolLevel = student.getHighestSchoolLevel()
        if (studentAge != null && !AvetmissStudentSchoolLevel.isValid(highestSchoolLevel, studentAge)) {
            line.setHighestSchoolLevel(null)
        } else if (AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL == highestSchoolLevel) {
            line.setHighestSchoolLevel(2)
        } else if (AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW == highestSchoolLevel) {
            line.setHighestSchoolLevel(8)
        } else if (AvetmissStudentSchoolLevel.COMPLETED_YEAR_9 == highestSchoolLevel) {
            line.setHighestSchoolLevel(9)
        } else if (AvetmissStudentSchoolLevel.COMPLETED_YEAR_10 == highestSchoolLevel) {
            line.setHighestSchoolLevel(10)
        } else if (AvetmissStudentSchoolLevel.COMPLETED_YEAR_11 == highestSchoolLevel) {
            line.setHighestSchoolLevel(11)
        } else if (AvetmissStudentSchoolLevel.COMPLETED_YEAR_12 == highestSchoolLevel) {
            line.setHighestSchoolLevel(12)
        } else {
            line.setHighestSchoolLevel(null)
        }

        // ------------------
        // sex p94
        // "F", "M" or "@"
        if (student.getContact().getGender() != null) {
            switch (student.getContact().getGender()) {
                case Gender.FEMALE:
                    line.setGender("F")
                    break
                case Gender.MALE:
                    line.setGender("M")
                    break
                case Gender.OTHER_GENDER:
                    line.setGender("X")
                    break
                default:
                    line.setGender("@")
            }
        } else {
            line.setGender("@")
        }

        // ------------------
        // date of birth p26
        if (studentAge != null && (studentAge < 6 || studentAge > 94)) {
            line.setBirthDate(null)
        } else {
            line.setBirthDate(student.getContact().getBirthDate())
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

        // ------------------
        // indigenous status identifier p46
        // 1 (aboriginal)
        // 2 (Torres Strait)
        // 3 (Aboriginal and Torres Strait)
        // 4 (neither)
        // @ (not stated)

        def indigenousStatus = student.getIndigenousStatus()

        if (indigenousStatus == null) {
            line.setIndigenous(0)
        } else if (indigenousStatus == AvetmissStudentIndigenousStatus.NEITHER) {
            line.setIndigenous(4)
        } else if (indigenousStatus == AvetmissStudentIndigenousStatus.ABORIGINAL) {
            line.setIndigenous(1)
        } else if (indigenousStatus == AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES) {
            line.setIndigenous(3)
        } else if (indigenousStatus == AvetmissStudentIndigenousStatus.TORRES) {
            line.setIndigenous(2)
        } else {
            line.setIndigenous(0)
        }

        // ------------------
        // Main Language other than English Spoken at Home Identifier p50
        // 0000-9999 Australian standard classification of language code
        // (Aust Bureau of Stats: 1267.0)
        try {
            line.setLanguageAtHome(Integer.parseInt(student.getLanguage().getAbsCode()))
        } catch (Exception ignored) {
        }

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

        // ------------------
        // at school p7
        // still at secondary school

        line.setStillAtSchool(student.getIsStillAtSchool())

        if (student.getLabourForceStatus() != null) {
            line.setLabourForce(student.getLabourForceStatus().getDatabaseValue())
            if (line.labourForce == null || line.labourForce < 1 || line.labourForce > 8) {
                line.setLabourForce(null)
            }
        }

        if (Boolean.TRUE == student.getIsStillAtSchool()) {
            line.setLabourForce(8) // cannot be employed
            line.setStillAtSchool(true)
        }

        if (studentAge != null && studentAge < 16) {
            line.setLabourForce(8) // not allowed to work
            line.setStillAtSchool(true)
        }

        // if the student never went to school
        if (line.highestSchoolLevel != null && line.highestSchoolLevel == 2) {
            line.setStillAtSchool(false)
        }

        // people who work full time aren't allowed to be at school
        if (line.labourForce != null && line.labourForce == 1) {
            line.setStillAtSchool(false)
        }

        // ------------------
        // country identifier p19
        // 0000-9999 Aust Bureau of Stats 1269.0
        if (student.getCountryOfBirth() != null && student.getCountryOfBirth().getSaccCode() > 0 && student.getCountryOfBirth().getSaccCode() < 10000) {
            line.setCountry(student.getCountryOfBirth().getSaccCode())
        }

        // ------------------
        // disability flag p30
        // Y/N/@
        if (student.getDisabilityType() == null || student.getDisabilityType() == AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION) {
            line.setDisabilityFlag(null)
        } else if (student.getDisabilityType() == AvetmissStudentDisabilityType.NONE) {
            line.setDisabilityFlag(false)
        } else {
            line.setDisabilityFlag(true)
            new Avetmiss090Factory(result, jurisdiction, preferenceController).createLine(student)
        }

        // ------------------
        // prior educational achievement flag p75
        // Y/N/@
        // beyond year 12 (sort of)
        if (student.getPriorEducationCode() == null ||
                AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION == student.getPriorEducationCode()) {
            line.setPriorEducation(null)
        } else if (studentAge != null && studentAge < 17) {
            line.setPriorEducation(null)
        } else if (AvetmissStudentPriorEducation.NONE == student.getPriorEducationCode()) {
            line.setPriorEducation(false)
        } else {
            line.setPriorEducation(true)
            new Avetmiss100Factory(result, jurisdiction, preferenceController).createLine(student)
        }

        if (line.priorEducation == null && ExportJurisdiction.VIC == this.jurisdiction) {
            line.setPriorEducation(false)
        }

        // ------------------
        // address suburb or town or locality p4
        // ------------------
        // address suburb or town or locality p4
        if (student.getContact().getSuburb() == null || student.getContact().getSuburb().length() == 0) {
            line.setSuburb("NOT PROVIDED")
        } else {
            line.setSuburb(student.getContact().getSuburb())
        }

        def addressParser = new AddressParser(student.getContact().getStreet())

        line.setBuildingName(addressParser.getBuilding())
        line.setUnit(addressParser.getUnit())
        line.setStreetNumber(addressParser.getStreetNumber())
        line.setStreetName(addressParser.getStreetName())

        line.setUniqueStateLearnerIdentifier(student.getUniqueLearnerIndentifier())

        // USI
        switch (student.getUsiStatus()) {
            case UsiStatus.INTERNATIONAL:
                line.setUsi("INTOFF")
                break

            case UsiStatus.EXEMPTION:
                line.setUsi("INDIV")
                break

            default:
                line.setUsi(student.getUsi())
        }

        if (student.getClientIndustryEmployment() != null) {
            line.setClientIndustryEmployment(student.getClientIndustryEmployment().getCode())
        }

        if (student.getClientOccupationIdentifier() != null && student.getClientOccupationIdentifier().getCode() != null) {
            line.setClientOccupationIdentifier(student.getClientOccupationIdentifier().getCode().toString())
        }

        if (this.jurisdiction == ExportJurisdiction.TAS) {
            try {
                line.ish_avetmiss_tasmania_industry = student.contact.getCustomFieldValue("ish_avetmiss_tasmania_industry")
            } catch (MissingPropertyException ignored) {}

            try {
                line.ish_avetmiss_tasmania_occupation = student.contact.getCustomFieldValue("ish_avetmiss_tasmania_occupation")
            } catch (MissingPropertyException ignored) {}
        }

        result.avetmiss080Lines.putIfAbsent(line.identifier, line)
        return line

    }

}
