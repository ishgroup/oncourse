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
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
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
        
        if (s.contact.middleName) { //E404
            student["student_given_name_others"] = s.contact.middleName
        }
        
        if (s.contact.gender) {  //E315
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

        // E348
        if (s.language) {
            student["language_spoken_at_home_code"] = s.language?.absCode
        } else {
            student["language_spoken_at_home_code"] = "9999"
        }
        
        if (s.yearSchoolCompleted) { //E572
            student["year_left_school"] = s.yearSchoolCompleted.toString()
        } else {
            student["year_left_school"] = '9999'
        }

        if (s.highestSchoolLevel) { //E612

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

        if (s.contact.country) { 
            student["term_address_country_code"] = s.contact.country.saccCode.toString() // E661
            student["residential_address_country_code"] = s.contact.country.saccCode.toString()   //E658

        }
        student["term_address_postcode"] = s.contact.postcode // E319
        student["residential_address_postcode"] = s.contact.postcode // E320
        student["residential_address_street"] = s.contact.street   // E410
        student["residential_address_suburb"] = s.contact.suburb   //E469
        student["residential_address_state"] = s.contact.state  // E470
        
        if (s.contact.tfn) {
            student["tfn"] = s.contact.tfn  // E416
        }
        if (s.chessn) {
            student["chessn"] = s.chessn // 488
        }
        if (s.usi) {
            student["usi"] = s.usi //E584
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
    static String getCourseData(Course c) {
        Map<String, Object> course = [:]

        course["course_code"] = c.code
        course["course_name"] = c.name
        course["course_of_study_load"] = c.reportableHours
        course["standard_course_duration"] = c.reportableHours
        List<CourseClass> cortedClasses = c.courseClasses.findAll { it.startDateTime }.sort { it.startDateTime }
        Date startDate = cortedClasses.empty ? new Date() : cortedClasses[0].startDateTime
        course["course_effective_from_date"] = startDate.format(DATE_FORMAT)
      
        def courseData  = [
                'correlation_id' : "courseData_${System.currentTimeMillis()}",
                'course' : course
        ]

        return JsonOutput.toJson([courseData])
        
    }
    
    static String testCourse() {
        Map<String, Object> course = [:]

        course["course_code"] = 'BSB40807'
        course["course_name"] = 'TEST name'
        course["course_of_study_load"] = 1
        course["standard_course_duration"] = 1
        course["course_effective_from_date"] = new Date().format(DATE_FORMAT)

        def courseData  = [
                'correlation_id' : "courseData_${System.currentTimeMillis()}",
                'course' : course
        ]
        return JsonOutput.toJson([courseData])

    }
    

}
