/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { Contact c ->
  csv << [
      "title"                          : c.title,
      "lastName"                       : c.lastName,
      "firstName"                      : c.firstName,
      "middleName"                     : c.middleName,
      "honorific"            : c.honorific,
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
      "countryOfResidency"             : c.student?.countryOfResidency,
      "passportNumber"                 : c.student?.passportNumber,
      "visaType"                       : c.student?.visaType,
      "visaNumber"                     : c.student?.visaNumber,
      "visaExpiryDate"                 : c.student?.visaExpiryDate?.format("yyyy-MM-dd"),
      "overseasHealthCareNo"           : c.student?.medicalInsurance,
      "abn"                            : c.abn,
      "tutorPayrollNo"                 : c.tutor?.payrollRef,
      "tutorDateStarted"               : c.tutor?.dateStarted?.format("yyyy-MM-dd"),
      "tutorDateFinished"              : c.tutor?.dateFinished?.format("yyyy-MM-dd"),
      "tutorResume"                    : c.tutor?.resume,
      "LibraryMembership"              : c.customFields.find { cf -> cf.customFieldType.key  == "libraryMembership" }?.value,
      "LibraryMembershipExpiry"        : c.customFields.find { cf -> cf.customFieldType.key == "LibraryMembershipExp"}?.value,
  ]
}
