records.each { Enrolment e ->
    csv << [
            "courseCode"           : e.courseClass.course.code,
            "courseName"           : e.courseClass.course.name,
            "studentLastName"      : e.student.contact.lastName,
            "studentFirstName"     : e.student.contact.firstName,
            "studentMobilePhone"   : e.student.contact.mobilePhone,
            "studentEnrolmentDate" : e.createdOn.format("dd-MM-yyyy"),
            "classEndDateTime"     : e.courseClass.endDateTime?.format("dd-MM-yyyy", e.courseClass.timeZone),
            "studentEmail"         : e.student.contact.email
    ]
}
