records*.successAndQueuedEnrolments.flatten().each  { Enrolment e ->
    csv << [                    
            "Class"                                     : e.courseClass.uniqueCode,
            "First name"                                : e.student.contact.firstName,
            "Last name"                                 : e.student.contact.lastName,
            "Mobile phone"                              : e.student.contact.mobilePhone ?: "",
            "Age"                                       : e.student.contact.age ?: "",
            "Dietary Requirements"                      : e.student.specialNeeds ?: "",
            "After school walk bus"                     : e.customFields.find { cf -> cf.customFieldType.key == "Bus" }?.value,
            "School attended"                           : e.customFields.find { cf -> cf.customFieldType.key == "School" }?.value,
            "How did you find out about this course"    : e.customFields.find { cf -> cf.customFieldType.key == "Howdidyoufindoutaboutcourse" }?.value,
            "Image Permission"                          : e.customFields.find { cf -> cf.customFieldType.key == "Authoritytouseimages" }?.value,
            "Sign in"                                   : "",
            "Sign out"                                  : ""
    ]
}