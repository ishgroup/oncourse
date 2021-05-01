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
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentEnglishProficiency
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.Gender
import ish.oncourse.server.cayenne.Contact
import ish.util.EnumUtil
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager

@CompileDynamic
class Avetmiss80Parser extends AbstractAvetmissParser {
    static logger = LogManager.logger

    private result = [
            clientId           : null,
            firstName          : null,
            lastName           : null,
            middleName         : null,
            gender             : null,
            birthDate          : null,
            postcode           : null,
            suburb             : null,
            language           : null,
            highestSchoolLevel : null,
            yearSchoolCompleted: null,
            indigenousStatus   : null,
            labourForceStatus  : null,
            countryOfBirth     : null,
            disabilityType     : null,
            priorEducationCode : null,
            isStillAtSchool    : null,
            englishProficiency : null,
    ]

    private Avetmiss80Parser() {
    }

    @Override
    /**
     * clientId(1+10),names(11+60), highestSchoolLevel(71+2), yearSchoolCompleted(73+4),
     * gender(77+1),birthDate(78+8),postcode(86+4), indigenousStatus(90+1),language(91+4),
     * labourForceStatus(95+2), countryOfBirth(97+4),disabilityType(101+1),priorEducationCode(102+1)
     * isStillAtSchool(103+1),englishProficiency(104+1), suburb(105+50)
     */
    Map<Object, Object> parse() {

        // ------------------
        // client identifier p9
        // Unique per college.
        result.clientId = line.readString(10)
        if (StringUtils.isBlank(result.clientId)) {
            errors.add("AVETMISS-80: record at line ${lineNumber + 1} doesn't contain student client identifier")
        }

        // ------------------
        // name for encryption p59
        String name = line.readString(60)
        def names = service.parseNames(name, lineNumber)
        result = result << names

        // ------------------
        // highest school level completed p43
        result.highestSchoolLevel = service.parseHighestSchoolLevel(line.readInteger(2))

        // ------------------
        // year highest school level completed p126
        // valid 4 digit year, not in the future
        result.yearSchoolCompleted = line.readInteger(4)

        // ------------------
        // sex p94
        // "F", "M" or "@"
        String gender = line.readString(1)

        result.gender = "M".equals(gender) ? Gender.MALE : "F".equals(gender) ? Gender.FEMALE : "X".equals(gender) ? Gender.OTHER_GENDER : null
        // ------------------
        // date of birth p26
        result.birthDate = line.readLocalDate(8)

        // ------------------
        // postcode p71
        // may be 0000 (unknown)
        // 0001-9999
        // OSPC (overseas)
        // @@@@ (not stated)
        result.postcode = line.readString(4)

        // ------------------
        // indigenous status identifier p46
        // 1 (aboriginal)
        // 2 (Torres Strait)
        // 3 (Aboriginal and Torres Strait)
        // 4 (neither)
        // @ (not stated)
        Integer indStatus = line.readInteger(1)
        if (indStatus != null) {
            result.indigenousStatus =
                    (AvetmissStudentIndigenousStatus) EnumUtil.enumForDatabaseValue(AvetmissStudentIndigenousStatus, indStatus)
        } else {
            result.indigenousStatus = AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION
        }
        // ------------------
        // language spoken at home p50
        // 0000-9999 Australian standard classification of language code
        // (Aust Bureau of Stats: 1267.0)
        result.language = service.getLanguageBy(line.readInteger(4))

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
        Integer labourStatus = line.readInteger(2)
        if (labourStatus != null) {
            result.labourForceStatus =
                    (AvetmissStudentLabourStatus) EnumUtil.enumForDatabaseValue(AvetmissStudentLabourStatus, labourStatus)
        } else {
            result.labourForceStatus = AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION
        }

        // ------------------
        // country identifier p19
        // 0000-9999 Aust Bureau of Stats 1269.0
        result.countryOfBirth = service.getCountryBy(line.readInteger(4))

        // ------------------
        // disability flag p30
        // Y/N/@
        String disabilityType = line.readString(1)
        result.disabilityType = "Y".equals(disabilityType) ? AvetmissStudentDisabilityType.OTHER
                : AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION

        // ------------------
        // prior educational achievement flag p75
        // Y/N/@
        // beyond year 12 (sort of)
        String priorEducationCode = line.readString(1)
        result.priorEducationCode = "Y".equals(priorEducationCode) ? AvetmissStudentPriorEducation.MISC
                : AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION

        // ------------------
        // at school p7
        // still at secondary school
        String stillAtSchool = line.readString(1)
        result.isStillAtSchool = "Y".equals(stillAtSchool) ? Boolean.TRUE : "N".equals(stillAtSchool) ? Boolean.FALSE : null

        // ------------------
        // proficiency in spoken English identifier p79
        // 1 (very well)
        // 2 (well)
        // 3 (not well)
        // 4 (not at all)
        // blank (if language spoken at home is 1201 - English)
        // @ (not stated)
        Integer engProficiency = line.readInteger(1)
        if (engProficiency != null) {
            result.englishProficiency = (AvetmissStudentEnglishProficiency) EnumUtil.enumForDatabaseValue(
                    AvetmissStudentEnglishProficiency, engProficiency)
        } else {
            result.englishProficiency = AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION
        }

        // ------------------
        // address suburb or town or locality p4
        String suburb = line.readString(50)
        result.suburb = "NOT PROVIDED".equalsIgnoreCase(suburb) ? null : suburb
        return result
    }

    @Override
    def fill(Contact contact) {
        contact.firstName = result.firstName
        contact.lastName = result.lastName
        contact.middleName = result.middleName
        contact.gender = result.gender
        contact.birthDate = result.birthDate
        contact.postcode = result.postcode
        contact.suburb = result.suburb
        contact.student.language = result.language
        contact.student.highestSchoolLevel = result.highestSchoolLevel
        contact.student.yearSchoolCompleted = result.yearSchoolCompleted
        contact.student.indigenousStatus = result.indigenousStatus
        contact.student.labourForceStatus = result.labourForceStatus
        contact.student.countryOfBirth = result.countryOfBirth
        contact.student.disabilityType = result.disabilityType
        contact.student.priorEducationCode = result.priorEducationCode
        contact.student.isStillAtSchool = result.isStillAtSchool
        contact.student.englishProficiency = result.englishProficiency
    }

    static valueOf(InputLine line, Integer lineNumber,
                   ObjectContext context) {
        Avetmiss80Parser result = new Avetmiss80Parser()
        result.line = line
        result.lineNumber = lineNumber
        result.context = context
        result.service = new AvetmissImportService(fileType: "AVETMISS-80",
                context: context,
                errors: result.errors
        )
        return result
    }

}
