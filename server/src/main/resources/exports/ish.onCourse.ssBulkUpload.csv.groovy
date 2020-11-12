import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.DeliveryMode
import ish.common.types.StudentCitizenship
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.ContactRelation
import ish.oncourse.server.cayenne.CourseClass

import java.time.LocalDate

records.each { Application a ->
    CourseClass courseClass = a.course.courseClasses.findAll{ cc -> cc.firstSession?.startDatetime > new Date() }.sort { cc -> cc.firstSession?.startDatetime }[0]
    ContactRelation relation = a.student.contact.fromContacts.find { r -> r.relationType.fromContactName.equalsIgnoreCase('employer') }

    csv << 	[
            'National_Provider_ID'						: preference.avetmiss.identifier,
            'Provider_Student_ID'						: a.student.studentNumber,
            'Enquiry_Or_Notification'					: 1,
            'Activity_Period_ID'						: LocalDate.now().getYear() - 2014,
            'Region'									: null,
            'Prog_Stream'								: null,
            'Nat_Qual_Code'								: a.course.qualification?.nationalCode,
            'First_Name'								: a.student.contact.firstName,
            'Surname'									: a.student.contact.lastName,
            'Other_Name'								: a.student.contact.middleName,
            'DOB'										: a.student.contact.birthDate?.format('dd/MM/yyyy'),
            'Gender'									: a.student.contact.gender == null ? 'X' : a.student.contact.gender.databaseValue == 1 ? 'M' : 'F',
            'Lives_in_NSW'								: a.student.contact.state == 'NSW' ? 1 : 0,
            'Residential_Postcode'						: a.student.contact.postcode,
            'Residential_Suburb'						: a.student.contact.suburb,
            'Still_At_School'							: a.student.isStillAtSchool ? 1 : 0,
            'Residency_Status'							: get_Residency_Status(a.student.citizenship),
            'Qual_Since_2017'							: get_Qual_Since_2017(a.student.priorEducationCode),
            'Highest_Post_School_Qual'					: get_Highest_Post_School_Qual(a.student.priorEducationCode),
            'Apprentice_Trainee'						: 0,
            'Apprentice_Trainee_Type'					: null,
            'Work_in_NSW'								: get_Work_in_NSW(relation),
            'Employer_Org_Name'							: get_Employer_Org_Name(relation),
            'Org_postcode'								: get_Org_Postcode(relation),
            'Org_subrub'								: get_Org_Suburb(relation),
            'ATSI'										: get_ATSI(a.student.indigenousStatus),
            'Another_SS_Qual'							: 0,
            'Disability_Status'							: null,
            'Disability_Assess_Type'					: null,
            'Welfare_Status'							: null,
            'Welfare_Type'								: null,
            'Planned_Start_Date'						: courseClass?.startDateTime?.format('dd/MM/yyyy'),
            'Delivery_Mode'								: get_Delivery_Mode(courseClass?.deliveryMode),
            'LTU_Evidence'								: 0,
            'Planned_End_Date'							: courseClass?.endDateTime?.format('dd/MM/yyyy'),
            'Unique_Student_ID'							: a.student.usi,
            'ESP_Client'								: 0,
            'ESP_Org_ID'								: null,
            'Client_ID'									: null,
            'Referred_by_ESP'							: null,
            'ESP_Referral_ID'							: null,
            'Confirmed'									: 1,
            'In_Social_Housing_Register_Or_Wait_List' 	: 0,
            'PAS_No.'									: null,
            'Waiver_Strategy'							: null,
            'Fee_Or_Waiver_Code'						: null,
            'Training_Location_Postcode'				: courseClass?.room?.site?.postcode,
            'Training_Location_Suburb'					: courseClass?.room?.site?.suburb,
            'Training_Location_Region'					: null,
            'Residential_Address'						: [a.student.contact.street, a.student.contact.suburb, a.student.contact.state, a.student.contact.postcode].findAll().join(',')
    ]
}

static def get_Residency_Status(StudentCitizenship citizenship) {
    switch(citizenship) {
        case StudentCitizenship.AUSTRALIAN_CITIZEN:
            return 1
        case StudentCitizenship.STUDENT_WITH_PERMANENT_VISA:
            return 2
        case StudentCitizenship.STUDENT_WITH_TEMPORARY_ENTRY_PERMIT:
            return 3
        case StudentCitizenship.NEW_ZELAND_CITIZEN:
            return 4
        default:
            return null
    }
}

static def get_Qual_Since_2017(AvetmissStudentPriorEducation code) {
    switch(code) {
        case AvetmissStudentPriorEducation.MISC:
        case AvetmissStudentPriorEducation.CERTIFICATE_I:
        case AvetmissStudentPriorEducation.CERTIFICATE_II:
            return 0
        default:
            return 2
    }
}

static def get_Highest_Post_School_Qual(AvetmissStudentPriorEducation code) {
    switch (code) {
        case AvetmissStudentPriorEducation.CERTIFICATE_I:
            return 2
        case AvetmissStudentPriorEducation.CERTIFICATE_II:
            return 3
        case AvetmissStudentPriorEducation.CERTIFICATE_III:
            return 4
        case AvetmissStudentPriorEducation.CERTIFICATE_IV:
        case AvetmissStudentPriorEducation.DIPLOMA:
        case AvetmissStudentPriorEducation.ADVANCED_DIPLOMA:
        case AvetmissStudentPriorEducation.BACHELOR:
            return 5
        default:
            return null
    }
}

static def get_Work_in_NSW(ContactRelation relation) {
    return relation?.fromContact?.state == 'NSW' ? 1 : 0
}

static def get_Employer_Org_Name(ContactRelation relation) {
    return relation?.fromContact?.fullName
}

static def get_Org_Postcode(ContactRelation relation) {
    return relation?.fromContact?.postcode
}

static def get_Org_Suburb(ContactRelation relation) {
    return relation?.fromContact?.suburb
}


static def get_ATSI(AvetmissStudentIndigenousStatus status) {
    switch (status) {
        case AvetmissStudentIndigenousStatus.ABORIGINAL:
        case AvetmissStudentIndigenousStatus.TORRES:
        case AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES:
            return 1
        default:
            return 0
    }
}

static def get_Delivery_Mode(DeliveryMode deliveryMode) {
    switch(deliveryMode) {
        case DeliveryMode.CLASSROOM:
        case DeliveryMode.NOT_SET:
        case DeliveryMode.WA_LOCAL_CLASS:
            return 10
        case DeliveryMode.ONLINE:
        case DeliveryMode.WA_EXTERNAL:
        case DeliveryMode.WA_INTERNET_SITE:
        case DeliveryMode.WA_REMOTE_CLASS:
        case DeliveryMode. WA_SELF_PACED_SCHEDULED:
        case DeliveryMode.WA_SELF_PACED_UNSCHEDULED:
        case DeliveryMode.WA_VIDEO_LEARNING:
            return 20
        case DeliveryMode.WA_WORKPLACE:
        case DeliveryMode.WORKPLACE:
            return 30
        case DeliveryMode.CLASSROOM_AND_ONLINE:
        case DeliveryMode.CLASSROOM_AND_WORKSPACE:
        case DeliveryMode.CLASSROOM_ONLINE_AND_WORKSPACE:
        case DeliveryMode.NA:
        case DeliveryMode.ONLINE_AND_WORKSPACE:
        case DeliveryMode.OTHER:
            return 40
        default:
            return null
    }
}
