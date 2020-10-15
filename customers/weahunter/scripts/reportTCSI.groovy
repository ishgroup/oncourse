import groovy.transform.CompileDynamic
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.CourseClassAttendanceType
import ish.common.types.CreditLevel
import ish.common.types.DeliveryMode
import ish.common.types.StudentCitizenship
import ish.common.types.StudentStatusForUnitOfStudy
import ish.common.types.StudyReason
import ish.math.Money
import ish.oncourse.server.cayenne.*
import groovy.json.JsonOutput

import java.time.LocalDate
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

@CompileDynamic
def format(data, length=0) {
    if (!data) {
        return null;
    }
    switch (data.class) {
        case Date:
            return data ? data.format('yyyy-MM-dd') : "01-01-1901"
    }

    if (data instanceof Date) {
        return data ? data.format('yyyy-MM-dd') : "01-01-1901"
    }
    if (data instanceof String) {
        return length < data.length() ? data[0..length] : data
    }
    if (data instanceof BigDecimal) {
        return data
    }
    if (data instanceof Integer) {
        return data
    }
    if (data instanceof LocalDate) {
        return data.format("yyyy-MM-dd")
    }
    if (data instanceof Money) {
        return data.toBigDecimal()
    }
    throw IllegalArgumentException
}

@CompileDynamic
def get_course(CourseClass course) {
    Map<String, Object> c = [:]
    c['course_code'] = format(course.id as String, 10)  // E307
    c['course_name'] = format(course.course.name, 210)   // E308
    c['course_of_study_load'] = format(course.fullTimeLoad)     // E350
    c['standard_course_duration'] = format(course.reportableHours)  // E596
    c['course_effective_from_date'] = format(course.startDateTime) // E609
    c['course_effective_to_date'] = format(course.endDateTime) // E610
//    c["course_fields_of_education"] = []
//    def fields = [:]
//    fields['course_fields_of_education_uid'] = ""
//    fields['course_field_of_education_code'] = ""
//    fields['course_field_of_education_supplementary_code'] = ""
//    fields['course_field_of_education_effective_from_date'] = ""
//    c["course_fields_of_education"] << fields
    return c
}

@CompileDynamic
def get_student(Student s) {
    def student = [:]
    student["student_identification_code"] = format(s.studentNumber as String, 10) // E313
    student["date_of_birth"] = format(s.contact.dateOfBirth)    // E314
    student["student_family_name"] = format(s.contact.lastName, 40) // E402
    student["student_given_name_first"] = format(s.contact.firstName, 40)   // E403
    //student["student given_name others"] = ""   //E404
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
    student["year_left_school"] = 9999

    student["level_left_school"] = 99
    switch (s.highestSchoolLevel) {
        case AvetmissStudentSchoolLevel.COMPLETED_YEAR_9:
            student["level_left_school"] = 9
            break
        case AvetmissStudentSchoolLevel.COMPLETED_YEAR_10:
            student["level_left_school"] = 10
            break
        case AvetmissStudentSchoolLevel.COMPLETED_YEAR_11:
            student["level_left_school"] = 11
            break
        case AvetmissStudentSchoolLevel.COMPLETED_YEAR_12:
            student["level_left_school"] = 12
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
def get_admission(Enrolment e) {
    def admission = [:]
    admission["student_identification_code"] = format(e.student.studentNumber.toString(), 10)  // E313
    admission["course_code"] = format(e.courseClass.id, 10).toString() // E307
    admission["course_of_study_commencement_date"] = format(e.courseClass.startDateTime) // E534
    switch (e.courseClass.attendanceType) {
        case CourseClassAttendanceType.FULL_TIME_ATTENDANCE:
            admission[ "type_of_attendance_code" ] = "1" // E330
            break
        case CourseClassAttendanceType.PART_TIME_ATTENDANCE:
            admission[ "type_of_attendance_code" ] = "2"
            break
    }
    admission["highest_attainment_code"] = "999"  //E620
    switch (e.creditLevel) {
        case CreditLevel.CERTIFICATE_1_LEVEL:
            admission["highest_attainment_code"] = "524"
            break
        case CreditLevel.CERTIFICATE_1_LEVEL:
            admission["highest_attainment_code"] = "524"
            break
        case CreditLevel.CERTIFICATE_2_LEVEL:
            admission["highest_attainment_code"] = "521"
            break
        case CreditLevel.CERTIFICATE_3_LEVEL:
            admission["highest_attainment_code"] = "514"
            break
        case CreditLevel.CERTIFICATE_4_LEVEL:
            admission["highest_attainment_code"] = "511"
            break
        case CreditLevel.DIPLOMA:
            admission["highest_attainment_code"] = "420"
            break
        case CreditLevel.ADVANCED_DIPLOMA:
            admission["highest_attainment_code"] = "410"
            break
    }

    admission["study_reason_code"] = "99" // E575
    switch (e.studyReason) {
        case StudyReason.STUDY_REASON_JOB:
            admission["study_reason_code"] = "01"
            break
        case StudyReason.STUDY_REASON_DEVELOP_BUSINESS:
            admission["study_reason_code"] = "02"
            break
        case StudyReason.STUDY_REASON_START_BUSINESS:
            admission["study_reason_code"] = "03"
            break
        case StudyReason.STUDY_REASON_CAREER_CHANGE:
            admission["study_reason_code"] = "04"
            break
        case StudyReason.STUDY_REASON_BETTER_JOB:
            admission["study_reason_code"] = "05"
            break
        case StudyReason.STUDY_REASON_JOB_REQUIREMENT:
            admission["study_reason_code"] = "06"
            break
        case StudyReason.STUDY_REASON_EXTRA_JOB_SKILLS:
            admission["study_reason_code"] = "07"
            break
        case StudyReason.STUDY_REASON_FOR_ANOTHER_COURSE:
            admission["study_reason_code"] = "08"
            break
        case StudyReason.STUDY_REASON_OTHER:
            admission["study_reason_code"] = "11"
            break
        case StudyReason.STUDY_REASON_PERSONAL_INTEREST:
            admission["study_reason_code"] = "12"
            break
    }
    admission["labour_force_status_code"] = "99" // E576
    switch (e.student.labourForceStatus) {
        case AvetmissStudentLabourStatus.FULL_TIME:
            admission["labour_force_status_code"] = "01"
            break
        case AvetmissStudentLabourStatus.PART_TIME:
            admission["labour_force_status_code"] = "02"
            break
        case AvetmissStudentLabourStatus.SELF_EMPLOYED:
            admission["labour_force_status_code"] = "03"
            break
        case AvetmissStudentLabourStatus.EMPLOYER:
            admission["labour_force_status_code"] = "04"
            break
        case AvetmissStudentLabourStatus.UNPAID_FAMILY_WORKER:
            admission["labour_force_status_code"] = "05"
            break
        case AvetmissStudentLabourStatus.UNEMPLOYED_NOT_SEEKING:
            admission["labour_force_status_code"] = "08"
            break
        case AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_FULL_TIME:
            admission["labour_force_status_code"] = "06"
            break
        case AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_PART_TIME:
            admission["labour_force_status_code"] = "07"
            break
    }
    if (e.outcomes) {
        //admission["course_outcome_code"] = e.outcomes?.first().code  // E599
        admission["course_outcome_date"] = format(e.outcomes?.first()?.startDate)   // E592
    }

    admission["basis_for_admission"] = []
    def basis_for_admission1 = [:]
    basis_for_admission1["basis_for_admission_code"] = "" // E327
    basis_for_admission1["bases_for_admission_uid"] = ""
    admission["basis_for_admission"] << basis_for_admission1

    admission["credit_used_value"] = ""
    admission["credit_basis_code"] = ""
    return admission
}

@CompileDynamic
def get_campus(CourseClass c) {
    def campus = [:]
    def site = c.firstSession?.room?.site
    if (!site) {
        return
    }

    campus["delivery_location_code"] = format(site.id as String)  // E625
    campus["delivery_location_suburb"] = format(c.firstSession.room.site.suburb as String, 48)    // E678
    campus["delivery_location_state"] = format(c.firstSession.room.site.state as String, 3) // E630
    if (c.firstSession.room.site.country?.saccCode ) {
        campus["delivery_location_country_code"] =
                format(c.firstSession.room.site.country?.saccCode as String, 4)  // E660
    }

    campus["campus_effective_from_date"] = format(c.firstSession.room.site.createdOn)  // E609
    campus["campus_effective_to_date"] = ""    // E610

    return campus
}

@CompileDynamic
def get_units_of_study (Module m) {
        def u = [:]
        u["unit_of_study_code"] = format(m?.id as String) // E354
        u["discipline_code"] = format(m?.fieldOfEducation) // E464
        u["unit_of_study_year_long_indicator"] = false // E622
        u["unit_of_study_effective_from_date"] = "" // E609
        u["unit_of_study_effective_to_date"] = "" // E610

    return u

}

@CompileDynamic
def get_unit_enrolment(Enrolment e) {
    def enrolments = []
    e.outcomes.each { o ->
        def numOutcomes = 1
        if (e.outcomes != null) {
            numOutcomes = e.outcomes.size()
        }
        def enrolment = [:]
        enrolment['course_admissions_uid'] = "" //
        enrolment["unit_of_study_code"] = format(o.module?.id) // E354
        enrolment["campuses_uid"] = ""
        enrolment["unit_of_study_census_date"] = format(e.courseClass.censusDate) // E489
        enrolment["discipline_code"] = format(o.module?.fieldOfEducation) // E464
        enrolment['work_experience_in_industry_code'] = "" // E337
        enrolment['summer_and_winter_school_code'] = ""
        enrolment["unit_of_study_year_long_indicator"] = false // E622
        enrolment["unit_of_study_commencement_date"] = format(e.outcomes.sort { it.startDate }.first().startDate)  // E600
        enrolment["unit_of_study_status_code"] = "" // E355
        enrolment["unit_of_study_outcome_date"] = ""    //E601
        enrolment["delivery_location_code"] = format(e.courseClass.firstSession.room.site.id as String)  // E625
        enrolment['delivery_location_postcode'] = ""    // E477
        enrolment["course_assurance_indicator"] = "" // E619
        enrolment['recognition_of_prior_learning_code'] = ""    // E577
        enrolment["mode_of_attendance_code"] = "3"   // E329
        switch (e.courseClass.deliveryMode) {
            case DeliveryMode.CLASSROOM:
                enrolment["mode_of_attendance_code"] = "1"
                break
            case DeliveryMode.WA_LOCAL_CLASS:
                enrolment["mode_of_attendance_code"] = "1"
                break
            case DeliveryMode.ONLINE:
                enrolment["mode_of_attendance_code"] = "2"
                break
            case DeliveryMode.WA_INTERNET_SITE:
                enrolment["mode_of_attendance_code"] = "2"
                break
            case DeliveryMode.WA_REMOTE_CLASS:
                enrolment["mode_of_attendance_code"] = "2"
                break
            case DeliveryMode.WA_VIDEO_LEARNING:
                enrolment["mode_of_attendance_code"] = "2"
                break
            case DeliveryMode.WORKPLACE:
                enrolment["mode_of_attendance_code"] = "6"
                break
        }
        enrolment['student_status_code'] = "" // E490
        switch (e.feeStatus) {
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "410"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "404"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "401"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "409"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "405"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT:
                enrolment["student_status_code"] = "502"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "406"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_TASMANIA_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "408"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "403"   // E490
                break
            case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED:
                enrolment["student_status_code"] = "407"   // E490
                break
        }
        enrolment['units_of_study_uid'] = ""
        enrolment["maximum_student_contribution_code"] = ""
        enrolment["eftsl"] = new BigDecimal(format(e.courseClass.fullTimeLoad))
        enrolment["amount_charged"] = ""    // E384
        enrolment["amount_paid_upfront"] = format(e.courseClass.deposit / numOutcomes)  // E381
        enrolment["loan_fee"] = ""  // E529
        enrolment["help_loan_amount"] = format(e.feeHelpAmount / numOutcomes)  // E558
        enrolment["remission_reason_code"] = "" // E446

        enrolments << enrolment
    }

    return enrolments
}


// Authentication process
// 1. device activation. This step uploads public key to PRODA
// 2. Authorisation. Use private key to make authorisation request. The response contains the access-token
// 3. Use access token for api requests

def activate_device() {
// activate proda device refer to PRODA B2B Software developers guide at files.ish.com.au:/dish/Development/TCSI
// device need to be activated before any api request can be made. The response body contains the expiration date
// public key needs to be updated before expiration date

    ACTIVATION_URL = 'https://test.5.rsp.humanservices.gov.au/piaweb/api/b2b/v1/devices/ishoncourse_device/jwk'

    def client = new RESTClient(ACTIVATION_URL)
    client.headers['dhs-auditId'] = '2742144277'
    client.headers['dhs-auditIdType'] = 'http://humanservices.gov.au/PRODA/device'
    client.headers['dhs-subjectId'] = 'ishoncourse_device'
    client.headers['dhs-subjectIdType'] = 'http://humanservices.gov.au/PRODA/device'
    client.headers['dhs-messageId'] = 'urn:uuid:4edf9afb-d6cd-4866-bf09-cf600ae87a3b'
    client.headers['dhs-correlationId'] = 'uuid:423083be-6445-4968-9fe6-dd83bcd08626'
    client.headers['dhs-productId'] = '858d06ed-7fbe-423c-be45-ac5742cf137c'
    def request_body = [
            otac: 'k9kZWBZ3AP',
            key: [
                    alg: 'RS256',
                    e: 'AQAB',
                    n: 'base64:LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFzZ0dpSmMxTlpBUk8vbWNhbFFBdwp1OXlzL2d4elJEdEM5NmxMajhsbjJ4dmwrRmNEdHhxVW55ajFrMHNLVmFLMkZ2Nk9Ia0M5NzdJbFZNSVdsb3haCkV4cVNjcmJUVWNTK0Q2U1NvMUtRV05rN0E3d2R5aDVqM1ArRDkyZHg4UngzOHJ1bnhrSGRXS2ZlUU9wU0xHR2cKVWhzUjR6bTFLbzlQTlNNbzhyYTY4eWpZR2V5N1ByUWZZazFqSmsvR0s1cG9tcEVyVDR3YnBnSUM4Z09Qb1h1WgpGVWJTZTlwLzR1TGJnVTJaK1hYNm5rRlJyK0lBbXJpWEZlekdERUt3M25kcXlzeDFSdTBqQU4xVE9hYzJ6QVVvCitYbGt2bHFldFpMNTVnaEVqSVJkWkpYZGVLQmpMand0ekwvczVtRkJRd2diYXU0Q1o0d0RUbGg4b1Y2REcxRnUKQ1FJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==',
                    // public key
                    kty: 'RSA',
                    use: 'sig',
                    kid: 'ishoncourse_device',
                    orgId: '2742144277'
            ]


    ]
    client.request(Method.POST, ContentType.Json) {
        body = data
        response.success = { resp, result ->
            return result
        }
    }
}

class TCSIAPI {
    // developer portal: https://test.developer.api.humanservices.gov.au
    static BASE_URL = "https://test.api.humanservices.gov.au/centrelink/ext-vend/tcsi/b2g/v1"
    RESTClient client

    TCSIAPI(username, token) {
        Date currentDate = new Date();
        def timestamp = (currentDate.getTime() / 1000).toString()
        client = new RESTClient(BASE_URL)
        client.headers["date-timestamp"] = timestamp
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"
        client.headers['x-ibm-client-id'] = "858d06ed-7fbe-423c-be45-ac5742cf137c" // client id in developer portal
        client.headers['organisation-id'] = "2742144277"
        client.headers['organisation-name'] = "ish onCourse"
        client.headers['provider-type'] = "VET"
        client.headers['user-name'] = username
        client.headers['message-id'] = timestamp.toString()
        client.headers['software-name'] = "onCourse"
        client.headers['software-version'] = '9.15'
        client.headers['access-token'] = token // PRODA token
        client.headers['tcsi-omit-links'] = true

        this.client = client
    }

}


def upload_data(endpoint, data) {
    Date currentDate = new Date();
    def correlation_id = (currentDate.getTime() / 1000).toString()
    def payload = [:]
    payload['correlation_id'] = correlation_id
    payload['courses'] = data
    def api = new TCSIAPI('ish', 'xxxx')

    api.client.request(Method.POST, ContentType.JSON) {
        uri.path = endpoint
        body = data
        response.success = { resp, result ->
            return result
        }
    }
}


def admissions = []
def loans = []
def units_of_study = []

List<Enrolment> result
List<Enrolment> enrolments = result

def courses = enrolments*.courseClass.flatten().unique().collect{ get_course(it) }
def campuses = enrolments*.courseClass.flatten().unique().collect{ get_campus(it)}
def students = enrolments*.student.flatten().unique().collect{ get_student(it)}
def units = enrolments*.outcomes*.module.flatten().collect{ get_units_of_study(it) }

enrolments.each { e ->

    admissions << get_admission(e)
    loans = loans + get_unit_enrolment(e)
}

upload_data('/courses', courses)
upload_data('/campuses', campuses)
upload_data('/students', students)
upload_data('/admissions', admissions)
upload_data('/loans', loans)

email {
    to "tao@ish.com.au"
    subject "TCSI"
    content JsonOutput.toJson(admissions)
}
