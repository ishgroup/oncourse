/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi

import groovy.transform.CompileDynamic
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.StudentCitizenship
import ish.math.Money
import ish.oncourse.server.cayenne.Student

import java.time.LocalDate

class TCSIUtils {

    @CompileDynamic
    static Map<String, Object> getStudentData(Student s) {
        Map<String, Object> student = [:]
        student["student_identification_code"] = format(s.studentNumber, 10) // E313
        student["date_of_birth"] = format(s.contact.dateOfBirth)    // E314
        student["student_family_name"] = format(s.contact.lastName, 40) // E402
        student["student_given_name_first"] = format(s.contact.firstName, 40)   // E403
        student["residential_address_street"] = format(s.contact.street, 255)   // E410
        student["residential_address_suburb"] = format(s.contact.suburb, 48)    //E469
        student["residential_address_state"] = format(s.contact.state, 3)   // E470
        if (s.contact.country?.saccCode) {
            student["residential_address_country_code"] = format(s.contact.country?.saccCode)    //E658
        }
        student["residential_address_postcode"] = format(s.contact.postcode, 4) // E320
        student["tfn"] = format(s.contact.tfn, 10)  // E416
        student["chessn"] = format(s.chessn, 10) // 488
        student["usi"] = format(s.usi) //E584
        student["gender_code"] = "F" //E315
        if (s.contact.isMale) {
            student["gender_code"] = "M"
        }

        student["atsi_code"] = "9" // E316
        switch (s.indigenousStatus) {
            case AvetmissStudentIndigenousStatus.ABORIGINAL:
                student["atsi_code"] = "3"
                break
            case AvetmissStudentIndigenousStatus.TORRES:
                student["atsi_code"] = "4"
                break
            case AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES:
                student["atsi_code"] = "5"
                break
            case AvetmissStudentIndigenousStatus.NEITHER:
                student["atsi_code"] = "2"
                break
        }
        student["country_of_birth_code"] = format(s.countryOfBirth?.saccCode)   // E346

        student["year_of_arrival_in_australia"] = "9999"    // E347

        if (s.countryOfBirth.isAustralia() ) {
            student["year_of_arrival_in_australia"] = "9998"
        }
        student["language_spoken_at_home_code"] = format(s.language?.absCode) // E348
        student["year_left_school"] = '9999'

        student["level_left_school"] = '99'
        switch (s.highestSchoolLevel) {
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_9:
                student["level_left_school"] = '9'
                break
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_10:
                student["level_left_school"] = '10'
                break
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_11:
                student["level_left_school"] = '11'
                break
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_12:
                student["level_left_school"] = '12'
        }

        student["term_address_postcode"] = student["residential_address_postcode"]   // E319
        student["term_address_country_code"] = student["residential_address_country_code"]   // E661

        student["citizenships"] = []
        def citizenship = [:]
        citizenship [ "citizen_resident_code" ] = "5"   //E358
        switch (s.citizenship) {
            case StudentCitizenship.AUSTRALIAN_CITIZEN:
                citizenship [ "citizen_resident_code" ] = "1"   //E358
                break
            case StudentCitizenship.NEW_ZELAND_CITIZEN:
                citizenship [ "citizen_resident_code" ] = "2"   //E358
                break
            case StudentCitizenship.STUDENT_WITH_PERMANENT_HUMANITARIAN_VISA:
                citizenship [ "citizen_resident_code" ] = "8"   //E358
                break
            case StudentCitizenship.STUDENT_WITH_PERMANENT_VISA:
                citizenship [ "citizen_resident_code" ] = "3"  //E358
                break
            case StudentCitizenship.STUDENT_WITH_TEMPORARY_ENTRY_PERMIT:
                citizenship [ "citizen_resident_code" ] = "4"   //E358
                break
        }
        student["citizenships"] << citizenship

        student["disabilities"] = []
        def disabilities = [:]
        disabilities["disability_code"] = "99" // E615
        switch(s.disabilityType) {
            case AvetmissStudentDisabilityType.HEARING:
                disabilities["disability_code"] = "11"
                break
            case AvetmissStudentDisabilityType.PHYSICAL:
                disabilities["disability_code"] = "12"
                break
            case AvetmissStudentDisabilityType.INTELLECTUAL:
                disabilities["disability_code"] = "13"
                break
            case AvetmissStudentDisabilityType.LEARNING:
                disabilities["disability_code"] = "14"
                break
            case AvetmissStudentDisabilityType.MENTAL:
                disabilities["disability_code"] = "15"
                break
            case AvetmissStudentDisabilityType.BRAIN_IMPAIRMENT:
                disabilities["disability_code"] = "16"
                break
            case AvetmissStudentDisabilityType.VISION:
                disabilities["disability_code"] = "17"
                break
            case AvetmissStudentDisabilityType.MEDICAL_CONDITION:
                disabilities["disability_code"] = "18"
                break
            case AvetmissStudentDisabilityType.OTHER:
                disabilities["disability_code"] = "19"
                break
        }
        disabilities["disability_effective_from_date"] = "" // E609
        disabilities["disability_effective_to_date"] = ""    // E610
        student["disabilities"] << disabilities

        return student
    }

    @CompileDynamic
    static String format(Object data, Integer length=0) {
        if (!data) {
            return ""
        }

        if (data instanceof Date) {
            return data ? data.format('yyyy-MM-dd') : "01-01-1901"
        }
        if (data instanceof String) {
            return length < data.length() ? data[0..length] : data
        }
        if (data instanceof BigDecimal) {
            return data.toString()
        }
        if (data instanceof Integer) {
            return data.toString()
        }
        if (data instanceof LocalDate) {
            return data.format("yyyy-MM-dd")
        }
        if (data instanceof Money) {
            return data.toBigDecimal().toString()
        }
        if (data instanceof Long) {
            return data.toString()
        }
        throw new IllegalArgumentException()
    }

}
