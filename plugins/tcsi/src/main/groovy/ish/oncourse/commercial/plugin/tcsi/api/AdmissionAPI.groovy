/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi.api

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.CourseClassAttendanceType
import ish.common.types.EnrolmentStatus
import ish.common.types.OutcomeStatus
import ish.common.types.StudyReason
import ish.oncourse.commercial.plugin.tcsi.TCSIIntegration
import ish.oncourse.commercial.plugin.tcsi.TCSIUtils
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.scripting.api.EmailService
import liquibase.pro.packaged.O

import java.time.LocalDate

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*
@CompileDynamic
class AdmissionAPI extends TCSI_API {

    static final String ADMISSIONS_PATH = TCSIIntegration.BASE_API_PATH + '/course-admissions'

    private Enrolment courseAdmission
    private Course highEducation
    private EntityRelationType highEducationType
    
    AdmissionAPI(Course highEducation, EntityRelationType highEducationType, Enrolment courseAdmission, RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
        this.courseAdmission = courseAdmission
        this.highEducation = highEducation
        this.highEducationType = highEducationType
    }

    void updateAdmission (String admissionUid, String studentUID, String courseUid) {
        String message = "Update admission"
        client.request(PUT, JSON) {
            uri.path = ADMISSIONS_PATH + "/$admissionUid"
            //single JSON object
            body = admissionPacket(studentUID, courseUid)

            response.success = { resp, result ->
               handleResponce(result, message)
            }

            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    String createCourseAdmission(String studentUID, String courseUid) {
        String message = "Create admission"
        client.request(POST, JSON) {
            uri.path = ADMISSIONS_PATH
            //POST admission group - list of admissiions incliding nested 'basiss for admission' packet
            body = admissionGroup(studentUID, courseUid)

            response.success = { resp, result ->
                def admission =  handleResponce(result, message)
                return admission['course_admissions_uid'].toString()
            }

            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    String getAdmission(String studentUid, String courseUid) {
        String message = 'looking for admission'
        client.request(GET, JSON) {
            uri.path = StudentAPI.STUDENTS_PATH + "/$studentUid/course-admissions"
            response.success = { resp, result ->

                def admissions = handleResponce(result, message)
                
                def admission = admissions['course_admission']?.find { courseUid == it['courses_uid']?.toString() }

                if (admission && admission['course_admissions_uid']) {
                    return admission['course_admissions_uid'].toString()
                }
                return null
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    @CompileDynamic
    Map<String, Object> getAdmissionData(String studentsUid, String courseUid) {

        Map<String, Object> admission = [:]

        admission["students_uid"] = Long.valueOf(studentsUid)
        admission["courses_uid"] = Long.valueOf(courseUid)
        admission["course_of_study_commencement_date"] = courseAdmission.courseClass.startDateTime?.format(DATE_FORMAT)

        if (CourseClassAttendanceType.PART_TIME_ATTENDANCE ==  courseAdmission.courseClass.attendanceType) {
            admission["type_of_attendance_code"] = '2'
        } else {
            admission["type_of_attendance_code"] = '1'
        }

        if(courseAdmission.student.priorEducationCode) {
            switch (courseAdmission.student.priorEducationCode) {
                case AvetmissStudentPriorEducation.BACHELOR:
                    admission["highest_attainment_code"] = '300'
                    break
                case AvetmissStudentPriorEducation.ADVANCED_DIPLOMA:
                    admission["highest_attainment_code"] = '410'
                    break
                case AvetmissStudentPriorEducation.DIPLOMA:
                    admission["highest_attainment_code"] = '410'
                    break
                case AvetmissStudentPriorEducation.CERTIFICATE_IV:
                    admission["highest_attainment_code"] = '511'

                    break
                case AvetmissStudentPriorEducation.CERTIFICATE_III:
                    admission["highest_attainment_code"] = '514'
                    break
                case AvetmissStudentPriorEducation.CERTIFICATE_II:
                    admission["highest_attainment_code"] = '521'
                    break
                case AvetmissStudentPriorEducation.CERTIFICATE_I:
                    admission["highest_attainment_code"] = '524'
                    break
                case AvetmissStudentPriorEducation.MISC:
                case AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION:
                case AvetmissStudentPriorEducation.NONE:
                    admission["highest_attainment_code"] = '000'
                    break
            }
        } else {
            admission["highest_attainment_code"] = '000'
        }

        if (courseAdmission.studyReason) {
            switch (courseAdmission.studyReason) {
                case StudyReason.STUDY_REASON_JOB:
                    admission["study_reason_code"] = '01'
                    break
                case StudyReason.STUDY_REASON_DEVELOP_BUSINESS:
                    admission["study_reason_code"] = '02'
                    break
                case StudyReason.STUDY_REASON_START_BUSINESS:
                    admission["study_reason_code"] = '03'
                    break
                case StudyReason.STUDY_REASON_CAREER_CHANGE:
                    admission["study_reason_code"] = '04'
                    break
                case StudyReason.STUDY_REASON_BETTER_JOB:
                    admission["study_reason_code"] = '05'
                    break
                case StudyReason.STUDY_REASON_VOLUNTARY_WORK:
                    admission["study_reason_code"] = '13'
                    break
                case StudyReason.STUDY_REASON_JOB_REQUIREMENT:
                    admission["study_reason_code"] = '06'
                    break
                case StudyReason.STUDY_REASON_EXTRA_JOB_SKILLS:
                    admission["study_reason_code"] = '07'
                    break
                case StudyReason.STUDY_REASON_FOR_ANOTHER_COURSE:
                    admission["study_reason_code"] = '08'
                    break
                case StudyReason.STUDY_REASON_OTHER:
                    admission["study_reason_code"] = '11'
                    break
                case StudyReason.STUDY_REASON_PERSONAL_INTEREST:
                    admission["study_reason_code"] = '12'
                    break
                case StudyReason.STUDY_REASON_NOT_STATED:
                    admission["study_reason_code"] = '99'
                    break
            }
        } else {
            admission["study_reason_code"] = '99'
        }

        if (courseAdmission.student.labourForceStatus) {
            switch (courseAdmission.student.labourForceStatus) {

                case AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION:
                    admission["labour_force_status_code"] = '99'
                    break
                case AvetmissStudentLabourStatus.FULL_TIME:
                    admission["labour_force_status_code"] = '01'
                    break
                case AvetmissStudentLabourStatus.PART_TIME:
                    admission["labour_force_status_code"] = '02'
                    break
                case AvetmissStudentLabourStatus.SELF_EMPLOYED:
                    admission["labour_force_status_code"] = '03'
                    break
                case AvetmissStudentLabourStatus.EMPLOYER:
                    admission["labour_force_status_code"] = '04'
                    break
                case AvetmissStudentLabourStatus.UNPAID_FAMILY_WORKER:
                    admission["labour_force_status_code"] = '05'
                    break
                case AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_FULL_TIME:
                    admission["labour_force_status_code"] = '06'
                    break
                case AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_PART_TIME:
                    admission["labour_force_status_code"] = '07'
                    break
                case AvetmissStudentLabourStatus.UNEMPLOYED_NOT_SEEKING:
                    admission["labour_force_status_code"] = '08'
                    break
            }
        } else {
            admission["labour_force_status_code"] = '99'
        }


        if (courseAdmission.status == EnrolmentStatus.CANCELLED) {
            admission["course_outcome_code"] = '2'
        } else {
            //looking throuhg all unit enrolments + high education course enrolment
            List<Course> units = TCSIUtils.getUnitCourses(highEducation, highEducationType)
            List<Enrolment> allEnrolments = enrolment.student.enrolments.findAll {it.courseClass.course in units}
            allEnrolments << courseAdmission
            List<Outcome> allOutcomes = allEnrolments*.outcomes.flatten() as List<Outcome>

            // if all related outcomes in both parent and all children courses have 'successful' status
            if (!allOutcomes.any {it.status == null || !(it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE) }) {
                admission["course_outcome_code"] = '1'
                LocalDate endDate = allOutcomes.findAll {it.endDate != null}*.endDate.sort().last()
                if (endDate) {
                    admission["course_outcome_date"] =  endDate.format(DATE_FORMAT) //E592  
                }
            }
        }
        
        return admission
    }
    
    String admissionPacket(String studentsUid, String courseUid) {
        def admissionPacket = [
                'correlation_id' : "admissionGroup_${System.currentTimeMillis()}",
                'course_admission' : getAdmissionData(studentsUid, courseUid)
        ]

        return JsonOutput.toJson(admissionPacket)
    }

    String admissionGroup(String studentsUid, String courseUid) {
        Map<String, Object> admissionData = getAdmissionData(studentsUid, courseUid)
        
        String basisForAdmissionCode =  courseAdmission.getCustomFieldValue('basisForAdmissionCode').toString()
        if (basisForAdmissionCode) {
            admissionData['bases_for_admission'] = []
            def basesForAdmission = [:]
            basesForAdmission['correlation_id'] = "bases_for_admission_${System.currentTimeMillis()}"
            basesForAdmission['basis_for_admission'] = ['basis_for_admission_code' : basisForAdmissionCode]
            admissionData['bases_for_admission'] << basesForAdmission
        }

        def admissionGroup  = [
                'correlation_id' : "admissionGroup_${System.currentTimeMillis()}",
                'course_admission' : admissionData
        ]

        return JsonOutput.toJson([admissionGroup])
    }
}
