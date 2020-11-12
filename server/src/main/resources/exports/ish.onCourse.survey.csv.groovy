import ish.oncourse.server.cayenne.Survey

records.each { Survey s ->
    csv << [
            "createdOn"                         : s.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "modifiedOn"                        : s.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "publicComment"                     : s.publicComment,
            "comment"                           : s.comment,
            "courseScore"                       : s.courseScore,
            "tutorScore"                        : s.tutorScore,
            "venueScore"                        : s.venueScore,
            "netPromoterScore"                  : s.netPromoterScore,
            "testimonial"                       : s.testimonial,
            "studentName"                       : s.enrolment?.student?.fullName,
            "courseName"                        : s.enrolment?.courseClass?.course?.name,
            "courseClassUniqueCode"             : s.enrolment?.courseClass?.uniqueCode,
            "roomName"                          : s.enrolment?.courseClass?.room?.name,
            "siteName"                          : s.enrolment?.courseClass?.room?.site?.name,
            "tutorName"                         : s.enrolment?.courseClass?.tutorRoles?.collect { tr -> tr?.tutor.fullName }.join("/ "),
            "status"                            : s.visibility?.displayName
    ]
}
