Integer count
try {
    count = Integer.valueOf(numberOfEnrolmentsText)
} catch (NumberFormatException e) {
    throw new IllegalArgumentException("Can't convert string '${count}' to Integer", e)
}
Date fromDate = LocalDateUtils.valueToDate(fromLocalDate)
Date toDate = LocalDateUtils.valueToDate(toLocalDate)
List<Student> allStudents = ObjectSelect.query(Student).select(context)
List<Student> resultStudents = allStudents.findAll { it.enrolments?.findAll { it.createdOn >= fromDate && it.createdOn <= toDate }?.size() > count }
List<Contact> contacts = resultStudents.contact
contacts.each { Contact c ->
    csv << [
            "title"                          : c.title,
            "lastName"                       : c.lastName,
            "firstName"                      : c.firstName,
            "middleName"                     : c.middleName,
            "honorific"						           : c.honorific,
            "gender"                         : c.isMale ? "M" : (c.isMale == null) ? "" : "F",
            "birthDate"                      : c.birthDate?.format("yyyy-MM-dd"),
            "company"                        : c.isCompany,
            "isStudent"                      : c.isStudent,
            "isTutor"                        : c.isTutor,
            "createdOn"                      : c.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "modifiedOn"                     : c.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "allowEmail"                     : c.allowEmail,
            "allowPost"                      : c.allowPost,
            "allowSms"                       : c.allowSms,
            "deliveryStatusEmail"            : c.deliveryStatusEmail,
            "deliveryStatusPost"             : c.deliveryStatusPost,
            "deliveryStatusSms"              : c.deliveryStatusSms,
            "homePhone"                      : c.homePhone,
            "workPhone"                      : c.workPhone,
            "mobilePhone"                    : c.mobilePhone,
            "street"                         : c.street,
            "postcode"                       : c.postcode,
            "suburb"                         : c.suburb,
            "state"                          : c.state,
            "email"                          : c.email,
            "fax"                            : c.fax,
            "uniqueCode"                     : c.uniqueCode,
            "notes"                          : c.notes,
            "message"                        : c.message,
            "studentNumber"                  : c.student?.studentNumber,
            "usi"                            : c.student?.usi,
            "usiStatus"                      : c.student?.usiStatus,
            "chessn"                         : c.student?.chessn,
            "countryOfBirth"                 : c.student?.countryOfBirth?.name,
            "townOfBirth"                    : c.student?.townOfBirth,
            "studentIndigenousStatus"        : c.student?.indigenousStatus?.displayName,
            "languageSpokenAtHome"           : c.student?.language?.name,
            "studentEnglishProficiency"      : c.student?.englishProficiency?.displayName,
            "studentHighestSchoolLevel"      : c.student?.highestSchoolLevel?.displayName,
            "studentYearSchoolCompleted"     : c.student?.yearSchoolCompleted,
            "studentPriorEducation"          : c.student?.priorEducationCode?.displayName,
            "studentIsStillAtSchool"         : c.student?.isStillAtSchool,
            "studentLabourForceStatus"       : c.student?.labourForceStatus?.displayName,
            "studentDisabilityType"          : c.student?.disabilityType?.displayName,
            "disabilitySupportRequested"     : c.student?.specialNeedsAssistance,
            "studentSpecialNeeds"            : c.student?.specialNeeds,
            "vetFEEHelpEligible"             : c.student?.feeHelpEligible,
            "citizenship"                    : c.student?.citizenship,
            "tfn"                            : c.tfn,
            "studentIsOverseasClient"        : c.student?.isOverseasClient,
            "countryOfResidency"             : c.student?.countryOfResidency?.name,
            "passportNumber"                 : c.student?.passportNumber,
            "visaType"                       : c.student?.visaType,
            "visaNumber"                     : c.student?.visaNumber,
            "visaExpiryDate"                 : c.student?.visaExpiryDate?.format("yyyy-MM-dd"),
            "overseasHealthCareNo"           : c.student?.medicalInsurance,
            "abn"                            : c.abn,
            "tutorPayrollNo"                 : c.tutor?.payrollRef,
            "tutorDateStarted"               : c.tutor?.dateStarted?.format("yyyy-MM-dd"),
            "tutorDateFinished"              : c.tutor?.dateFinished?.format("yyyy-MM-dd"),
            "workingWithChildrenCheckStatus" : c.tutor?.wwChildrenStatus,
            "workingWithChildrenCheckDate"	 : c.tutor?.wwChildrenCheckedOn?.format("yyyy-MM-dd"),
            "workingWithChildrenCheckRef"	 : c.tutor?.wwChildrenRef,
            "workingWithChildrenCheckExpiry" : c.tutor?.wwChildrenExpiry?.format("yyyy-MM-dd"),
            "tutorResume"                    : c.tutor?.resume
    ]
}