import ish.oncourse.server.cayenne.Survey

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Survey s ->
        survey(id: s.id) {
            createdOn(s.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            modifiedOn(s.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            publicComment(s.publicComment)
            comment(s.comment)
            courseScore(s.courseScore)
            tutorScore(s.tutorScore)
            venueScore(s.venueScore)
            netPromoterScore(s.netPromoterScore)
            testimonial(s.testimonial)
            studentName(s.enrolment?.student?.fullName)
            courseName(s.enrolment?.courseClass?.course?.name)
            courseClassUniqueCode(s.enrolment?.courseClass?.uniqueCode)
            roomName(s.enrolment?.courseClass?.room?.name)
            siteName(s.enrolment?.courseClass?.room?.site?.name)

            s.enrolment?.courseClass?.tutorRoles?.each {t ->
                tutor(id: t.id) {
                    tutorName(t.tutor?.contact?.fullName)
                }
            }
        }
    }
}
