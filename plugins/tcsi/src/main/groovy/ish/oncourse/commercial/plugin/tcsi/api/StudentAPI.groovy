/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi.api

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.Gender
import ish.common.types.StudentCitizenship
import ish.oncourse.commercial.plugin.tcsi.TCSIIntegration
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.scripting.api.EmailService

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT

@CompileDynamic
class StudentAPI extends TCSI_API{
    
    static final String STUDENTS_PATH = TCSIIntegration.BASE_API_PATH + '/students'

    Student student
    Enrolment courseAdmission

    StudentAPI(Enrolment courseAdmission, RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
        this.student = enrolment?.student
        this.courseAdmission = courseAdmission
    }
    
    String createStudent() {
        String message = "Create student"
        client.request(POST, JSON) {
            uri.path = STUDENTS_PATH
            body = getStudentGroup()
            response.success = { resp, result ->
                def student = handleResponce(result as List, message)
                return student['students_uid'].toString()
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    Object getStudentUid() {
        String message = 'Get student by student number'
        client.request(GET, JSON) {
            uri.path = STUDENTS_PATH + "/students-uid/${student.studentNumber}"
            response.success = { resp, result ->
                def studentData = handleResponce(result, message)
                return studentData['student']['students_uid'].toString()
            }
            response.failure =  { resp, body ->
                if (resp.status == 404) {
                    return null
                }
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    void updateStudent(String studentUid) {
        String message = "Update student"
        client.request(PUT, JSON) {
            uri.path = STUDENTS_PATH + "/$studentUid"
            body = getStudentPacket()
            response.success = { resp, result ->
                handleResponce(result, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
            
        if (!hasCitizenship(studentUid)) {
            createCitizenship(studentUid)
        }
        
        //always maintain one disability record (oncourse can has only one)
        String disabilityUid = getDisability(studentUid)
        if (disabilityUid) {
            updateDisability(studentUid, disabilityUid)
        } else {
            createDisability(studentUid)
        }
        
    }

    String hasCitizenship(String studenUid) {
        String  message = "get student's citizenships"
        client.request(GET, JSON) {
            uri.path = STUDENTS_PATH + "/$studenUid/citizenships"
            response.success = { resp, result ->
                def citizenships = handleResponce(result, message)
                if (citizenships && !citizenships.empty) {
                    return citizenships.any {it.citizenship['citizen_resident_code'] == getResidantialCode()}
                } else {
                    return false
                }

            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String createCitizenship(String studentUid) {
        String  message = "create student's citizenships"
        client.request(POST, JSON) {
            uri.path = STUDENTS_PATH + "/$studentUid/citizenships"
            body = getCitizenshipPacket()
            response.success = { resp, result ->
                handleResponce(result, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    String getDisability(String studentUid) {
        String  message = "get student's Disability"
        client.request(GET, JSON) {
            uri.path = STUDENTS_PATH + "/$studentUid/disabilities"
            response.success = { resp, result ->
                def disabilities = handleResponce(result, message)
                if (disabilities && !disabilities.empty) {
                    return disabilities[0].disability['disabilities_uid'].toString()
                } else {
                    return null
                }

            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    String createDisability(String studentUid) {
        String  message = "create student's disability"
        client.request(POST, JSON) {
            uri.path = STUDENTS_PATH + "/$studentUid/disabilities"
            body = JsonOutput.toJson([getDisabilitiData()])
            response.success = { resp, result ->
                handleResponce(result, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String updateDisability(String studentUid,String disabilityUid) {
        String  message = "update student's disability"
        client.request(PUT, JSON) {
            uri.path = STUDENTS_PATH + "/$studentUid/disabilities/$disabilityUid"
            body = JsonOutput.toJson(getDisabilitiData())
            response.success = { resp, result ->
                handleResponce(result, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
        @CompileDynamic
    private getStudentPacket() {
        def studentPacket  = [
                'correlation_id' : "student_packet_${System.currentTimeMillis()}",
                'student' : getStudentData()
        ]
        return JsonOutput.toJson(studentPacket)
    }

    @CompileDynamic
    private  Map<String, Object> getStudentData() {
        Map<String, Object> studentData = [:]
        studentData["student_identification_code"] = student.studentNumber.toString() // E313
        if (student.contact.dateOfBirth) {
            studentData["date_of_birth"] = student.contact.dateOfBirth.format(DATE_FORMAT)   // E314
        }

        studentData["student_family_name"] = student.contact.lastName // E402
        studentData["student_given_name_first"] = student.contact.firstName   // E403

        if (student.contact.middleName) { //E404
            studentData["student_given_name_others"] = student.contact.middleName
        }

        if (student.contact.gender) {  //E315
            switch (student.contact.gender) {
                case Gender.FEMALE:
                    studentData["gender_code"] = "F"
                    break
                case Gender.MALE:
                    studentData["gender_code"] = "M"
                    break
            }
        }

        // E316
        if (student.indigenousStatus) {
            switch (student.indigenousStatus) {
                case AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION:
                    studentData["atsi_code"] = "9"
                    break
                case AvetmissStudentIndigenousStatus.ABORIGINAL:
                    studentData["atsi_code"] = "3"
                    break
                case AvetmissStudentIndigenousStatus.TORRES:
                    studentData["atsi_code"] = "4"
                    break
                case AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES:
                    studentData["atsi_code"] = "4"
                    break
                case AvetmissStudentIndigenousStatus.NEITHER:
                    studentData["atsi_code"] = "2"
                    break
            }
        } else {
            studentData["atsi_code"] = "9"
        }

        // E346
        if (student.countryOfBirth) {
            studentData["country_of_birth_code"] = student.countryOfBirth?.saccCode?.toString()   // E346
        } else {
            studentData["country_of_birth_code"] = "9999"
        }

        // E348
        if (student.language) {
            studentData["language_spoken_at_home_code"] = student.language?.absCode?.toString()
        } else {
            studentData["language_spoken_at_home_code"] = "9999"
        }

        if (student.yearSchoolCompleted) { //E572
            studentData["year_left_school"] = student.yearSchoolCompleted.toString()
        } else {
            studentData["year_left_school"] = '9999'
        }

        if (student.highestSchoolLevel) { //E612

            switch (student.highestSchoolLevel) {
                case AvetmissStudentSchoolLevel.COMPLETED_YEAR_9:
                    studentData["level_left_school"] = '9'
                    break
                case AvetmissStudentSchoolLevel.COMPLETED_YEAR_10:
                    studentData["level_left_school"] = '10'
                    break
                case AvetmissStudentSchoolLevel.COMPLETED_YEAR_11:
                    studentData["level_left_school"] = '11'
                    break
                case AvetmissStudentSchoolLevel.COMPLETED_YEAR_12:
                    studentData["level_left_school"] = '12'
                    break
                default:
                    studentData["level_left_school"] = '99'

            }
        } else {
            studentData["level_left_school"] = '99'
        }

        if (student.contact.country) {
            studentData["term_address_country_code"] = student.contact.country.saccCode.toString() // E661
            studentData["residential_address_country_code"] = student.contact.country.saccCode.toString()   //E658

        }
        studentData["term_address_postcode"] = student.contact.postcode // E319
        studentData["residential_address_postcode"] = student.contact.postcode // E320
        studentData["residential_address_street"] = student.contact.street   // E410
        studentData["residential_address_suburb"] = student.contact.suburb   //E469
        studentData["residential_address_state"] = student.contact.state  // E470

        if (student.contact.tfn) {
            studentData["tfn"] = student.contact.tfn  // E416
        }
        if (student.chessn) {
            studentData["chessn"] = student.chessn // 488
        }
        if (student.usi) {
            studentData["usi"] = student.usi //E584
        }

        return studentData
    }
    
    private String getCitizenshipPacket() {
        Map<String, Object> citizenship = getCitizenshipData()
        citizenship['citizenship']['citizenship_effective_from_date'] = courseAdmission.createdOn.format(DATE_FORMAT)
        return JsonOutput.toJson(citizenship)
    }

    @CompileDynamic
    private Map<String, Object> getCitizenshipData() {
        Map<String, Object> citizenship = [:]
        citizenship['correlation_id'] = "citizenship_${System.currentTimeMillis()}"
        String residentCode = getResidantialCode()
        citizenship['citizenship'] = ['citizen_resident_code': residentCode]
        return citizenship
    }
    
    private String getResidantialCode() {
        String residentCode
        if (student?.citizenship) {
            switch (student.citizenship) {
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
        return residentCode
    }
    
    @CompileDynamic
    private Map<String, Object> getDisabilitiData(){
        Map<String, Object> disabilities = [:]
        disabilities['correlation_id'] = "disability_${System.currentTimeMillis()}"
        String disabilityCode
        if (student?.disabilityType) {
            switch (student?.disabilityType) {
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
        disabilities['disability'] = [:]
        disabilities['disability']['disability_code']= disabilityCode
        disabilities['disability']['disability_effective_from_date'] =  courseAdmission.createdOn.format(DATE_FORMAT)

        return disabilities
    }

    @CompileDynamic
    private String getStudentGroup() {
        Map<String, Object> studentData = getStudentData()

        studentData["citizenships"] = []

        studentData["citizenships"] << getCitizenshipData()

        studentData["disabilities"] = []

        studentData["disabilities"] << getDisabilitiData()

        def result  = [
                'correlation_id' : "studentData_${System.currentTimeMillis()}",
                'student' : studentData
        ]

        return JsonOutput.toJson([result])
    }


}
