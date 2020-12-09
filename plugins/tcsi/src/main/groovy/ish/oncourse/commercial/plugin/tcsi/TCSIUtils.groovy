/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.Gender
import ish.common.types.StudentCitizenship
import ish.oncourse.server.cayenne.Student


class TCSIUtils {
    static final String DATE_FORMAT='yyyy-MM-dd'

    @CompileDynamic
    static String getStudentData(Student s) {
        Map<String, Object> student = [:]
        student["student_identification_code"] = s.studentNumber.toString() // E313
        if (s.contact.dateOfBirth) {
            student["date_of_birth"] = s.contact.dateOfBirth.format(DATE_FORMAT)   // E314
        }
        student["student_family_name"] = s.contact.lastName // E402
        student["student_given_name_first"] = s.contact.firstName   // E403
        student["residential_address_street"] = s.contact.street   // E410
        student["residential_address_suburb"] = s.contact.suburb   //E469
        student["residential_address_state"] = s.contact.state  // E470
        student["residential_address_postcode"] = s.contact.postcode // E320
        student["term_address_postcode"] = s.contact.postcode // E319

        if (s.contact.country) {
            student["residential_address_country_code"] = s.contact.country.saccCode.toString()   //E658
            student["term_address_country_code"] = s.contact.country.saccCode.toString()   // E661
        }
        
        if (s.contact.tfn) {
            student["tfn"] = s.contact.tfn  // E416
        }
        if (s.chessn) {
            student["chessn"] = s.chessn // 488
        }
        if (s.usi) {
            student["usi"] = s.usi //E584
        }
        
        if (s.contact.gender) {
            switch (s.contact.gender) {
                case Gender.FEMALE:
                    student["gender_code"] = "F"
                    break
                case Gender.MALE:
                    student["gender_code"] = "M"
                    break
            }
        }

        // E316
        if (s.indigenousStatus) {
            switch (s.indigenousStatus) {
                case AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION:
                    student["atsi_code"] = "9"
                    break
                case AvetmissStudentIndigenousStatus.ABORIGINAL:
                    student["atsi_code"] = "3"
                    break
                case AvetmissStudentIndigenousStatus.TORRES:
                    student["atsi_code"] = "4"
                    break
                case AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES:
                    student["atsi_code"] = "4"
                    break
                case AvetmissStudentIndigenousStatus.NEITHER:
                    student["atsi_code"] = "2"
                    break
            }
        } else {
            student["atsi_code"] = "9" 
        }
        
        // E346
        if (s.countryOfBirth) {
            student["country_of_birth_code"] = s.countryOfBirth?.saccCode   // E346
        } else {
            student["country_of_birth_code"] = "9999"
        }
    

        // E347
        if (s.countryOfBirth?.isAustralia()) {
            student["year_of_arrival_in_australia"] = "9998"
        } else {
            student["year_of_arrival_in_australia"] = "9999"
        }

        // E348
        if (s.language) {
            student["language_spoken_at_home_code"] = s.language?.absCode 
        } else {
            student["language_spoken_at_home_code"] = "9999"
        }
        if (s.yearSchoolCompleted) {
            student["year_left_school"] = s.yearSchoolCompleted.toString()
        } else {
            student["year_left_school"] = '9999'
        }

        if (s.highestSchoolLevel) {

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
                    break
                default:
                    student["level_left_school"] = '99'

            }
        } else {
            student["level_left_school"] = '99'
        }

        student["citizenships"] = []
            
        def citizenship = [:]
        citizenship['correlation_id'] = "citizenship_${System.currentTimeMillis()}"
        String residentCode
        if (s.citizenship) {
            switch (s.citizenship) {
                case StudentCitizenship.AUSTRALIAN_CITIZEN:
                   residentCode = "1"   //E358
                    break
                case StudentCitizenship.NEW_ZELAND_CITIZEN:
                   residentCode = "2"   //E358
                    break
                case StudentCitizenship.STUDENT_WITH_PERMANENT_HUMANITARIAN_VISA:
                   residentCode = "8"   //E358
                    break
                case StudentCitizenship.STUDENT_WITH_PERMANENT_VISA:
                   residentCode = "3"  //E358
                    break
                case StudentCitizenship.STUDENT_WITH_TEMPORARY_ENTRY_PERMIT:
                   residentCode = "4"   //E358
                    break
                default:
                   residentCode = "5"   //E358

            }
        } else {
            residentCode = "5"   //E358
        }
        
        citizenship['citizenship'] = ['citizen_resident_code': residentCode]
        student["citizenships"] << citizenship

        student["disabilities"] = []
        def disability = [:]
        disability['correlation_id'] = "disability_${System.currentTimeMillis()}"
        String disabilityCode
        if (s.disabilityType) {
            switch (s.disabilityType) {
                case AvetmissStudentDisabilityType.HEARING:
                    disabilityCode = "11"
                    break
                case AvetmissStudentDisabilityType.PHYSICAL:
                    disabilityCode = "12"
                    break
                case AvetmissStudentDisabilityType.INTELLECTUAL:
                    disabilityCode = "13"
                    break
                case AvetmissStudentDisabilityType.LEARNING:
                    disabilityCode = "14"
                    break
                case AvetmissStudentDisabilityType.MENTAL:
                    disabilityCode = "15"
                    break
                case AvetmissStudentDisabilityType.BRAIN_IMPAIRMENT:
                    disabilityCode = "16"
                    break
                case AvetmissStudentDisabilityType.VISION:
                    disabilityCode = "17"
                    break
                case AvetmissStudentDisabilityType.MEDICAL_CONDITION:
                    disabilityCode = "18"
                    break
                case AvetmissStudentDisabilityType.OTHER:
                    disabilityCode = "19"
                    break
                default:
                    disabilityCode = "99" // E615
            }
        } else {
            disabilityCode = "99" // E615
        }
        disability['disability'] = ['disability_code' : disabilityCode] 
        student["disabilities"] << disability

        def studentData  = [
                'correlation_id' : "studentData_${System.currentTimeMillis()}",
                'student' : student
        ]

        return JsonOutput.toJson([studentData])
        
        
    }



    @CompileDynamic
    static String testStudent() {
        Map<String, Object> student = [:]
        student["student_identification_code"] = '123'
        student["date_of_birth"] = "1991-07-20"
        student["student_family_name"] = "Kravchenko"
        student["student_given_name_first"] = "Artyom"
        student["residential_address_street"] = "30-34 Wilson St"
        student["residential_address_suburb"] = "NEWTOWN"
        student["residential_address_state"] = "NSW"
        student["residential_address_country_code"] = "9111"    
        student["residential_address_postcode"] = "2042"
        student["tfn"] = '123212234'
//        student["chessn"] = '123212234'
        student["usi"] = '2222222222'
        student["gender_code"] = "F" //E315
        student["atsi_code"] = "3" // E316
      
        student["country_of_birth_code"] = "9111" // E346

        student["year_of_arrival_in_australia"] = "9999"    // E347

        student["language_spoken_at_home_code"] = "1201" // E348
        student["year_left_school"] = '2009'

        student["level_left_school"] = '11'


        student["term_address_postcode"] = "2042"   // E319
        student["term_address_country_code"] = "9111"// E661

        student["citizenships"] = []
        def citizenship = [:]
        citizenship['correlation_id'] = "citizenship_${System.currentTimeMillis()}"
        String residentCode ="1"
        citizenship['citizenship'] = ['citizen_resident_code': residentCode]
        student["citizenships"] << citizenship

        student["disabilities"] = []
        def disability = [:]
        disability['correlation_id'] = "disability_${System.currentTimeMillis()}"
        String disabilityCode = "15"
        disability['disability'] = ['disability_code' : disabilityCode]
        student["disabilities"] << disability

        
        def studentData  = [
                'correlation_id' : "studentData_${System.currentTimeMillis()}",
                'student' : student
        ]

        return JsonOutput.toJson([studentData])

    }
    

}
