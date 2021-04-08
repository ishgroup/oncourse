if (!record || record.isDistantLearningCourse) {
    return
}
if (record?.firstSession?.tutors?.empty) {
    return
}

Tutor ccTutor = record.firstSession.tutors[0]
List<Enrolment> enrolmentList =  new ArrayList<>()

def today = new Date().clearTime()
def eighteenMonths = today.minus(548)  // 18 months ago

/**
 * Iterate over a list of classes the tutor has taught over the last 18 months
 */
List<Student> students = ccTutor.courseClasses.findAll { cc ->  cc.course.id != record.course.id &&
        cc.firstSession?.startDatetime?.after(eighteenMonths) &&
        cc.firstSession?.startDatetime?.before(today) &&
        cc.successAndQueuedEnrolments.size() > 0
}*.enrolments.flatten().findAll { e -> EnrolmentStatus.SUCCESS == e.status }*.student.flatten().unique()


/**
 * Exclude students that are already enrolled to marketingClass or any class with the same courseId
 */
students.removeAll { s ->
    s.enrolments.any { e -> EnrolmentStatus.SUCCESS == e.status && e.courseClass.course.id == record.course.id }
}

/**
 * Once you have a non-duped student list, notify all students in the list
 */
records = students*.contact.flatten()
message {
    template alertStudentsTemplate
    record records
    tutor ccTutor.contact
    courseClass record
    key "alert students of related classes", record
    keyCollision "drop"
}

message {
    to preference.email.admin
    subject 'marketing: students updated about upcoming class'
    content "${students.size()} prior students who attended ${ccTutor.getContact().getName(true)}\'s were notified about the upcoming class ${record?.firstSession?.startDatetime?.format("dd/MM/yyyy")} ${record.course.name}."
}