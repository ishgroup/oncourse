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
import ish.common.types.EnrolmentStatus
import ish.common.types.Gender
import ish.common.types.OutcomeStatus
import ish.common.types.StudentCitizenship
import ish.common.types.StudentStatusForUnitOfStudy
import ish.math.Money
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import java.time.Duration
import java.time.LocalDate


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
    static String getCourseData(Course c, EntityRelationType highEducationType) {
        Map<String, Object> course = [:]

        course["course_code"] = c.code
        course["course_name"] = c.name

        course["course_of_study_load"]  = c.fullTimeLoad?:'0'
        
        List<Course> units = getUnitCourses(c, highEducationType)
        units << c

        List<CourseClass> classes = (units*.courseClasses.flatten() as List<CourseClass>).sort { CourseClass clazz -> clazz.startDateTime}
        
        course["course_effective_from_date"] = (classes.first().startDateTime?:new Date()).format(DATE_FORMAT)
        course["course_effective_to_date"] = (classes.last().endDateTime?:new Date()).format(DATE_FORMAT)

        
        Duration duration = Duration.ZERO
        classes.groupBy {it.course}.each { k, v ->
            CourseClass clazz = v.find { it.startDateTime && it.endDateTime }
            if (clazz) {
                duration += Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant())
            }
        }
        
        course["standard_course_duration"] =  String.format("%.1f", duration.toDays() / 365) 
        
        def courseData  = [
                'correlation_id' : "courseData_${System.currentTimeMillis()}",
                'course' : course
        ]

        return JsonOutput.toJson([courseData])
        
    }
    
    @CompileDynamic
    static String getAdmissionData(Student student, Course highEducation, EntityRelationType highEducationType, Enrolment courseAdmission, String studentsUid, String courseUid) {
        
        Map<String, Object> admission = [:]

        admission["students_uid"] = studentsUid
        admission["courses_uid"] = courseUid
        admission["course_of_study_commencement_date"] = courseAdmission.courseClass.startDateTime?.format(DATE_FORMAT)
        admission["type_of_attendance_code"]  = courseAdmission.courseClass.attendanceType.databaseValue.toString()

        admission["highest_attainment_code"] = courseAdmission.student.priorEducationCode.toString()
        admission["study_reason_code"] = courseAdmission.studyReason.databaseValue.toString()
        admission["labour_force_status_code"] = courseAdmission.student.labourForceStatus.toString()
        
        if (courseAdmission.status == EnrolmentStatus.CANCELLED) {
            admission["course_outcome_code"] = '2'  
        } else {
            //looking throuhg all unit enrolments + high education course enrolment
            List<Course> units = getUnitCourses(highEducation, highEducationType)
            List<Enrolment> allEnrolments = student.enrolments.findAll {it.courseClass.course in units}
            allEnrolments << courseAdmission
            List<Outcome> allOutcomes = allEnrolments*.outcomes.flatten() as List<Outcome>
            
            // if all related outcomes in both parent and all children courses have 'successful' status
            if (!allOutcomes.any {it.status == null || !(it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE) }) {
                admission["course_outcome_code"] = '1'
                LocalDate endDate = allOutcomes.findAll {it.endDate != null}*.endDate.sort().last()
                if (endDate) {
                    admission["course_outcome_date"] =  endDate.format(DATE_FORMAT)
                }
            }
            
            
            admission["course_outcome_code"] = '1'
            LocalDate endDate = courseAdmission.outcomes.findAll {it.endDate != null}*.endDate.sort().reverse().first()
            if (endDate) {
                admission["course_outcome_date"] =  endDate.format(DATE_FORMAT)
            }
        }

        String basisForAdmissionCode =  courseAdmission.getCustomFieldValue('basisForAdmissionCode').toString()
        if (basisForAdmissionCode) {
            admission['bases_for_admission'] = []
            def basesForAdmission = [:]
            basesForAdmission['correlation_id'] = "bases_for_admission_${System.currentTimeMillis()}"
            basesForAdmission['basis_for_admission'] = ['basis_for_admission_code' : basisForAdmissionCode]
            admission['bases_for_admission'] << basesForAdmission
        }

        def courseData  = [
                'correlation_id' : "admissionData_${System.currentTimeMillis()}",
                'course_admission' : admission
        ]

        return JsonOutput.toJson([courseData])
    }

    @CompileDynamic
    static String getCampusData (Site site) {
        Map<String, Object> campus = [:]

        campus["delivery_location_code"] = site.id.toString()
        campus["campus_effective_from_date"] = '2000-01-01'
        campus["delivery_location_street_address"] = site.street
        campus["delivery_location_suburb"] = site.suburb
        campus["delivery_location_country_code"] = site.country?.saccCode?.toString()
        campus["delivery_location_postcode"] = site.postcode    
        campus["delivery_location_state"] = site.state
        
        
        def campuseData  = [
                'correlation_id' : "campuse_${System.currentTimeMillis()}",
                'campus' : campus
        ]

        return JsonOutput.toJson(campuseData)
    }


    @CompileDynamic
    static String testCampusData () {
        Map<String, Object> campus = [:]

        campus["delivery_location_code"] = '201'
        campus["campus_effective_from_date"] = '2000-01-01'
        campus["delivery_location_street_address"] ='Suite 302, Level 3, 468 George Street'
        campus["delivery_location_suburb"] ='Sydney'
        campus["delivery_location_country_code"] = '1101'
        campus["delivery_location_postcode"] = '2000'
        campus["delivery_location_state"] = 'NSW'


        def campuseData  = [
                'correlation_id' : "campuse_${System.currentTimeMillis()}",
                'campus' : campus
        ]

        return JsonOutput.toJson(campuseData)
    }
    
    
    
    @CompileDynamic
    static String getUnitData(Enrolment enrolmentUnit, String admissionUid, String campuseUid) {
        Map<String, Object> unit = [:]
        CourseClass clazz = enrolmentUnit.courseClass
        unit["course_admissions_uid"] = admissionUid
        unit["unit_of_study_code"] =  clazz.course.code
        unit["campuses_uid"] = campuseUid
        unit["unit_of_study_census_date"] = clazz.censusDate?.format(DATE_FORMAT)
        unit["discipline_code"] = clazz.course.qualification?.fieldOfEducation
        if (clazz.startDateTime && clazz.endDateTime) {
            unit["unit_of_study_year_long_indicator"] = Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant()).toDays() > 300
        }
        LocalDate strtDate = enrolmentUnit.outcomes.findAll {it.startDate}*.startDate.sort().first()
        if (strtDate) {
            unit["unit_of_study_commencement_date"] = strtDate.format(DATE_FORMAT)
        }
        unit['eftsl'] = clazz.course.fullTimeLoad


        if (enrolmentUnit.status == EnrolmentStatus.CANCELLED) {
            unit["unit_of_study_status_code"] = "1" 
        } else {
            if (enrolmentUnit.outcomes.any {(it.endDate?.isAfter(LocalDate.now())) || !(it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE)}) {
                unit["unit_of_study_status_code"] = "3"
            } else {
                unit["unit_of_study_status_code"] = "4"
            }
        }
        

        LocalDate endDate = enrolmentUnit.outcomes.findAll {it.endDate}*.endDate.sort().last()
        if (endDate) {
            unit["unit_of_study_outcome_date"] = endDate.format(DATE_FORMAT)
        }

        unit["course_assurance_indicator"] = false
        unit["mode_of_attendance_code"] = enrolmentUnit.attendanceType?.databaseValue?.toString()
        
        if (enrolmentUnit.feeStatus) {
            switch (enrolmentUnit.feeStatus)  {
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "401"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT:
                    unit["student_status_code"] = "402"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "403"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "404"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "405"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "406"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "407"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_TASMANIA_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "408"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "409"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "410"
                    break
            }
        }
        
        String feeCharged =  enrolmentUnit.invoiceLines.empty ?
                Money.ZERO.toPlainString() :
                enrolmentUnit.originalInvoiceLine.priceTotalIncTax.toPlainString()
        unit["amount_charged"] = feeCharged
        unit["amount_paid_upfront"] = feeCharged
        if (enrolmentUnit.feeHelpAmount) {
            unit["loan_fee"] =  enrolmentUnit.feeHelpAmount.multiply(0.2).toPlainString()
            unit["help_loan_amount"] =  enrolmentUnit.feeHelpAmount.toPlainString()
        }
        unit["recognition_of_prior_learning_code"] = enrolmentUnit.creditType.getDatabaseValue().toString()
        
        def unitData  = [
                'correlation_id' : "unit_${System.currentTimeMillis()}",
                'unit_enrolment' : unit
        ]
        
        return JsonOutput.toJson([unitData])
    }



    static List<Course> getUnitCourses(Course hihgEducation, EntityRelationType highEducationType) {
        ObjectContext context =  hihgEducation.context
        List<EntityRelation> unitRelations = ObjectSelect.query(EntityRelation)
                .where(EntityRelation.RELATION_TYPE.eq(highEducationType))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(hihgEducation.id))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .select(context)

        return unitRelations
                .collect { SelectById.query(Course, it.toRecordId).selectOne(context) }

    }

    static Course getHighEducation(ObjectContext context, EntityRelationType highEducationType, Enrolment enrolment) {
        Course course = enrolment.courseClass.course

        EntityRelation relation = ObjectSelect.query(EntityRelation)
                .where(EntityRelation.RELATION_TYPE.eq(highEducationType))
                .and(EntityRelation.TO_ENTITY_ANGEL_ID.eq(course.id))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .selectFirst(context)
        
        if (relation) {
            return SelectById.query(Course, relation.fromRecordId).selectOne(context)
        } else {
            //check if course is high education itself 
            relation = ObjectSelect.query(EntityRelation)
                    .where(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(course.id))
                    .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                    .and(EntityRelation.RELATION_TYPE.eq(highEducationType)).selectFirst(context)
            if (relation) {
                return course
            }
        }
        return null
    }
    
    static String testCourse() {
        Map<String, Object> course = [:]

        course["course_code"] = 'V5SHBBEA02'
        course["course_name"] = 'Community Services 6'
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
