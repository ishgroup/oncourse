/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.CourseClassAttendanceType
import ish.common.types.DeliveryMode
import ish.common.types.EnrolmentStatus
import ish.common.types.Gender
import ish.common.types.OutcomeStatus
import ish.common.types.RecognitionOfPriorLearningIndicator
import ish.common.types.StudentCitizenship
import ish.common.types.StudentStatusForUnitOfStudy
import ish.common.types.StudyReason
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
import org.apache.commons.lang3.StringUtils

import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate


class TCSIUtils {
    
    static final String DATE_FORMAT='yyyy-MM-dd'
    
    @CompileDynamic
    static String getCourseData(Course c, EntityRelationType highEducationType) {
        Map<String, Object> course = [:]

        course["course_code"] = c.code
        course["course_name"] = c.name

        List<Course> units = getUnitCourses(c, highEducationType)
        BigDecimal studyLoad = BigDecimal.ZERO
        units*.fullTimeLoad.findAll { StringUtils.trimToNull(it) && it.number}.each {
            studyLoad += new BigDecimal(it)
        }
        
        if (StringUtils.trimToNull(c.fullTimeLoad) && c.fullTimeLoad.number) {
            studyLoad += new BigDecimal(c.fullTimeLoad)
        }
        course["course_of_study_load"] = studyLoad.setScale(2, RoundingMode.UP)
        
        List<CourseClass> classes = (units*.courseClasses.flatten() as List<CourseClass>).sort { CourseClass clazz -> clazz.startDateTime}
        
        course["course_effective_from_date"] = (classes.first().startDateTime?:new Date()).format(DATE_FORMAT)
        course["course_effective_to_date"] = (classes.last().endDateTime?:new Date()).format(DATE_FORMAT)

        
        Long durationDays = 0
        classes.groupBy {it.course}.each { k, v ->
            CourseClass clazz = v.find { it.startDateTime && it.endDateTime }
            if (clazz) {
                durationDays += Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant()).toDays()
            }
        }
        
        course["standard_course_duration"] =  new BigDecimal(durationDays/365).setScale(2, RoundingMode.UP) 
        
        def courseData  = [
                'correlation_id' : "courseData_${System.currentTimeMillis()}",
                'course' : course
        ]

        return JsonOutput.toJson([courseData])
        
    }
    
    @CompileDynamic
    static String getAdmissionData(Student student, Course highEducation, EntityRelationType highEducationType, Enrolment courseAdmission, String studentsUid, String courseUid) {
        
        Map<String, Object> admission = [:]

        admission["students_uid"] = Long.valueOf(studentsUid)
        admission["courses_uid"] = Long.valueOf(courseUid)
        admission["course_of_study_commencement_date"] = courseAdmission.courseClass.startDateTime?.format(DATE_FORMAT)
        
        if (CourseClassAttendanceType.FULL_TIME_ATTENDANCE ==  courseAdmission.courseClass.attendanceType) {
            admission["type_of_attendance_code"] = '1'
        } else {
            admission["type_of_attendance_code"] = '2'
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
            List<Course> units = getUnitCourses(highEducation, highEducationType)
            List<Enrolment> allEnrolments = student.enrolments.findAll {it.courseClass.course in units}
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
        unit["course_admissions_uid"] = Long.valueOf(admissionUid)
        unit["unit_of_study_code"] =  clazz.course.code
        if (campuseUid) {
            unit["campuses_uid"] = Long.valueOf(campuseUid)
        }
        if (clazz.censusDate) {
            unit["unit_of_study_census_date"] = clazz.censusDate.format(DATE_FORMAT)
        }
        if (!enrolmentUnit.outcomes.empty  && enrolmentUnit.outcomes[0].module?.fieldOfEducation ) {
            unit["discipline_code"] =enrolmentUnit.outcomes[0].module?.fieldOfEducation
        }
        
        if (clazz.startDateTime && clazz.endDateTime) {
            unit["unit_of_study_year_long_indicator"] = (Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant()).toDays() > 300)
        }
        LocalDate strtDate = enrolmentUnit.outcomes.findAll {it.startDate}*.startDate.sort().first()
        if (strtDate) {
            unit["unit_of_study_commencement_date"] = strtDate.format(DATE_FORMAT)
        }
        if (clazz.course.fullTimeLoad) {
            try {
                unit['eftsl'] = new BigDecimal(clazz.course.fullTimeLoad)
            } catch  (Exception ignor) {
                unit['eftsl'] = 0
            }
        }

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
            unit["unit_of_study_outcome_date"] = endDate.format(DATE_FORMAT) //E601 
        }

        unit["course_assurance_indicator"] = false

        if (clazz.deliveryMode) { //E329
            switch (clazz.deliveryMode) {
                case DeliveryMode.CLASSROOM:
                case DeliveryMode.CLASSROOM_AND_WORKSPACE:
                    unit["mode_of_attendance_code"] = '1' 
                    break
                case DeliveryMode.WORKPLACE:
                    unit["mode_of_attendance_code"] = '6'
                    break
                default:
                    unit["mode_of_attendance_code"] = '3'
                    break
            }
        } else {
            unit["mode_of_attendance_code"] = '3'
        }
        
        
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
        
        BigDecimal feeCharged =  enrolmentUnit.invoiceLines.empty ?
                Money.ZERO.toBigDecimal() :
                enrolmentUnit.originalInvoiceLine.priceTotalIncTax.toBigDecimal()
        unit["amount_charged"] = feeCharged
        unit["amount_paid_upfront"] = feeCharged
        if (enrolmentUnit.feeHelpAmount) {
            unit["loan_fee"] =  enrolmentUnit.feeHelpAmount.multiply(0.2).toBigDecimal()
            unit["help_loan_amount"] =  enrolmentUnit.feeHelpAmount.toBigDecimal()
        }
        if (enrolmentUnit.creditTotal) {
            switch (enrolmentUnit.creditTotal) {
                case RecognitionOfPriorLearningIndicator.NOT_RPL_UNIT_OF_STUDY:
                    break
                case RecognitionOfPriorLearningIndicator.UNIT_OF_STUDY_CONSISTS_WHOLLY_OF_RPL:
                    unit["recognition_of_prior_learning_code"]='1'
                    break
                case RecognitionOfPriorLearningIndicator.UNIT_OF_STUDY_HAS_A_COMPONENT_OF_RPL:
                    unit["recognition_of_prior_learning_code"]='2'
                    break
            }
        }
        
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
